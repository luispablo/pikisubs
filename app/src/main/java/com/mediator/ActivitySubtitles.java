package com.mediator;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;


public class ActivitySubtitles extends ActionBarActivity {

    TextView txtFilename;
    ListView listSubtitles;
    List<Subtitle> subtitles;
    ArrayAdapter<String> adapter;
    VideoEntry videoEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtitles);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtFilename = (TextView) findViewById(R.id.txtFilename);
        listSubtitles = (ListView) findViewById(R.id.listSubtitles);
        listSubtitles.setOnItemClickListener(new SubtitleClickListener());

        videoEntry = (VideoEntry) getIntent().getSerializableExtra("videoEntry");
        txtFilename.setText(videoEntry.getFilename());

        setTitle(R.string.title_activity_subtitles);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new TaskGetSubtitles(this) {
            @Override
            protected void onPostExecute(List<Subtitle> subtitles) {
                ActivitySubtitles.this.subtitles = subtitles;
                List<String> titles = Oju.reduce(subtitles, new Oju.Reducer<Subtitle, String>() {
                    @Override
                    public String reduce(Subtitle subtitle) {
                        return subtitle.getTitle().toUpperCase() +"\n--------\n\n"+ subtitle.getDescription() +"\n\n";
                    }
                });
                adapter = new ArrayAdapter<String>(ActivitySubtitles.this, android.R.layout.simple_list_item_1,
                        android.R.id.text1, titles);
                listSubtitles.setAdapter(adapter);
            }
        }.execute(videoEntry.getFilename());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_subtitles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class SubtitleClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Subtitle subtitle = subtitles.get(position);

            OnSubtitlesDecompressed next = new OnSubtitlesDecompressed();
            SubtitleDownloadCancelled cancelledListener = new SubtitleDownloadCancelled();
            TaskDownloadSubtitle task = new TaskDownloadSubtitle(ActivitySubtitles.this, next, cancelledListener);
            task.execute(subtitle);
        }
    }

    class SubtitleDownloadCancelled implements TaskCancelledListener<String> {

        @Override
        public void onCancelled(String s) {
            String message = String.format(getString(R.string.message_cannot_download), s);
            Toast.makeText(ActivitySubtitles.this, message, Toast.LENGTH_LONG).show();
        }
    }

    class OnSubtitlesDecompressed implements TaskDownloadSubtitle.OnTaskFinished {

        @Override
        public void doSomething(List<File> files) {
            if (files.isEmpty()) {
                Toast.makeText(ActivitySubtitles.this, R.string.message_no_files, Toast.LENGTH_LONG).show();
            } else {
                OnSubtitlesUploaded next = new OnSubtitlesUploaded();
                TaskUploadSubtitles task = new TaskUploadSubtitles(ActivitySubtitles.this, videoEntry, next);

                if (files.size() > 1) {
                    FragmentSelectSubtitleDialog selectDialog = new FragmentSelectSubtitleDialog();
                    selectDialog.setFiles(files);
                    selectDialog.setDoneListener(new SubtitleSelectedListener(task));
                    selectDialog.show(getSupportFragmentManager(), null);
                } else {
                    task.execute(files.iterator().next());
                }
            }
        }
    }

    class SubtitleSelectedListener implements FragmentDoneListener<File> {

        private TaskUploadSubtitles task;

        public SubtitleSelectedListener(TaskUploadSubtitles task) {
            this.task = task;
        }

        @Override
        public void onDone(File file) {
            task.execute(file);
        }
    }

    class OnSubtitlesUploaded implements TaskUploadSubtitles.OnTaskDone {

        @Override
        public void uploaded(File file) {
            Toast.makeText(ActivitySubtitles.this, R.string.message_subs_uploaded, Toast.LENGTH_LONG).show();
            file.delete();
        }
    }
}