package com.mediator.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.mediator.helpers.MediatorPrefs;

/**
 * Created by luispablo on 28/04/15.
 */
public class YouTubePlayer {

    private Context context;
    private String key;

    public YouTubePlayer(Context context, String key) {
        this.context = context;
        this.key = key;
    }

    public void play() {
        String url = String.format(MediatorPrefs.getString(context, MediatorPrefs.Key.YOUTUBE_URL), key);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }
}
