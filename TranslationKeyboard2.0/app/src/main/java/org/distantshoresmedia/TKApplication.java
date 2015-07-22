package org.distantshoresmedia;

import android.app.Application;

import com.door43.tools.reporting.GlobalExceptionHandler;
import com.door43.tools.reporting.Logger;

import org.distantshoresmedia.translationkeyboard20.R;

import java.io.File;

/**
 * Created by Fechner on 7/22/15.
 */
public class TKApplication extends Application {

    public static final String STACKTRACE_DIR = "stacktrace";

    @Override
    public void onCreate() {
        super.onCreate();

        File dir = new File(getExternalCacheDir(), STACKTRACE_DIR);
        GlobalExceptionHandler.register(dir);

        // configure logger
        int minLogLevel = 1;
        Logger.configure(new File(getExternalCacheDir(), "log.txt"), Logger.Level.getLevel(minLogLevel));
    }
}
