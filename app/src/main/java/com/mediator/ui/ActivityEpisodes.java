package com.mediator.ui;

import static com.mediator.helpers.TinyLogger.*;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import com.mediator.R;
import com.mediator.helpers.HelperDAO;
import com.mediator.model.TVShow;
import com.mediator.model.VideoEntry;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by luispablo on 11/05/15.
 */
public class ActivityEpisodes extends ActionBarActivity {

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

        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        HelperDAO helperDAO = new HelperDAO(this);
        listEpisodes = helperDAO.episodesFrom(tvShow);
        Collections.sort(listEpisodes, new EpisodeComparator());
        listViewEpisodes.setAdapter(new AdapterEpisodes(this, listEpisodes));
    }

    class EpisodeComparator implements Comparator<VideoEntry> {

        @Override
        public int compare(VideoEntry v1, VideoEntry v2) {
            if (v1.getSeason() < v2.getSeason()) {
                return -1;
            } else if (v1.getSeason() == v2.getSeason()) {
                int episode1 = Integer.parseInt(v1.getEpisodeNumber());
                int episode2 = Integer.parseInt(v2.getEpisodeNumber());

                if (episode1 < episode2) {
                    return -1;
                } else if (episode1 == episode2) {
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
