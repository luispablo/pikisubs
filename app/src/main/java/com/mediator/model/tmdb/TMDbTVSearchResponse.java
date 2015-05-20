package com.mediator.model.tmdb;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luispablo on 20/05/15.
 */
public class TMDbTVSearchResponse implements Serializable {

    private int page;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("total_results")
    private int totalResults;
    private List<TMDbTVSearchResult> results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<TMDbTVSearchResult> getResults() {
        return results;
    }

    public void setResults(List<TMDbTVSearchResult> results) {
        this.results = results;
    }
}
