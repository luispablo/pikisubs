package com.mediator.sources;

import com.mediator.model.GuessitObject;
import com.mediator.model.Subtitle;
import com.mediator.model.VideoEntry;

import java.util.List;

/**
 * Created by luispablo on 10/04/15.
 */
public interface SubtitlesSource {

    public List<Subtitle> search(VideoEntry videoEntry);

    public String getName();
}