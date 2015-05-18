package com.mediator.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mediator.R;
import com.mediator.model.VideoEntry;

import java.util.List;

/**
 * Created by luispablo on 11/05/15.
 */
public class AdapterEpisodes extends BaseAdapter {

    Context context;
    List<VideoEntry> episodes;
    LayoutInflater inflater;

    public AdapterEpisodes(Context context, List<VideoEntry> episodes) {
        this.episodes = episodes;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return episodes.size();
    }

    @Override
    public Object getItem(int position) {
        return episodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_row_episode, null);
        }

        VideoEntry videoEntry = (VideoEntry) getItem(position);

        ((TextView) view.findViewById(R.id.textTitle)).setText(videoEntry.getTitle());

        String seasonEpisode = String.format(context.getString(R.string.season_episode), videoEntry.getSeasonNumber(), videoEntry.getEpisodeNumber());
        ((TextView) view.findViewById(R.id.textSeasonEpisode)).setText(seasonEpisode);

        int watchedTextId = videoEntry.isWatched() ? R.string.watched : R.string.not_watched;
        ((TextView) view.findViewById(R.id.textWatched)).setText(context.getString(watchedTextId));

        if (videoEntry.needsSubs()) {
            ((TextView) view.findViewById(R.id.textSubsIndicator)).setText(context.getString(R.string.needs_subs));
        }

        return view;
    }
}
