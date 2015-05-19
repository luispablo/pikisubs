package com.mediator.ui;

import static com.mediator.helpers.TinyLogger.*;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.mediator.R;
import com.mediator.actions.ActionSetTMDbId;
import com.mediator.actions.IAction;
import com.mediator.helpers.HelperDAO;
import com.mediator.helpers.HelperSnappyDB;
import com.mediator.helpers.Oju;
import com.mediator.model.TVShow;
import com.mediator.model.VideoEntry;
import com.mediator.model.tmdb.TMDbTVResult;
import com.snappydb.SnappydbException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * Created by luispablo on 11/05/15.
 */
public class ActivityEpisodes extends ActionBarActivity {

    FragmentFilterVideosDialog.VideoFilter filter;
    TVShow tvShow;
    List<VideoEntry> listEpisodes;

    @InjectView(R.id.listViewEpisodes)
    ListView listViewEpisodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvShow = (TVShow) getIntent().getSerializableExtra(TVShow.class.getName());
        filter = FragmentFilterVideosDialog.VideoFilter.NOT_WATCHED;

        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_episodes, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set_tmdb_id:
                setTVShowTMDbId();
                return true;
            case R.id.action_episodes_filter:
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

    private void setTVShowTMDbId() {
        HelperDAO helperDAO = new HelperDAO(this);
        final List<VideoEntry> episodes = helperDAO.episodesFrom(tvShow);

        ActionSetTMDbId actionSetTMDbId = new ActionSetTMDbId() {
            @Override
            protected void gotTVShowResult(TMDbTVResult tmdbTVResult) {
                d("ActivityEpisodes > ActionSetTMDbId.gotTVShowResult()");
                HelperSnappyDB helperSnappyDB = HelperSnappyDB.getSingleton(ActivityEpisodes.this);

                try {
                    for (VideoEntry episode : episodes) {
                        episode.setPosterPath(tmdbTVResult.getPosterPath());
                        episode.setSeriesTitle(tmdbTVResult.getName());
                        episode.setVideoType(VideoEntry.VideoType.TV_SHOW);
                        helperSnappyDB.update(episode);
                    }

                    helperSnappyDB.close();
                } catch (SnappydbException e) {
                    e(e);
                }
            }
        };
        actionSetTMDbId.execute(this, episodes.get(0));
    }

    private void filterList(FragmentFilterVideosDialog.VideoFilter filter) {
        this.filter = filter;
        load();
    }

    private void load() {
        HelperDAO helperDAO = new HelperDAO(this);
        listEpisodes = Oju.filter(helperDAO.episodesFrom(tvShow), new Oju.UnaryChecker<VideoEntry>() {
            @Override
            public boolean check(VideoEntry episode) {
                return filter.applies(episode);
            }
        });
        Collections.sort(listEpisodes, new EpisodeComparator());
        listViewEpisodes.setAdapter(new AdapterEpisodes(this, listEpisodes));
    }

    @OnItemClick(R.id.listViewEpisodes)
    public void onItemClick(int position) {
        FragmentEpisodeActionsDialog fragmentEpisodeActionsDialog = new FragmentEpisodeActionsDialog() {
            @Override
            public void onDone(IAction action) {
                if (action.changedDB()) load();
            }
        };
        fragmentEpisodeActionsDialog.setEpisode(listEpisodes.get(position));
        fragmentEpisodeActionsDialog.show(getFragmentManager(), null);
    }

    class EpisodeComparator implements Comparator<VideoEntry> {

        @Override
        public int compare(VideoEntry v1, VideoEntry v2) {
            if (v1.getSeasonNumber() < v2.getSeasonNumber()) {
                return -1;
            } else if (v1.getSeasonNumber() == v2.getSeasonNumber()) {
                if (v1.getEpisodeNumber() < v2.getEpisodeNumber()) {
                    return -1;
                } else if (v1.getEpisodeNumber() == v2.getEpisodeNumber()) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }
    }
}
