package com.enrique7mc.spotifydemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.enrique7mc.spotifydemo.adapters.TracksAdapter;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AlbumActivity extends AppCompatActivity implements
        PlayerNotificationCallback, ConnectionStateCallback {
    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "04530d39932e4843990ad1f546905e23";
    public static final String ALBUM_ACTIVITY = "AlbumActivity";

    @BindView(R.id.albumsListView) ListView albumsListView;
    @BindView(R.id.trackTextView) TextView trackTextView;
    @BindView(R.id.playButton) Button playButton;

    private boolean isPlaying;
    private Album album;
    private String token;
    private SpotifyService spotify;
    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ButterKnife.bind(this);

        DemoApplication application = (DemoApplication) getApplicationContext();
        album = application.getCurrentAlbum();
        token = application.getToken();

        setTitle(album.name);
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(token);
        spotify = api.getService();

        View header = getLayoutInflater().inflate(R.layout.list_header, null);
        ImageView albumImageView = (ImageView) header.findViewById(R.id.headerImageView);
        albumsListView.addHeaderView(header);
        Picasso.with(this)
                .load(album.images.get(0).url)
                .placeholder(R.drawable.album_placeholder)
                .into(albumImageView);
        albumsListView.setEmptyView(findViewById(android.R.id.empty));

        getTracks(album.id);
        initializePlayer();
    }

    private void initializePlayer() {
        Config playerConfig = new Config(AlbumActivity.this, token, CLIENT_ID);
        Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
                mPlayer = player;
                mPlayer.addConnectionStateCallback(AlbumActivity.this);
                mPlayer.addPlayerNotificationCallback(AlbumActivity.this);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(ALBUM_ACTIVITY, "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    private void getTracks(String albumId) {
        spotify.getAlbumTracks(albumId, new Callback<Pager<Track>>() {

            @Override
            public void success(Pager<Track> trackPager, Response response) {
                if(trackPager.items.size() > 0) {
                    TracksAdapter adapter = new TracksAdapter(getApplicationContext(), trackPager.items);
                    albumsListView.setAdapter(adapter);
                    albumsListView.setOnItemClickListener(onItemClickListener);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(ALBUM_ACTIVITY, error.getMessage());
            }
        });
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Track track = (Track) parent.getItemAtPosition(position);
            Log.d(ALBUM_ACTIVITY, track.name + " " + track.id);
            mPlayer.play("spotify:track:" + track.id);
            trackTextView.setText(track.name);
            playButton.setText("Pause");
        }
    };

    @Override
    public void onLoggedIn() {
        Log.d(ALBUM_ACTIVITY, "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d(ALBUM_ACTIVITY, "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d(ALBUM_ACTIVITY, "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d(ALBUM_ACTIVITY, "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d(ALBUM_ACTIVITY, "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d(ALBUM_ACTIVITY, "Playback event received: " + eventType.name());
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d(ALBUM_ACTIVITY, "Playback error received: " + errorType.name());
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }
}
