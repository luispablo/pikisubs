package com.mediator;

import com.mediator.model.GuessitObject;
import com.mediator.model.Subtitle;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luispablo on 10/04/15.
 */
public class SubtitlesSearcher {

    SubtitlesSource[] sources = {new Subdivx()};

    public List<Subtitle> search(GuessitObject giObject) {
        Logger.d("searching: "+ giObject);
        List<Subtitle> subtitles = new ArrayList<>();

        for (SubtitlesSource source : sources) {
            subtitles.addAll(source.search(giObject));
        }

        return subtitles;
    }
}
