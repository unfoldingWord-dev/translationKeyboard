package org.distantshoresmedia.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import org.distantshoresmedia.tasks.JsonDownloadTask;
import org.distantshoresmedia.tasks.UpdateAvailableKeyboardsRunnable;
import org.distantshoresmedia.utilities.TKPreferenceManager;
import org.distantshoresmedia.utilities.URLHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Acts Media Inc on 11/12/14.
 */
public class TKUpdater extends Service {

    private static final String TAG = "UpdateService";

    public static final String BROAD_CAST_DOWN_COMP = "org.unfoldingword.mobile.DOWNLOAD_COMPLETED";
    public static final String KEYBOARDS_JSON_KEY = "keyboards";
    public static final String UPDATED_AT_JSON_KEY = "updatedAt";

    private Looper mServiceLooper;
    private Handler mServiceHandler;


    int numberRunning = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected TKUpdater getThis(){
        return this;
    }
    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("DataDownloadServiceThread", 2);

        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new Handler(mServiceLooper);

        super.onCreate();
    }

    public void addRunnable(Runnable runnable){

        numberRunning++;
        mServiceHandler.post(runnable);
    }

    public void runnableFinished(){

        numberRunning--;
        Log.d(TAG, "a runnable was finished. current Number: " + numberRunning);
        if(numberRunning == 0){
            stopService();
        }
    }

    protected void stopService(){
        getApplicationContext().sendBroadcast(new Intent(BROAD_CAST_DOWN_COMP));
        this.stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        addRunnable(new UpdateRunnable());
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    class UpdateRunnable implements Runnable {

        @Override
        public void run() {

            new JsonDownloadTask(new JsonDownloadTask.DownloadTaskListener() {
                @Override
                public void downloadFinishedWithJson(String jsonString) {

                    try{
                        JSONObject json = new JSONObject(jsonString);
                        long lastModified = json.getLong(UPDATED_AT_JSON_KEY);

                        if(true){//lastModified > currentUpdated) {
                            TKPreferenceManager.setLastUpdatedDate(getApplicationContext(), lastModified);
                            addRunnable(new UpdateAvailableKeyboardsRunnable(json.getJSONArray(KEYBOARDS_JSON_KEY), getThis()));
                        }
                        runnableFinished();
                    } catch (JSONException e){
                        e.printStackTrace();
                        runnableFinished();
                    }
                }
            }).execute(URLHelper.getKeyboardUrl());
        }
    }
}
