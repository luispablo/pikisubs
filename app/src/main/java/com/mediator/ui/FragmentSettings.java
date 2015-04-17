package com.mediator.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.mediator.R;
import com.mediator.tasks.TaskGetVideos;

/**
 * Created by luispablo on 17/04/15.
 */
public class FragmentSettings extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    public static FragmentSettings newInstance() {
        FragmentSettings fragment = new FragmentSettings();

        return fragment;
    }

    public FragmentSettings() {
    }
}