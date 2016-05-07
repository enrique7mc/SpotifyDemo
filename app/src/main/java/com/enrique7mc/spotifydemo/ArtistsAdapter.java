package com.enrique7mc.spotifydemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by enrique.munguia on 06/05/2016.
 */
public class ArtistsAdapter extends ArrayAdapter<Artist> {

    public ArtistsAdapter(Context context, List<Artist> artists) {
        super(context, 0, artists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Artist artist = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_artist, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.imageView);
        TextView name = (TextView) convertView.findViewById(R.id.nameTextView);

        if (artist.images.size() > 0) {
            Image im = artist.images.get(0);
            Picasso.with(getContext())
                    .load(im.url)
                    .resize(250, 250)
                    .placeholder(R.drawable.placeholder)
                    .into(image);
        }
        name.setText(artist.name);

        return convertView;
    }
}
