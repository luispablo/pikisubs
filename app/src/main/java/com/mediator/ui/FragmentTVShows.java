package com.mediator.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.mediator.R;
import com.mediator.helpers.HelperParse;
import com.mediator.helpers.Oju;
import com.mediator.model.TVShow;
import com.mediator.model.VideoEntry;
import com.parse.ParseException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * Created by luispablo on 07/05/15.
 */
public class FragmentTVShows extends Fragment {

    @InjectView(R.id.listTVShows)
    AbsListView listVideos;
    ProgressDialog progressDialog;
    List<TVShow> tvShows;

    public static FragmentTVShows newInstance() {
        return new FragmentTVShows();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tv_shows, container, false);
        ButterKnife.inject(this, view);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.title_progress_tv_shows);
        progressDialog.setMessage(getString(R.string.message_wait_please));

        loadList();

        return view;
    }

    private void loadList() {
        if (!progressDialog.isShowing()) progressDialog.show();

        HelperParse helperParse = new HelperParse();
        helperParse.allEpisodes(new HelperParse.CustomFindCallback<VideoEntry>() {
            @Override
            public void done(List<VideoEntry> videoEntries, ParseException e) {
                tvShows = Oju.distinct(Oju.map(videoEntries, new Oju.UnaryOperator<VideoEntry, TVShow>() {
                    @Override
                    public TVShow operate(VideoEntry videoEntry) {
                        return videoEntry.buildTVShow(getActivity());
                    }
                }));

                for (VideoEntry videoEntry : videoEntries) {
                    for (TVShow tvShow : tvShows) {
                        if (tvShow.contains(videoEntry)) tvShow.addEpisode(videoEntry);
                    }
                }

                Collections.sort(tvShows, new Comparator<TVShow>() {
                    @Override
                    public int compare(TVShow tvShow1, TVShow tvShow2) {
                        return tvShow1.getTitle().compareTo(tvShow2.getTitle());
                    }
                });

                listVideos.setAdapter(new AdapterTVShows(getActivity(), tvShows));
                progressDialog.dismiss();
            }
        });
    }

    @OnItemClick(R.id.listTVShows)
    public void onItemClick(int position) {
        TVShow tvShow = tvShows.get(position);

        Intent intent = new Intent(getActivity(), ActivityEpisodes.class);
        intent.putExtra(TVShow.class.getName(), tvShow);

        startActivity(intent);
    }
}
