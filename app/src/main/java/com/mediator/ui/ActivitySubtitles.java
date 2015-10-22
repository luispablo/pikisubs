package com.mediator.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mediator.R;
import com.mediator.helpers.HelperDAO;
import com.mediator.helpers.Oju;
import com.mediator.model.Subtitle;
import com.mediator.model.TVShow;
import com.mediator.model.VideoEntry;
import com.mediator.sources.SubtitlesSource;
import com.mediator.tasks.TaskCancelledListener;
import com.mediator.tasks.TaskDownloadSubtitle;
import com.mediator.tasks.TaskGetSubtitles;
import com.mediator.tasks.TaskProgressedListener;
import com.mediator.tasks.TaskUploadSubtitles;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.mediator.helpers.TinyLogger.d;


public class ActivitySubtitles extends ActionBarActivity {

    TextView txtSearchText;
    TextView txtFilename;
    ListView listSubtitles;
    List<Subtitle> subtitles;
    AdapterSubtitles adapter;
    VideoEntry videoEntry;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtitles);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtSearchText = (TextView) findViewById(R.id.txtSearchText);
        txtFilename = (TextView) findViewById(R.id.txtFilename);
        listSubtitles = (ListView) findViewById(R.id.listSubtitles);
        listSubtitles.setOnItemClickListener(new SubtitleClickListener());

        videoEntry = (VideoEntry) getIntent().getSerializableExtra("videoEntry");

        txtSearchText.setText(videoEntry.suggestedSearchText());
        txtFilename.setText(videoEntry.getFilename());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.message_wait_please));
        progressDialog.setIndeterminate(true);

        setTitle(R.string.title_activity_subtitles);
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        Intent intent = new Intent();

        if (videoEntry.isMovie()) {
            intent.setClass(this, ActivityMain.class);
        } else {
            intent.setClass(this, ActivityEpisodes.class);
            intent.putExtra(TVShow.class.getName(), videoEntry.buildTVShow(this));
        }

        return intent;
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressDialog.setTitle(R.string.title_progress_subtitles);
        progressDialog.show();

        TaskProgressedListener<SubtitlesSource> listener = new TaskProgressedListener<SubtitlesSource>() {
            @Override
            public void onProgressed(final SubtitlesSource subtitlesSource) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String messageSearchingIn = getString(R.string.message_searching_in);
                        progressDialog.setMessage(String.format(messageSearchingIn, subtitlesSource.getName()));
                    }
                });
            }
        };

        new TaskGetSubtitles(listener) {
            @Override
            protected void onPostExecute(List<Subtitle> subtitles) {
                Collections.sort(subtitles, new SubtitleComparator(videoEntry));
                ActivitySubtitles.this.subtitles = subtitles;
                List<String> titles = Oju.map(subtitles, new Oju.UnaryOperator<Subtitle, String>() {
                    @Override
                    public String operate(Subtitle subtitle) {
                        return subtitle.getTitle().toUpperCase() + "\n--------\n\n" + subtitle.getDescription() + "\n\n";
                    }
                });
                adapter = new AdapterSubtitles(ActivitySubtitles.this, subtitles);
                listSubtitles.setAdapter(adapter);

                progressDialog.dismiss();
            }
        }.execute(videoEntry);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_subtitles, menu);
        return true;
    }

    class SubtitleClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            progressDialog.setTitle(R.string.title_progress_subtitle_download);
            progressDialog.show();
            d("dialogo mostrado");

            Subtitle subtitle = subtitles.get(position);

            OnSubtitlesDecompressed next = new OnSubtitlesDecompressed();
            SubtitleDownloadCancelled cancelledListener = new SubtitleDownloadCancelled();
            TaskDownloadSubtitle task = new TaskDownloadSubtitle(ActivitySubtitles.this, next, cancelledListener);
            task.execute(subtitle);
            d("tarea disparada");
        }
    }

    class SubtitleDownloadCancelled implements TaskCancelledListener<String> {

        @Override
        public void onCancelled(String s) {
            progressDialog.dismiss();

            String message = String.format(getString(R.string.message_cannot_download), s);
            Toast.makeText(ActivitySubtitles.this, message, Toast.LENGTH_LONG).show();
        }
    }

    class OnSubtitlesDecompressed implements TaskDownloadSubtitle.OnTaskFinished {

        @Override
        public void doSomething(List<File> files) {
            if (files.isEmpty()) {
                progressDialog.dismiss();

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
            videoEntry.setHasSubs(true);

            HelperDAO helperDAO = new HelperDAO(getBaseContext());
            helperDAO.update(videoEntry);

            progressDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ActivitySubtitles.this, R.string.message_subs_uploaded, Toast.LENGTH_LONG).show();
                }
            });
            file.delete();
        }
    }

    class SubtitleComparator implements Comparator<Subtitle> {

        private Set<String> terms;

        public SubtitleComparator(VideoEntry videoEntry) {
            terms = Oju.lowerCaseTerms(videoEntry.getFilename());
        }

        @Override
        public int compare(Subtitle sub1, Subtitle sub2) {
            Set<String> termsSub1 = Oju.lowerCaseTerms(sub1.getDescription());
            Set<String> termsSub2 = Oju.lowerCaseTerms(sub2.getDescription());
            int matches1 = Oju.matches(terms, termsSub1);
            int matches2 = Oju.matches(terms, termsSub2);

            if (matches1 > matches2) {
                return -1;
            } else if (matches2 > matches1) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}