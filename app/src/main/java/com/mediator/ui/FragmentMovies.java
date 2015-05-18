package com.mediator.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.mediator.R;
import com.mediator.actions.IAction;
import com.mediator.helpers.HelperSnappyDB;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoSource;
import com.mediator.tasks.TaskDoneListener;
import com.mediator.tasks.TaskGetAllVideos;
import com.mediator.tasks.TaskGuessitVideos;
import com.mediator.tasks.TaskSearchPoster;
import com.mediator.tasks.TaskUpdateLocalDB;
import com.snappydb.SnappydbException;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

import static com.mediator.helpers.TinyLogger.d;
import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 23/04/15.
 */
public class FragmentMovies extends Fragment {

    @InjectView(R.id.listVideos)
    AbsListView listVideos;
    ProgressDialog progressDialog;
    List<VideoEntry> videoEntries;
    Bus bus;
    FragmentFilterVideosDialog.VideoFilter filter;

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

        try {
            HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(getActivity());

            videoEntries = Oju.filter(helperSnappyDB.all(VideoEntry.class), new Oju.UnaryChecker<VideoEntry>() {
                @Override
                public boolean check(VideoEntry videoEntry) {
                    return videoEntry.isMovie() && filter.applies(videoEntry);
                }
            });
            helperSnappyDB.close();
        } catch (SnappydbException e) {
            e(e);
        }

        listVideos.setAdapter(new AdapterMovies(getActivity(), videoEntries));
        progressDialog.dismiss();
    }

    @OnItemClick(R.id.listVideos)
    public void onClickVideo(int position) {
        FragmentVideoActionsDialog actionsDialog = new FragmentVideoActionsDialog() {

            @Override
            public void onDone(IAction action) {
                if (action.changedDB()) loadList();
            }
        };
        actionsDialog.setVideoEntry(videoEntries.get(position));
        actionsDialog.show(getFragmentManager(), null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_local_videos, menu);
    }

    private void refreshLocalDB() throws SnappydbException {
        progressDialog.setMessage(getString(R.string.message_getting_videos));
        progressDialog.show();

        TaskDoneListener<List<VideoEntry>> updateLocalDBListener = new TaskDoneListener<List<VideoEntry>>(){

            @Override
            public void onDone(List<VideoEntry> o) {
                d("updateLocalDBListener onDone()");
                loadList();
            }
        };

        final TaskUpdateLocalDB taskUpdateLocalDB = new TaskUpdateLocalDB(getActivity(), updateLocalDBListener);

        TaskDoneListener<List<VideoEntry>> searchTMDbListener = new TaskDoneListener<List<VideoEntry>>() {
            @Override
            public void onDone(List<VideoEntry> videoEntries) {
                progressDialog.setMessage(getString(R.string.message_updating_local_db));
                taskUpdateLocalDB.execute(videoEntries.toArray(new VideoEntry[]{}));
            }
        };

        final TaskSearchPoster taskSearchPoster = new TaskSearchPoster(getActivity(), searchTMDbListener);

        TaskDoneListener<List<VideoEntry>> guessitListener = new TaskDoneListener<List<VideoEntry>>() {
            @Override
            public void onDone(List<VideoEntry> videoEntries) {
                progressDialog.setMessage(getString(R.string.message_getting_posters));
                taskSearchPoster.execute(videoEntries);
            }
        };

        final TaskGuessitVideos taskGuessitVideos = new TaskGuessitVideos(getActivity(), null, guessitListener);

        TaskDoneListener<List<VideoEntry>> getAllVideosListener = new TaskDoneListener<List<VideoEntry>>() {
            @Override
            public void onDone(List<VideoEntry> videoEntries) {
                progressDialog.setMessage(getString(R.string.message_guessing_videos));
                taskGuessitVideos.execute(videoEntries.toArray(new VideoEntry[]{}));
            }
        };

        HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(getActivity());
        List<VideoSource> videoSources = helperSnappyDB.all(VideoSource.class);
        helperSnappyDB.close();

        TaskGetAllVideos taskGetAllVideos = new TaskGetAllVideos(getActivity(), getAllVideosListener);
        taskGetAllVideos.execute(videoSources.toArray(new VideoSource[]{}));
    }

    private void filterList(FragmentFilterVideosDialog.VideoFilter filter) {
        this.filter = filter;
        loadList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_local_videos_refresh:
                try {
                    refreshLocalDB();
                } catch (SnappydbException e) {
                    e(e);
                }
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
}
