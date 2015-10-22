package com.mediator.helpers;

/**
 * Created by luispablo on 18/05/15.
 */
public class HelperParse {
/*
    public static final String HOST = "host";
    public static final String HTTP_URL = "httpUrl";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String VIDEO_TYPE = "videoType";
    public static final String HTTP_PATH = "httpPath";
    public static final String VIDEO_SERVER = "videoServer";
    public static final String SSH_PATH = "sshPath";
    public static final String TMDB_ID = "tmdbId";
    public static final String SEASON_NUMBER = "seasonNumber";
    public static final String ABSOLUTE_PATH = "absolutePath";
    public static final String EPISODE_NUMBER = "episodeNumber";
    public static final String FILENAME = "filename";
    public static final String PATH_RELATIVE_TO_SOURCE = "pathRelativeToSource";
    public static final String POSTER_PATH = "posterPath";
    public static final String VIDEO_SOURCE = "videoSource";
    public static final String TITLE = "title";
    public static final String SERIES_TITLE = "seriesTitle";
    public static final String HAS_SUBS = "hasSubs";
    public static final String IS_WATCHED = "isWatched";
    public static final String NEEDS_SUBS = "needsSubs";
    public static final String PARSE_USER = "parseUser";

    public ParseObject toParse(VideoEntry videoEntry) {
        ParseObject po = new ParseObject(parseClassName(VideoEntry.class));
        toParse(videoEntry, po);

        return po;
    }

    public void toParse(VideoEntry videoEntry, ParseObject po) {
        if (videoEntry.getObjectId() != null) po.setObjectId(videoEntry.getObjectId());

        po.put(VIDEO_TYPE, videoEntry.getVideoType().name());
        po.put(TMDB_ID, videoEntry.getTmdbId());
        po.put(SEASON_NUMBER, videoEntry.getSeasonNumber());
        po.put(ABSOLUTE_PATH, videoEntry.getAbsolutePath());
        po.put(EPISODE_NUMBER, videoEntry.getEpisodeNumber());
        po.put(FILENAME, videoEntry.getFilename());
        po.put(PATH_RELATIVE_TO_SOURCE, videoEntry.getPathRelativeToSource());

        put(po, POSTER_PATH, videoEntry.getPosterPath());

        po.put(VIDEO_SOURCE, ParseObject.createWithoutData(parseClassName(VideoSource.class),
                videoEntry.getVideoSource().getObjectId()));

        po.put(HAS_SUBS, videoEntry.hasSubs());
        po.put(IS_WATCHED, videoEntry.isWatched());
        po.put(NEEDS_SUBS, videoEntry.needsSubs());
        po.put(PARSE_USER, ParseUser.getCurrentUser());

        put(po, TITLE, videoEntry.getTitle());
        put(po, SERIES_TITLE, videoEntry.getSeriesTitle());
    }

    private void put(ParseObject po, String key, String value) {
        if (value != null) {
            po.put(key, value);
        }
    }

    public VideoServer toVideoServer(ParseObject po) {
        VideoServer videoServer = new VideoServer();
        videoServer.setHost(po.getString(HOST));
        videoServer.setHttpUrl(po.getString(HTTP_URL));
        videoServer.setObjectId(po.getObjectId());
        videoServer.setPassword(po.getString(PASSWORD));
        videoServer.setUsername(po.getString(USERNAME));

        return videoServer;
    }

    public VideoSource toVideoSource(ParseObject po) {
        VideoSource videoSource = new VideoSource();
        videoSource.setObjectId(po.getObjectId());
        videoSource.setHttpPath(po.getString(HTTP_PATH));
        videoSource.setSshPath(po.getString(SSH_PATH));
        videoSource.setVideoType(VideoEntry.VideoType.valueOf(po.getString(VIDEO_TYPE)));

        return videoSource;
    }

    public VideoEntry toVideoEntry(ParseObject po) {
        VideoEntry videoEntry = new VideoEntry();
        videoEntry.setObjectId(po.getObjectId());
        videoEntry.setPosterPath(po.getString(POSTER_PATH));
        videoEntry.setVideoType(VideoEntry.VideoType.valueOf(po.getString(VIDEO_TYPE)));
        videoEntry.setHasSubs(po.getBoolean(HAS_SUBS));
        videoEntry.setSeasonNumber(po.getInt(SEASON_NUMBER));
        videoEntry.setTmdbId(po.getLong(TMDB_ID));
        videoEntry.setAbsolutePath(po.getString(ABSOLUTE_PATH));
        videoEntry.setEpisodeNumber(po.getInt(EPISODE_NUMBER));
        videoEntry.setFilename(po.getString(FILENAME));
        videoEntry.setNeedsSubs(po.getBoolean(NEEDS_SUBS));
        videoEntry.setPathRelativeToSource(po.getString(PATH_RELATIVE_TO_SOURCE));
        videoEntry.setSeriesTitle(po.getString(SERIES_TITLE));
        videoEntry.setTitle(po.getString(TITLE));
        videoEntry.setWatched(po.getBoolean(IS_WATCHED));

        return videoEntry;
    }

    public ParseObject toParse(VideoSource videoSource) {
        ParseObject po = new ParseObject(parseClassName(VideoSource.class));
        toParse(videoSource, po);

        return po;
    }

    public void toParse(VideoSource videoSource, ParseObject po) {
        if (videoSource.getObjectId() != null) po.setObjectId(videoSource.getObjectId());

        po.put(VIDEO_TYPE, videoSource.getVideoType().name());
        po.put(HTTP_PATH, videoSource.getHttpPath());
        po.put(VIDEO_SERVER, ParseObject.createWithoutData(parseClassName(VideoServer.class), videoSource.getVideoServer().getObjectId()));
        po.put(SSH_PATH, videoSource.getSshPath());
        po.put(PARSE_USER, ParseUser.getCurrentUser());
    }

    public <T> void delete(String objectId, Class<T> clazz) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(parseClassName(clazz));
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject != null) parseObject.deleteInBackground();
            }
        });
    }

    public void update(final VideoEntry videoEntry, final SaveCallback saveCallback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(parseClassName(VideoEntry.class));
        query.getInBackground(videoEntry.getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    toParse(videoEntry, parseObject);
                    if (saveCallback != null) {
                        parseObject.saveInBackground(saveCallback);
                    } else {
                        parseObject.saveInBackground();
                    }
                } else {
                    if (saveCallback != null) saveCallback.done(e);
                }
            }
        });
    }

    public void update(final VideoSource videoSource, final SaveCallback saveCallback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(parseClassName(VideoSource.class));
        query.getInBackground(videoSource.getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    toParse(videoSource, parseObject);
                    if (saveCallback != null) {
                        parseObject.saveInBackground(saveCallback);
                    } else {
                        parseObject.saveInBackground();
                    }
                } else {
                    if (saveCallback != null) saveCallback.done(e);
                }
            }
        });
    }

    public void update(final VideoServer videoServer, final SaveCallback saveCallback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(parseClassName(VideoServer.class));
        query.getInBackground(videoServer.getObjectId(), new GetCallback<ParseObject>() {
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    toParse(videoServer, parseObject);
                    if (saveCallback != null) {
                        parseObject.saveInBackground(saveCallback);
                    } else {
                        parseObject.saveInBackground();
                    }
                } else {
                    if (saveCallback != null) saveCallback.done(e);
                }
            }
        });
    }

    public void getVideoEntry(String objectId, final CustomGetCallback<VideoEntry> callback) {
        get(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject != null) {
                    final VideoEntry videoEntry = toVideoEntry(parseObject);
                    getVideoSource(parseObject.getParseObject(VIDEO_SOURCE).getObjectId(), new CustomGetCallback<VideoSource>() {
                        @Override
                        public void done(VideoSource videoSource, ParseException e) {
                            videoEntry.setVideoSource(videoSource);
                            callback.done(videoEntry, e);
                        }
                    });
                } else {
                    callback.done(null, e);
                }
            }
        }, VideoEntry.class);
    }

    public void getVideoSource(String objectId, final CustomGetCallback<VideoSource> callback) {
        get(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject != null) {
                    final VideoSource videoSource = toVideoSource(parseObject);
                    getVideoServer(parseObject.getParseObject(VIDEO_SERVER).getObjectId(), new CustomGetCallback<VideoServer>() {
                        @Override
                        public void done(VideoServer videoServer, ParseException e) {
                            videoSource.setVideoServer(videoServer);
                            callback.done(videoSource, e);
                        }
                    });
                } else {
                    callback.done(null, e);
                }
            }
        }, VideoSource.class);
    }

    public void getVideoServer(String objectId, final CustomGetCallback<VideoServer> callback) {
        get(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                VideoServer videoServer = (parseObject != null) ? toVideoServer(parseObject) : null;
                callback.done(videoServer, e);
            }
        }, VideoServer.class);
    }

    public <T> void get(String objectId, GetCallback<ParseObject> getCallback, Class<T> clazz) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(parseClassName(clazz));
        query.getInBackground(objectId, getCallback);
    }

    public void allMovies(CustomFindCallback<VideoEntry> callback) {
        allVideoEntries(VideoEntry.VideoType.MOVIE, callback);
    }

    public void allEpisodes(CustomFindCallback<VideoEntry> callback) {
        allVideoEntries(VideoEntry.VideoType.TV_SHOW, callback);
    }

    public void allVideoEntries(VideoEntry.VideoType videoType, CustomFindCallback<VideoEntry> callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(parseClassName(VideoEntry.class));
        query.whereEqualTo(PARSE_USER, ParseUser.getCurrentUser());
        query.whereEqualTo(VIDEO_TYPE, videoType.name());
        query.findInBackground(new VideoEntryFindCallback(callback));
    }

    public void allVideoEntries(final CustomFindCallback<VideoEntry> callback) {
        all(VideoEntry.class, new VideoEntryFindCallback(callback));
    }

    public void allVideoSources(final CustomFindCallback<VideoSource> callback) {
        all(VideoSource.class, new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> list, ParseException e) {
                final List<VideoSource> videoSources = new ArrayList<VideoSource>();

                if (list != null && !list.isEmpty()) {
                    for (ParseObject po : list) {
                        final VideoSource videoSource = toVideoSource(po);
                        getVideoServer(po.getParseObject(VIDEO_SERVER).getObjectId(), new CustomGetCallback<VideoServer>() {
                            @Override
                            public void done(VideoServer videoServer, ParseException e) {
                                videoSource.setVideoServer(videoServer);
                                videoSources.add(videoSource);

                                if (videoSources.size() == list.size())
                                    callback.done(videoSources, e);
                            }
                        });
                    }
                } else {
                    callback.done(videoSources, e);
                }
            }
        });
    }

    public void allVideoServers(final CustomFindCallback<VideoServer> callback) {
        all(VideoServer.class, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                List<VideoServer> videoServers = new ArrayList<>();
                if (list != null) for (ParseObject po : list) videoServers.add(toVideoServer(po));
                callback.done(videoServers, e);
            }
        });
    }

    public <T> void all(Class<T> clazz, FindCallback<ParseObject> findCallback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(parseClassName(clazz));
        query.setLimit(1000); // FIXME: This limit is crap!!!!
        query.whereEqualTo(PARSE_USER, ParseUser.getCurrentUser());
        query.findInBackground(findCallback);
    }

    public ParseObject toParse(VideoServer videoServer) {
        ParseObject po = new ParseObject(parseClassName(VideoServer.class));
        toParse(videoServer, po);

        return po;
    }

    public void toParse(VideoServer videoServer, ParseObject po) {
        if (videoServer.getObjectId() != null) po.setObjectId(videoServer.getObjectId());

        po.put(HOST, videoServer.getHost());
        po.put(HTTP_URL, videoServer.getHttpUrl());
        po.put(PASSWORD, videoServer.getPassword());
        po.put(USERNAME, videoServer.getUsername());
        po.put(PARSE_USER, ParseUser.getCurrentUser());
    }

    private String parseClassName(Class clazz) {
        return clazz.getName().replaceAll("\\.", "_");
    }

    class VideoEntryFindCallback implements FindCallback<ParseObject> {

        CustomFindCallback<VideoEntry> callback;

        public VideoEntryFindCallback(CustomFindCallback<VideoEntry> callback) {
            this.callback = callback;
        }

        @Override
        public void done(final List<ParseObject> list, ParseException e) {
            final List<VideoEntry> videoEntries = new ArrayList<VideoEntry>();

            if (list != null && !list.isEmpty()) {
                for (ParseObject parseObject : list) {
                    final VideoEntry videoEntry = toVideoEntry(parseObject);
                    getVideoSource(parseObject.getParseObject(VIDEO_SOURCE).getObjectId(), new CustomGetCallback<VideoSource>() {
                        @Override
                        public void done(VideoSource videoSource, ParseException e) {
                            videoEntry.setVideoSource(videoSource);
                            videoEntries.add(videoEntry);

                            if (videoEntries.size() == list.size())
                                callback.done(videoEntries, e);
                        }
                    });
                }
            } else {
                callback.done(videoEntries, e);
            }
        }
    }

    public interface CustomFindCallback<T> {
        void done(List<T> list, ParseException e);
    }

    public interface CustomGetCallback<T> {
        void done(T object, ParseException e);
    }
    */
}
