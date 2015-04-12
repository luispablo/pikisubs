package com.mediator.helpers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by luispablo on 11/04/15.
 */
public class HelperAndroid {

    public static List<File> decompressZip(File zipFile, String destination) {
        List<File> files = new ArrayList<>();

        try  {
            FileInputStream fin = new FileInputStream(zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;

            while ((ze = zin.getNextEntry()) != null) {
                String filename = destination + File.separator + ze.getName();
                FileOutputStream fout = new FileOutputStream(filename);

                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }

                zin.closeEntry();
                fout.close();
                files.add(new File(filename));
            }
            zin.close();
        } catch(Exception e) {
            Logger.e("Decompress", "unzip", e);
        }

        return files;
    }

    public static <T extends Activity> void start(Activity caller, Class<T> activityClass, Bundle params) {
        Intent intent = new Intent(caller, activityClass);
        if (params != null) {
            intent.putExtras(params);
        }
        caller.startActivity(intent);
    }
}