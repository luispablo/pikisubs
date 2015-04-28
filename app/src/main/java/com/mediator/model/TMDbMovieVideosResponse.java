package com.mediator.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luispablo on 27/04/15.
 */
public class TMDbMovieVideosResponse implements Serializable {
    private long id;
    private List<TMDbMovieVideosResult> results;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<TMDbMovieVideosResult> getResults() {
        return results;
    }

    public void setResults(List<TMDbMovieVideosResult> results) {
        this.results = results;
    }
}
