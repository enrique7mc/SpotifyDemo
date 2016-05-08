package com.enrique7mc.spotifydemo;

import android.app.Application;

import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by enrique.munguia on 07/05/2016.
 */
public class DemoApplication extends Application {
    private static DemoApplication application;
    private Artist currentArtist;
    private Album currentAlbum;
    private String token;

    public DemoApplication getInstance(){
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public Artist getCurrentArtist() {
        return currentArtist;
    }

    public void setCurrentArtist(Artist currentArtist) {
        this.currentArtist = currentArtist;
    }

    public Album getCurrentAlbum() {
        return currentAlbum;
    }

    public void setCurrentAlbum(Album currentAlbum) {
        this.currentAlbum = currentAlbum;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
