package com.mediator.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mediator.R;
import com.mediator.model.TVShow;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Set;

/**
 * Created by luispablo on 08/05/15.
 */
public class AdapterTVShows extends BaseAdapter {

    private Context context;
    private List<TVShow> tvShows;
    private LayoutInflater inflater;

    public AdapterTVShows(Context context, List<TVShow> tvShows) {
        this.tvShows = tvShows;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return tvShows.size();
    }

    @Override
    public Object getItem(int position) {
        return tvShows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_row_tv_show, null);
        }

        TVShow tvShow = tvShows.get(position);

        ((TextView) view.findViewById(R.id.txtTitle)).setText(tvShow.getTitle());

        ImageView imagePoster = (ImageView) view.findViewById(R.id.imagePoster);

        if (tvShow.getPosterFullURL() != null && !tvShow.getPosterFullURL().isEmpty()) {
            Picasso.with(context)
                    .load(tvShow.getPosterFullURL())
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.poster_placeholder)
                    .fit()
                    .centerInside()
                    .into(imagePoster);
        }

        return view;
    }
}
