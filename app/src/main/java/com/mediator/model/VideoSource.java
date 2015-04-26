package com.mediator.model;

import static com.mediator.model.VideoEntry.VideoType;

/**
 * Created by luispablo on 26/04/15.
 */
public class VideoSource implements SnappyKey {

    private String snappyKey;
    private String sshPath;
    private VideoType videoType;
    private String serverSnappyKey;
    private String httpPath;

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

    public VideoType getVideoType() {
        return videoType;
    }

    public void setVideoType(VideoType videoType) {
        this.videoType = videoType;
    }

    public String getServerSnappyKey() {
        return serverSnappyKey;
    }

    public void setServerSnappyKey(String serverSnappyKey) {
        this.serverSnappyKey = serverSnappyKey;
    }

    public String getHttpPath() {
        return httpPath;
    }

    public void setHttpPath(String httpPath) {
        this.httpPath = httpPath;
    }
}
