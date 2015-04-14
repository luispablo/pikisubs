package com.mediator.model;

import com.google.gson.annotations.SerializedName;

import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by luispablo on 14/04/15.
 */
public class TMDbMovieSearchResponse {
    private int page;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("total_results")
    private int totalResults;
    private List<TMDbMovieSearchResult> results;

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

    public List<TMDbMovieSearchResult> getResults() {
        return results;
    }

    public void setResults(List<TMDbMovieSearchResult> results) {
        this.results = results;
    }
}