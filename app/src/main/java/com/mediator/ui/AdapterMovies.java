package com.mediator.ui;

import static com.mediator.helpers.TinyLogger.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mediator.R;
import com.mediator.model.VideoEntry;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by luispablo on 13/04/15.
 */
public class AdapterMovies extends BaseAdapter {

    private List<VideoEntry> videoEntries;
    private LayoutInflater inflater;
    private Context context;

    public AdapterMovies(Context context, List<VideoEntry> videoEntries) {
        this.context = context;
        this.videoEntries = videoEntries;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return videoEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return videoEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_video_entry, null);
        }

        VideoEntry videoEntry = videoEntries.get(position);
        d("videoEntry: "+ videoEntry.titleToShow());

        ((TextView) convertView.findViewById(R.id.txtTitleToShow)).setText(videoEntry.titleToShow());

        int watchedStringId = videoEntry.isWatched() ? R.string.watched : R.string.not_watched;
        ((TextView) convertView.findViewById(R.id.txtWatched)).setText(watchedStringId);

        ImageView imagePoster = (ImageView) convertView.findViewById(R.id.imagePoster);

        int hasSubsText = (videoEntry.hasSubs() || !videoEntry.needsSubs()) ? R.string.empty_string : R.string.needs_subs;
        ((TextView) convertView.findViewById(R.id.txtSubsIndicator)).setText(hasSubsText);

        if (videoEntry.getPosterPath() != null) {
            Picasso.with(context)
                    .load(videoEntry.buildPosterURL(context))
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.poster_placeholder)
                    .fit()
                    .centerInside()
                    .into(imagePoster);
        }

        return convertView;
    }
}