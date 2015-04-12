package com.mediator.model;

import java.io.Serializable;

/**
 * Created by luispablo on 11/04/15.
 */
public class VideoEntry implements Serializable {
    private String path;
    private String filename;
    private boolean hasSubs;
    private GuessitObject guessitObject;

    public VideoEntry() {

    }

    public VideoEntry(String path, String filename, boolean hasSubs) {
        this.path = path;
        this.filename = filename;
        this.hasSubs = hasSubs;
    }

    public String titleToShow() {
        if (getGuessitObject() != null) {
            return getGuessitObject().suggestedSearchText();
        } else {
            return getFilename();
        }
    }

    public GuessitObject getGuessitObject() {
        return guessitObject;
    }

    public void setGuessitObject(GuessitObject guessitObject) {
        this.guessitObject = guessitObject;
    }

    public boolean hasSubs() {
        return hasSubs;
    }

    public void setHasSubs(boolean hasSubs) {
        this.hasSubs = hasSubs;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}