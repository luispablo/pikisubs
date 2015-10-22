package com.mediator.model;

import java.io.Serializable;

import static com.mediator.model.VideoEntry.VideoType;

/**
 * Created by luispablo on 26/04/15.
 */
public class VideoSource implements Serializable {

    private Long id;
    private Long videoServerId;
    private String sshPath;
    private VideoType videoType;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVideoServerId() {
        return videoServerId;
    }

    public void setVideoServerId(Long videoServerId) {
        this.videoServerId = videoServerId;
    }
}
