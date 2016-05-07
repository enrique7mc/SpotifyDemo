package com.enrique7mc.spotifydemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends Activity {
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "enrique7mc-demo-android://callback";
    private static final String CLIENT_ID = "04530d39932e4843990ad1f546905e23";
    private static final String MAIN_ACTIVITY = "MainActivity";
    private static String TOKEN;
    SpotifyService spotify;

    @BindView(R.id.editText) EditText searchText;
    @BindView(R.id.button) Button searchButton;
    @BindView(R.id.listView) ListView listView;

    private static final int REQUEST_CODE = 1337;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupSpotifyAuth();

        listView.setEmptyView(findViewById(android.R.id.empty));
    }

    private void setupSpotifyAuth() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @OnClick(R.id.button)
    public void search(View v) {
        String artist = searchText.getText().toString();
        searchArtist(artist);
    }

    private void searchArtist(final String artist) {
        if (spotify == null) {
            throw new UnsupportedOperationException("Service not initialized");
        }
        
        spotify.searchArtists(artist, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                Pager<Artist> pager = artistsPager.artists;

                if (pager.items.size() > 0) {
                    ArtistsAdapter adapter = new ArtistsAdapter(getApplicationContext(), pager.items);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(onItemClickListener);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(MAIN_ACTIVITY, error.getMessage());
            }
        });
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), ArtistActivity.class);
            intent.putExtra("artist", (Artist)parent.getItemAtPosition(position));
            intent.putExtra("token", TOKEN);
            startActivity(intent);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                TOKEN = response.getAccessToken();
                Log.i(MAIN_ACTIVITY, "Token: " + TOKEN);
                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(TOKEN);
                spotify = api.getService();
            }
        }
    }
}