package com.mediator.helpers;

import android.content.Context;

import com.mediator.model.TVShow;
import com.mediator.model.VideoEntry;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luispablo on 26/04/15.
 */
public class HelperDAO {

    private Context context;

    public HelperDAO(Context context) {
        this.context = context;
    }

    public void episodesFrom(final TVShow tvShow, final HelperParse.CustomFindCallback<VideoEntry> callback) {
        HelperParse helperParse = new HelperParse();
        helperParse.allVideoEntries(new HelperParse.CustomFindCallback<VideoEntry>() {
            @Override
            public void done(List<VideoEntry> list, ParseException e) {
                List<VideoEntry> episodes = new ArrayList<>();

                episodes.addAll(Oju.filter(list, new Oju.UnaryChecker<VideoEntry>() {
                    @Override
                    public boolean check(VideoEntry videoEntry) {
                        return tvShow.contains(videoEntry);
                    }
                }));

                callback.done(episodes, e);
            }
        });
    }
}
