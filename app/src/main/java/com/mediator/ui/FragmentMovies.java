package com.mediator.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.mediator.R;
import com.mediator.actions.ActionDownloadSubs;
import com.mediator.actions.IActionCallback;
import com.mediator.helpers.HelperMediator;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;
import com.mediator.tasks.TaskRefreshLocalDB;
import com.mediator.tasks.TaskRemoveDuplicated;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * Created by luispablo on 23/04/15.
 */
public class FragmentMovies extends Fragment implements IActionCallback {

    @InjectView(R.id.listVideos)
    AbsListView listVideos;
    ProgressDialog progressDialog;
    List<VideoEntry> videoEntries;
    Bus bus;
    FragmentFilterVideosDialog.VideoFilter filter;
    Parcelable listVideosState;

    public static FragmentMovies newInstance() {
        return new FragmentMovies();
    }

    public FragmentMovies() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.inject(this, view);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.title_progress_videos);
        progressDialog.setMessage(getString(R.string.message_wait_please));

        bus = new Bus();
        bus.register(this);

        videoEntries = new ArrayList<>();
        filter = FragmentFilterVideosDialog.VideoFilter.NOT_WATCHED;

        setHasOptionsMenu(true);

        loadList();

        return view;
    }

    private void loadList() {
        if (!progressDialog.isShowing()) progressDialog.show();

        HelperMediator helperMediator = new HelperMediator(getActivity());
        List<VideoEntry> allMovies = helperMediator.allMovies();

        videoEntries = Oju.filter(allMovies, new Oju.UnaryChecker<VideoEntry>() {
            @Override
            public boolean check(VideoEntry videoEntry) {
                return filter.applies(videoEntry);
            }
        });

        Collections.sort(videoEntries, new Comparator<VideoEntry>() {
            @Override
            public int compare(VideoEntry video1, VideoEntry video2) {
                return video1.titleToShow().compareTo(video2.titleToShow());
            }
        });

        listVideos.setAdapter(new AdapterMovies(getActivity(), videoEntries));

        if (listVideosState != null) listVideos.onRestoreInstanceState(listVideosState);

        progressDialog.dismiss();
    }

    @OnItemClick(R.id.listVideos)
    public void onClickVideo(int position) {
        listVideosState = listVideos.onSaveInstanceState();

        FragmentMovieActionsDialog actionsDialog = new FragmentMovieActionsDialog();
        actionsDialog.setVideoEntry(videoEntries.get(position));
        actionsDialog.setCallback(this);
        actionsDialog.show(getFragmentManager(), null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_local_videos, menu);
    }

    private void filterList(FragmentFilterVideosDialog.VideoFilter filter) {
        this.filter = filter;
        loadList();
    }

    private void refreshLocalDB() {
        progressDialog.setMessage(getString(R.string.message_removing_duplicates));
        progressDialog.show();

        final TaskRefreshLocalDB taskRefreshLocalDB = new TaskRefreshLocalDB(getActivity()) {
            @Override
            public void onProgress(String message) {
                progressDialog.setMessage(message);
            }

            @Override
            public void onFinished() {
                loadList();
                progressDialog.dismiss();
            }
        };

        TaskRemoveDuplicated taskRemoveDuplicated = new TaskRemoveDuplicated(getActivity()) {
            @Override
            protected void onPostExecute(Void aVoid) {
                progressDialog.setMessage(getString(R.string.message_rescanning_db));
                taskRefreshLocalDB.execute();
            }
        };
        taskRemoveDuplicated.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_local_videos_refresh:
                refreshLocalDB();
                return true;
            case R.id.action_local_videos_filter:
                FragmentFilterVideosDialog filterVideosDialog = new FragmentFilterVideosDialog() {
                    @Override
                    public void onSelected(VideoFilter filter) {
                        filterList(filter);
                    }
                };
                filterVideosDialog.setFilterItems(FragmentFilterVideosDialog.VideoFilter.values());
                filterVideosDialog.show(getFragmentManager(), null);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ActionDownloadSubs.REQUEST_CODE_DOWNLOAD_SUBS == requestCode) {
            loadList();
        }
    }

    @Override
    public void onDone(boolean changedDB) {
        if (changedDB) loadList();
    }
}
