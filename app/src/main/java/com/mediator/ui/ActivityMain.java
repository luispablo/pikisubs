package com.mediator.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mediator.R;
import com.mediator.helpers.HelperAndroid;
import com.mediator.tasks.TaskGetVideos;
import com.mediator.tasks.TaskUpdateLocalDB;

public class ActivityMain extends ActionBarActivity
        implements FragmentNavigationDrawer.NavigationDrawerCallbacks,
                    FragmentVideos.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    FragmentNavigationDrawer navDrawerFragment;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navDrawerFragment = (FragmentNavigationDrawer) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        navDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment fragment = null;

        title = FragmentNavigationDrawer.drawerItems[position];

        switch (position) {
            case 0:
                fragment = FragmentLocalVideos.newInstance();
                break;
            case 1:
                fragment = FragmentVideos.newInstance(TaskGetVideos.Filter.WITHOUT_SUBS);
                break;
            case 2:
                fragment = FragmentVideos.newInstance(TaskGetVideos.Filter.WITH_SUBS);
                break;
            case 3:
                fragment = FragmentSource.newInstance();
                break;
            case 4:
                fragment = FragmentSettings.newInstance();
                break;
        }

        if (fragment != null) {
            // update the main content by replacing fragments
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(HelperAndroid.getStringByName(this, title));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String id) {
    }
}