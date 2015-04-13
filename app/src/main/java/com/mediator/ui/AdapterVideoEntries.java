package com.mediator.ui;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mediator.R;
import com.mediator.model.VideoEntry;

import java.util.List;

/**
 * Created by luispablo on 13/04/15.
 */
public class AdapterVideoEntries extends BaseAdapter {

    private List<VideoEntry> videoEntries;
    private LayoutInflater inflater;

    public AdapterVideoEntries(Context context, List<VideoEntry> videoEntries) {
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

        if (videoEntry.getGuessitObject() != null) {
            ((TextView) convertView.findViewById(R.id.txtTitleToShow)).setText(videoEntry.titleToShow());
        }
        ((TextView) convertView.findViewById(R.id.txtFilename)).setText(videoEntry.getFilename());

        return convertView;
    }
}