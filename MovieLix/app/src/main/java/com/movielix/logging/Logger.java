package com.movielix.logging;

import java.io.File;
import java.io.IOException;

public class Logger {

    private Logger() {}

    public static void init(File output) throws IOException {
        // Clear the old file.
        Runtime.getRuntime().exec("logcat -c");
        Runtime.getRuntime().exec("logcat -f " + output);
    }
}
