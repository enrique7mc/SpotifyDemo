package com.enrique7mc.spotifydemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.enrique7mc.spotifydemo.adapters.AlbumsAdapter;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Pager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ArtistActivity extends AppCompatActivity {

    // @BindView(R.id.artistImageView) ImageView artistImageView;
    @BindView(R.id.albumsListView) ListView albumsListView;

    private Artist artist;
    private String token;
    SpotifyService spotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        ButterKnife.bind(this);

        DemoApplication application = (DemoApplication) getApplicationContext();
        artist = application.getCurrentArtist();
        token = application.getToken();

        setTitle(artist.name);
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(token);
        spotify = api.getService();

        View header = getLayoutInflater().inflate(R.layout.list_header, null);
        ImageView artistImageView = (ImageView) header.findViewById(R.id.headerImageView);
        albumsListView.addHeaderView(header);
        Picasso.with(this)
                .load(artist.images.get(0).url)
                .resize(400, 400)
                .placeholder(R.drawable.artist_placeholder)
                .into(artistImageView);
        albumsListView.setEmptyView(findViewById(android.R.id.empty));

        getAlbums(artist.id);
    }

    private void getAlbums(String artistId) {
        spotify.getArtistAlbums(artistId, new Callback<Pager<Album>>() {
            @Override
            public void success(Pager<Album> albumPager, Response response) {
                if(albumPager.items.size() > 0) {
                    AlbumsAdapter adapter = new AlbumsAdapter(getApplicationContext(), albumPager.items);
                    albumsListView.setAdapter(adapter);
                    albumsListView.setOnItemClickListener(onItemClickListener);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("ArtistActivity", error.getMessage());
            }
        });
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DemoApplication application = (DemoApplication) getApplicationContext();
            Album album = (Album)parent.getItemAtPosition(position);
            application.setCurrentAlbum(album);
            Intent intent = new Intent(getApplicationContext(), AlbumActivity.class);
            startActivity(intent);
        }
    };
}
