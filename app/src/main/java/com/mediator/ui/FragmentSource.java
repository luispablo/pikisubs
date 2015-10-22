package com.mediator.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.mediator.R;
import com.mediator.helpers.HelperDAO;
import com.mediator.helpers.Oju;
import com.mediator.model.VideoSource;
import com.squareup.otto.Bus;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

public class FragmentSource extends Fragment {

    @InjectView(android.R.id.list)
    AbsListView listViewSources;
    ArrayAdapter<String> adapter;
    List<VideoSource> videoSources;
    Bus bus;

    public static FragmentSource newInstance() {
        FragmentSource fragment = new FragmentSource();
        return fragment;
    }

    public FragmentSource() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.source_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_source:
                FragmentSourceDialog sourceDialog = new FragmentSourceDialog() {
                    @Override
                    public void onDone() {
                        loadList();
                    }
                };
                sourceDialog.show(getFragmentManager(), null);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_source, container, false);

        ButterKnife.inject(this, view);

        bus = new Bus();
        bus.register(this);

        setHasOptionsMenu(true);
        loadList();

        return view;
    }

    @OnItemClick(android.R.id.list)
    public void onItemClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(VideoSource.class.getName(), videoSources.get(position));

        FragmentSourceDialog sourceDialog = new FragmentSourceDialog() {
            @Override
            public void onDone() {
                loadList();
            }
        };
        sourceDialog.setArguments(bundle);
        sourceDialog.show(getFragmentManager(), null);
    }

    private void loadList() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.title_wait_please);
        progressDialog.setMessage(getString(R.string.message_loading_data));
        progressDialog.show();

        HelperDAO helperDAO = new HelperDAO(getActivity());
        videoSources = helperDAO.all(VideoSource.class);

        List<String> sourcesPaths = Oju.map(videoSources, new Oju.UnaryOperator<VideoSource, String>() {
            @Override
            public String operate(VideoSource videoSource) {
                return videoSource.getSshPath();
            }
        });
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sourcesPaths);
        listViewSources.setAdapter(adapter);

        progressDialog.dismiss();

        if (videoSources == null || videoSources.isEmpty()) {
            Toast.makeText(getActivity(), R.string.message_no_data, Toast.LENGTH_SHORT).show();
        }
    }
}
