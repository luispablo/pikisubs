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
import android.widget.ListView;

import com.mediator.R;
import com.mediator.actions.IAction;
import com.mediator.helpers.HelperSnappyDB;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoEntry;
import com.mediator.model.VideoSource;
import com.mediator.tasks.TaskDoneListener;
import com.mediator.tasks.TaskGetAllVideos;
import com.mediator.tasks.TaskGuessitVideos;
import com.mediator.tasks.TaskSearchTMDb;
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
public class FragmentLocalVideos extends Fragment {

    public enum Filter {
        ALL,
        WATCHED,
        NOT_WATCHED;

        public boolean applies(VideoEntry videoEntry) {
            return ALL.equals(this) ||
                    WATCHED.equals(this) && videoEntry.isWatched() ||
                    NOT_WATCHED.equals(this) && !videoEntry.isWatched();
        }
    }

    @InjectView(R.id.listVideos)
    ListView listVideos;
    ProgressDialog progressDialog;
    List<VideoEntry> videoEntries;
    Bus bus;
    Filter filter;

    public static FragmentLocalVideos newInstance() {
        return new FragmentLocalVideos();
    }

    public FragmentLocalVideos() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_videos, container, false);
        ButterKnife.inject(this, view);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.title_progress_videos);
        progressDialog.setMessage(getString(R.string.message_wait_please));

        bus = new Bus();
        bus.register(this);

        videoEntries = new ArrayList<>();
        filter = Filter.NOT_WATCHED;

        setHasOptionsMenu(true);

        loadList();

        return view;
    }

    private void loadList() {
        if (!progressDialog.isShowing()) progressDialog.show();

        try {
            HelperSnappyDB helperSnappyDB = new HelperSnappyDB(getActivity());

            videoEntries = Oju.filter(helperSnappyDB.all(VideoEntry.class), new Oju.UnaryChecker<VideoEntry>() {
                @Override
                public boolean check(VideoEntry videoEntry) {
                    return filter.applies(videoEntry);
                }
            });
            helperSnappyDB.close();
        } catch (SnappydbException e) {
            e(e);
        }

        listVideos.setAdapter(new AdapterVideoEntries(getActivity(), videoEntries));
        progressDialog.dismiss();
    }

    @OnItemClick(R.id.listVideos)
    public void onClickVideo(int position) {
        FragmentVideoActionsDialog actionsDialog = new FragmentVideoActionsDialog() {

            @Override
            public void onDone(IAction action) {
                loadList();
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

        final TaskSearchTMDb taskSearchTMDb = new TaskSearchTMDb(getActivity(), searchTMDbListener);

        TaskDoneListener<List<VideoEntry>> guessitListener = new TaskDoneListener<List<VideoEntry>>() {
            @Override
            public void onDone(List<VideoEntry> videoEntries) {
                progressDialog.setMessage(getString(R.string.message_getting_posters));
                taskSearchTMDb.execute(videoEntries);
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

        HelperSnappyDB helperSnappyDB = new HelperSnappyDB(getActivity());
        List<VideoSource> videoSources = helperSnappyDB.all(VideoSource.class);
        helperSnappyDB.close();

        TaskGetAllVideos taskGetAllVideos = new TaskGetAllVideos(getActivity(), getAllVideosListener);
        taskGetAllVideos.execute(videoSources.toArray(new VideoSource[]{}));
    }

    private void filterList(Filter filter) {
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
                    public void onSelected(Filter filter) {
                        filterList(filter);
                    }
                };
                filterVideosDialog.setFilterItems(Filter.values());
                filterVideosDialog.show(getFragmentManager(), null);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
