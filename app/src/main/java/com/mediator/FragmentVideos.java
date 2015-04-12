package com.mediator;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class FragmentVideos extends Fragment implements AbsListView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;
    private AbsListView listView;
    private ListAdapter adapter;
    private List<VideoEntry> videoEntries;

    public static FragmentVideos newInstance() {
        FragmentVideos fragment = new FragmentVideos();
        return fragment;
    }

    public FragmentVideos() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayAdapter<Object>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, Collections.emptyList());
        buildAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        // Set the adapter
        listView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) listView).setAdapter(adapter);

        // Set OnItemClickListener so we can be notified on item clicks
        listView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("videoEntry", videoEntries.get(position));
        HelperAndroid.start(getActivity(), ActivitySubtitles.class, bundle);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
//            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = listView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    private void buildAdapter() {
        VideosDownloaded taskFinishedListener = new VideosDownloaded();
        TaskGetVideos task = new TaskGetVideos(getActivity(), TaskGetVideos.Filter.WITHOUT_SUBS, taskFinishedListener);
        task.execute(MediatorPrefs.sources(getActivity()).toArray(new String[]{}));
    }

    class VideosDownloaded implements TaskGetVideos.OnTaskFinished {

        @Override
        public void videosDownloaded(List<VideoEntry> videoEntries) {
            FragmentVideos.this.videoEntries = videoEntries;
            List<String> filenames = Oju.reduce(videoEntries, new Oju.Reducer<VideoEntry, String>() {
                @Override
                public String reduce(VideoEntry videoEntry) {
                    return videoEntry.getFilename();
                }
            });
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                                                android.R.id.text1, filenames);
            listView.setAdapter(adapter);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }
}