package org.distantshoresmedia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.distantshoresmedia.database.KeyboardDatabaseHandler;
import org.distantshoresmedia.database.KeyboardFileLoader;

/**
 * Created by Fechner on 7/13/15.
 */
public class UpdateBroadcastReceiver extends BroadcastReceiver
{
    private static final String TAG = "UpdateBroadcastReceiver";
    @Override
    public void onReceive(final Context context,final Intent intent)
    {
        final String msg="intent:"+intent+" action:"+intent.getAction();
        Log.d(TAG, "APK was updated: " + msg);
        KeyboardDatabaseHandler.initializeDatabaseIfNecessary(context);
//        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
