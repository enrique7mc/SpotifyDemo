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

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by enrique.munguia on 06/05/2016.
 */
public class TracksAdapter extends ArrayAdapter<Track> {

    public TracksAdapter(Context context, List<Track> artists) {
        super(context, 0, artists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Track track = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.imageView);
        TextView name = (TextView) convertView.findViewById(R.id.nameTextView);

        Picasso.with(getContext())
                .load(R.drawable.album_placeholder)
                .resize(250, 250)
                .into(image);

        name.setText(track.name);

        return convertView;
    }
}
