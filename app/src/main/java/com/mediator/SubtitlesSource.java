package com.mediator;

import java.util.List;

/**
 * Created by luispablo on 10/04/15.
 */
public interface SubtitlesSource {

    public List<Subtitle> search(String text);

}
