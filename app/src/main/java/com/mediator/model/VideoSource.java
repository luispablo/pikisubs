package com.mediator.model;

import java.io.Serializable;

import static com.mediator.model.VideoEntry.VideoType;

/**
 * Created by luispablo on 26/04/15.
 */
public class VideoSource implements Serializable {

    private String objectId;
    private String sshPath;
    private VideoType videoType;
    private VideoServer videoServer;
    private String httpPath;

    public String getSshPath() {
        return sshPath;
    }

    public void setSshPath(String sshPath) {
        this.sshPath = sshPath;
    }

    public VideoType getVideoType() {
        return videoType;
    }

    public void setVideoType(VideoType videoType) {
        this.videoType = videoType;
    }

    public String getHttpPath() {
        return httpPath;
    }

    public void setHttpPath(String httpPath) {
        this.httpPath = httpPath;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public VideoServer getVideoServer() {
        return videoServer;
    }

    public void setVideoServer(VideoServer videoServer) {
        this.videoServer = videoServer;
    }
}
