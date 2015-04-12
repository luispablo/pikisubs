package com.mediator;

import com.mediator.model.GuessitObject;
import com.mediator.model.Subtitle;

import java.util.List;

/**
 * Created by luispablo on 10/04/15.
 */
public interface SubtitlesSource {

    public List<Subtitle> search(GuessitObject giObject);

}
