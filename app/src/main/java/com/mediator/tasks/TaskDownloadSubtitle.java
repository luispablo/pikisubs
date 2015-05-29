package com.mediator.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.mediator.helpers.HelperAndroid;
import com.mediator.helpers.MediatorPrefs;
import com.mediator.model.Subtitle;
import com.mediator.retrofit.RetrofitServiceSubdivxDownload;
import com.mediator.sources.Subdivx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.exception.RarException;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static com.mediator.helpers.TinyLogger.e;

/**
 * Created by luispablo on 11/04/15.
 */
public class TaskDownloadSubtitle extends AsyncTask<Subtitle, Void, List<File>> {

    private Context context;
    private OnTaskFinished next;
    private TaskCancelledListener<String> cancelledListener;
    private String compressedFilename;

    public TaskDownloadSubtitle(Context context, OnTaskFinished next, TaskCancelledListener<String> listener) {
        this.context = context;
        this.next = next;
        this.cancelledListener = listener;
    }

    @Override
    protected void onPostExecute(List<File> files) {
        next.doSomething(files);
    }

    @Override
    protected void onCancelled() {
        cancelledListener.onCancelled(compressedFilename);
    }

    @Override
    protected List<File> doInBackground(Subtitle... params) {
        List<File> files = new ArrayList<>();
        Subtitle subtitle = params[0];

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(MediatorPrefs.getString(context, MediatorPrefs.Key.SUBDIVX_DOWNLOAD_URL))
                .build();

        String link = Subdivx.findRealLink(subtitle);
        Uri linkUri = Uri.parse(link);
        RetrofitServiceSubdivxDownload service = restAdapter.create(RetrofitServiceSubdivxDownload.class);
        Response response = service.download(linkUri.getQueryParameter("id"), linkUri.getQueryParameter("u"));
        TypedByteArray byteArray = (TypedByteArray) response.getBody();
        compressedFilename = Uri.parse(response.getUrl()).getLastPathSegment().toLowerCase();

        try {
            File compressedFile = File.createTempFile(compressedFilename, "", context.getCacheDir());
            FileOutputStream fos = new FileOutputStream(compressedFile);
            fos.write(byteArray.getBytes());
            fos.close();

            if (compressedFilename.endsWith("rar")) {
                Archive archive = new Archive(compressedFile);
                files.addAll(archive.extractAllFiles(context.getCacheDir()));
            } else if(compressedFilename.endsWith("zip")) {
                files.addAll(HelperAndroid.decompressZip(compressedFile, context.getCacheDir().getAbsolutePath()));
            } else {
                cancel(true);
            }

            compressedFile.delete();

        } catch (IOException e) {
            e(e);
        } catch (RarException e) {
            e(e);
        }

        return files;
    }

    public interface OnTaskFinished {
        void doSomething(List<File> files);
    }
}
