package com.mediator.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.mediator.R;
import com.mediator.helpers.HelperSnappyDB;
import com.mediator.helpers.Oju;
import com.mediator.model.TVShow;
import com.mediator.model.VideoEntry;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 07/05/15.
 */
public class FragmentTVShows extends Fragment {

    @InjectView(R.id.listTVShows)
    AbsListView listVideos;
    ProgressDialog progressDialog;
    Map<TVShow, List<VideoEntry>> videoEntries;

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

        try {
            HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(getActivity());
            List<VideoEntry> allVideos = Oju.filter(helperSnappyDB.all(VideoEntry.class), new Oju.UnaryChecker<VideoEntry>() {
                @Override
                public boolean check(VideoEntry videoEntry) {
                    return videoEntry.isTVShow();
                }
            });
            helperSnappyDB.close();

            videoEntries = Oju.groupBy(allVideos, new Oju.UnaryOperator<VideoEntry, TVShow>() {
                @Override
                public TVShow operate(VideoEntry videoEntry) {
                    return videoEntry.buildTVShow(getActivity());
                }
            });

        } catch (SnappydbException e) {
            e(e);
        }

        List<TVShow> tvShows = Oju.list(videoEntries.keySet());
        Collections.sort(tvShows);

        listVideos.setAdapter(new AdapterTVShows(getActivity(), tvShows));
        progressDialog.dismiss();
    }
}
