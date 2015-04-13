package com.mediator.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mediator.R;
import com.mediator.model.Subtitle;

import java.util.List;

/**
 * Created by luispablo on 13/04/15.
 */
public class AdapterSubtitles extends BaseAdapter {

    private List<Subtitle> subtitles;
    private LayoutInflater inflater;

    public AdapterSubtitles(Context context, List<Subtitle> subtitles) {
        this.subtitles = subtitles;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return subtitles.size();
    }

    @Override
    public Object getItem(int position) {
        return subtitles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_subtitle, null);
        }

        Subtitle subtitle = subtitles.get(position);

        ((TextView) convertView.findViewById(R.id.txtTitle)).setText(subtitle.getTitle());
        ((TextView) convertView.findViewById(R.id.txtDescription)).setText(subtitle.getDescription());

        return convertView;
    }
}