package com.mediator.ui;

import static com.mediator.helpers.TinyLogger.*;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import com.mediator.R;
import com.mediator.helpers.HelperDAO;
import com.mediator.model.TVShow;
import com.mediator.model.VideoEntry;

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
        listViewEpisodes.setAdapter(new AdapterEpisodes(this, listEpisodes));
    }
}
