package com.mediator;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by luispablo on 18/05/15.
 */
public class ApplicationPikisubs extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "loClredfXgT3FQr3TkfzkaX84XxR21Fg4uUuOeRb", "ZkTSOIlTfuEcfk1G7xY3MdqGv1FcGMczHMR4vqok");
    }
}
