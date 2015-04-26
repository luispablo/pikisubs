package com.mediator.model;

/**
 * Created by luispablo on 26/04/15.
 */
public class VideoServer implements SnappyKey {

    private String snappyKey;
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

    @Override
    public void setSnappyKey(String key) {
        this.snappyKey = key;
    }

    @Override
    public String getSnappyKey() {
        return this.snappyKey;
    }
}
