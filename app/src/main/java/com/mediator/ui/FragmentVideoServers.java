package com.mediator.ui;

import static com.mediator.helpers.TinyLogger.*;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mediator.R;
import com.mediator.helpers.HelperSnappyDB;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoServer;
import com.snappydb.SnappydbException;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * Created by luispablo on 26/04/15.
 */
public class FragmentVideoServers extends Fragment {

    @InjectView(R.id.listViewVideoServers)
    ListView listViewVideoServers;
    List<VideoServer> videoServers;

    public static FragmentVideoServers newInstance() {
        FragmentVideoServers fragmentVideoServers = new FragmentVideoServers();

        return fragmentVideoServers;
    }

    public FragmentVideoServers() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_servers_list, container, false);

        ButterKnife.inject(this, view);
        setHasOptionsMenu(true);
        loadList();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_video_servers, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                FragmentVideoServerDialog fragment = new FragmentVideoServerDialog() {
                    @Override
                    public void onDone() {
                        loadList();
                    }
                };
                fragment.show(getFragmentManager(), null);
                
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadList() {
        try {
            HelperSnappyDB helperSnappyDB = new HelperSnappyDB(getActivity());

            videoServers = helperSnappyDB.all(VideoServer.class);
            List<String> videoServersNames = Oju.map(videoServers, new Oju.UnaryOperator<VideoServer, String>() {
                @Override
                public String operate(VideoServer videoServer) {
                    return videoServer.getHost();
                }
            });
            listViewVideoServers.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, videoServersNames));

            helperSnappyDB.close();
        } catch (SnappydbException e) {
            e(e);
        }
    }

    @OnItemClick(R.id.listViewVideoServers)
    public void onVideoServerClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(VideoServer.class.getName(), videoServers.get(position));

        FragmentVideoServerDialog fragment = new FragmentVideoServerDialog() {
            @Override
            public void onDone() {
                loadList();
            }
        };
        fragment.setArguments(bundle);
        fragment.show(getFragmentManager(), null);
    }
}
