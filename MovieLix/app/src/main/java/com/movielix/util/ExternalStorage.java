package com.movielix.util;

import android.os.Environment;

public class ExternalStorage {

    private ExternalStorage() {}

    public static boolean isWritable() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
