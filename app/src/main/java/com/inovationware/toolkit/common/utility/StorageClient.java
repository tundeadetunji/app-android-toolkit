package com.inovationware.toolkit.common.utility;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import com.inovationware.toolkit.application.factory.Factory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class StorageClient {
    private Factory factory = Factory.getInstance();

    private static StorageClient instance;
    private Context context;

    private StorageClient(Context context) {
        this.context = context;
    }

    public static StorageClient getInstance(Context context) {
        if (instance == null) instance = new StorageClient(context);
        return instance;
    }

    public void writeText(String s, String filename) {
        securePermissions();

        try (FileOutputStream stream = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), filename))) {
            stream.write(s.getBytes());
        } catch (IOException ignored) {
        }
    }

    public void writeText(String s, String filename, String successMessage, String failureMessage) {
        securePermissions();

        try (FileOutputStream stream = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), filename))) {
            stream.write(s.getBytes());
            factory.device.tell(successMessage, context);
        } catch (IOException ignored) {
            factory.device.tell(failureMessage, context);
        }
    }

    public void securePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                context.startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
            }
        }
    }

    public File writeAudioScratchFile(String content) throws IOException {
        writeText(content, "Audio.mp3");
        return null;
    }
    public File writeAudioScratchFileEx(Context context, byte[] content, String filename) throws IOException {
        File tempFile = new File(context.getCacheDir(), filename);
        FileOutputStream stream = new FileOutputStream(tempFile);
        stream.write(content);
        stream.close();
        return tempFile;
    }


}
