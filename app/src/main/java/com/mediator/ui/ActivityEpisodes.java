package com.mediator.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.mediator.R;
import com.mediator.actions.ActionDownloadSubs;
import com.mediator.actions.ActionGetEpisodesInfo;
import com.mediator.actions.ActionIdentifyVideo;
import com.mediator.actions.ActionSetTMDbId;
import com.mediator.actions.IActionCallback;
import com.mediator.helpers.HelperDAO;
import com.mediator.helpers.HelperParse;
import com.mediator.helpers.Oju;
import com.mediator.model.TVShow;
import com.mediator.model.VideoEntry;
import com.mediator.model.tmdb.TMDbTVResult;
import com.parse.ParseException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * Created by luispablo on 11/05/15.
 */
public class ActivityEpisodes extends ActionBarActivity implements IActionCallback {

    FragmentFilterVideosDialog.VideoFilter filter;
    TVShow tvShow;
    List<VideoEntry> listEpisodes;
    Parcelable listViewEpisodesState;

    @InjectView(R.id.listViewEpisodes)
    ListView listViewEpisodes;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvShow = (TVShow) getIntent().getSerializableExtra(TVShow.class.getName());
        filter = FragmentFilterVideosDialog.VideoFilter.NOT_WATCHED;

        ButterKnife.inject(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.title_wait_please);
        progressDialog.setMessage(getString(R.string.message_loading_data));
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
            case R.id.action_identify_video:
                identifyVideo();
                return true;
            case R.id.action_set_tmdb_id:
                setTVShowTMDbId();
                return true;
            case R.id.action_get_videos_info:
                getEpisodesInfo();
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

    private void getEpisodesInfo() {
        ActionGetEpisodesInfo actionGetEpisodesInfo = new ActionGetEpisodesInfo();
        actionGetEpisodesInfo.execute(this, listEpisodes.get(0), this);
    }

    private void identifyVideo() {
        ActionIdentifyVideo actionIdentifyVideo = new ActionIdentifyVideo();
        actionIdentifyVideo.execute(this, listEpisodes.get(0), this);
    }

    private void setTVShowTMDbId() {
        HelperDAO helperDAO = new HelperDAO(this);
        helperDAO.episodesFrom(tvShow, new HelperParse.CustomFindCallback<VideoEntry>() {
            @Override
            public void done(final List<VideoEntry> episodes, ParseException e) {
                ActionSetTMDbId actionSetTMDbId = new ActionSetTMDbId() {
                    @Override
                    protected void gotTVShowResult(TMDbTVResult tmdbTVResult) {
                        for (VideoEntry episode : episodes) {
                            episode.setPosterPath(tmdbTVResult.getPosterPath());
                            episode.setSeriesTitle(tmdbTVResult.getName());
                            episode.setVideoType(VideoEntry.VideoType.TV_SHOW);

                            HelperParse helperParse = new HelperParse();
                            helperParse.update(episode, null);
                        }
                    }
                };
                actionSetTMDbId.execute(ActivityEpisodes.this, episodes.get(0), ActivityEpisodes.this);
            }
        });
    }

    private void filterList(FragmentFilterVideosDialog.VideoFilter filter) {
        this.filter = filter;
        load();
    }

    private void load() {
        progressDialog.show();

        if (tvShow.getEpisodes().isEmpty()) {
            HelperDAO helperDAO = new HelperDAO(this);
            helperDAO.episodesFrom(tvShow, new HelperParse.CustomFindCallback<VideoEntry>() {
                @Override
                public void done(List<VideoEntry> episodes, ParseException e) {
                    tvShow.getEpisodes().addAll(episodes);
                    fillListView();
                }
            });
        } else {
            fillListView();
        }
    }

    private void fillListView() {
        listEpisodes = Oju.filter(tvShow.getEpisodes(), new Oju.UnaryChecker<VideoEntry>() {
            @Override
            public boolean check(VideoEntry episode) {
                return filter.applies(episode);
            }
        });
        Collections.sort(listEpisodes, new EpisodeComparator());
        listViewEpisodes.setAdapter(new AdapterEpisodes(ActivityEpisodes.this, listEpisodes));

        if (listViewEpisodesState != null)
            listViewEpisodes.onRestoreInstanceState(listViewEpisodesState);

        progressDialog.dismiss();
    }

    @OnItemClick(R.id.listViewEpisodes)
    public void onItemClick(int position) {
        listViewEpisodesState = listViewEpisodes.onSaveInstanceState();

        FragmentEpisodeActionsDialog fragmentEpisodeActionsDialog = new FragmentEpisodeActionsDialog();
        fragmentEpisodeActionsDialog.setEpisode(listEpisodes.get(position));
        fragmentEpisodeActionsDialog.setCallback(this);
        fragmentEpisodeActionsDialog.show(getFragmentManager(), null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ActionDownloadSubs.REQUEST_CODE_DOWNLOAD_SUBS == requestCode) {
            load();
        }
    }

    @Override
    public void onDone(boolean changedDB) {
        if (changedDB) load();
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
