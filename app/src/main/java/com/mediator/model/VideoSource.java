package com.mediator.model;

/**
 * Created by luispablo on 26/04/15.
 */
public class VideoSource implements SnappyKey {

    private String snappyKey;
    private String sshPath;

    public String getSshPath() {
        return sshPath;
    }

    public void setSshPath(String sshPath) {
        this.sshPath = sshPath;
    }

    @Override
    public void setSnappyKey(String key) {
        this.snappyKey = key;
    }

    @Override
    public String getSnappyKey() {
        return this.snappyKey;
    }
}
