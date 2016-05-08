package com.enrique7mc.spotifydemo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.enrique7mc.spotifydemo.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by enrique.munguia on 06/05/2016.
 */
public class AlbumsAdapter extends ArrayAdapter<Album> {

    public AlbumsAdapter(Context context, List<Album> artists) {
        super(context, 0, artists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Album album = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.imageView);
        TextView name = (TextView) convertView.findViewById(R.id.nameTextView);

        if (album.images.size() > 0) {
            Image im = album.images.get(0);
            Picasso.with(getContext())
                    .load(im.url)
                    .resize(250, 250)
                    .placeholder(R.drawable.album_placeholder)
                    .into(image);
        }
        name.setText(album.name);

        return convertView;
    }
}

