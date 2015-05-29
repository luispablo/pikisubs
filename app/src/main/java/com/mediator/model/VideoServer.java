package com.mediator.model;

import java.io.Serializable;

/**
 * Created by luispablo on 26/04/15.
 */
public class VideoServer implements Serializable {

    private String objectId;
    private String host;
    private String username;
    private String password;
    private String httpUrl;

    public VideoServer() {

    }

    public VideoServer(String host, String username, String password, String httpPath) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.httpUrl = httpPath;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
