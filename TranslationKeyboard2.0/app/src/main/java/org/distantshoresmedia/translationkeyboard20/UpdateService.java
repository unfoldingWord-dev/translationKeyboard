package org.distantshoresmedia.translationkeyboard20;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.distantshoresmedia.database.KeyboardDatabaseHandler;
import org.distantshoresmedia.activities.Main;
import org.distantshoresmedia.utilities.DownloadUtil;

import java.util.ArrayList;


/**
 * Created by Acts Media Inc on 11/12/14.
 */
public class UpdateService extends Service {

    private static final String TAG = "UpdateService";

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private String downloadUrl;
    private boolean serviceState = false;
    private boolean onComplete = true;

    public static final String BROAD_CAST_DOWN_COMP = "org.distantshoresmedia.translationkeyboard20.DOWNLOAD_COMPLETED";
    public static final String BROAD_CAST_DOWN_ERROR = "org.distantshoresmedia.translationkeyboard20.DOWNLOAD_WHILE_ERROR";

    static final String kBaseURL = "http://remote.actsmedia.com/api/";
    static final String kVersionUrlTag = "v2/";
    static final String kKeyboardUrlTag = "keyboard/";

    static final String kIdTag = "id";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        serviceState = true;
        HandlerThread thread = new HandlerThread("ServiceStartArguments", 1);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Bundle extra = intent.getExtras();
            if (extra != null) {
                String downloadUrl = extra.getString("downloadUrl");

                this.downloadUrl = downloadUrl;
            }
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);
        } catch (Exception e) {

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            // Get current list of languages

            JsonObject jObject = DownloadUtil.downloadJson(UpdateService.getKeyboardUrl());

                String failText = "Invalid update. Please try again";
//                try{
//                     = new JSONObject(json);
                    JsonArray keysArray = jObject.getAsJsonArray("keyboards");

                    // this means it's the available keyboards json
                    if(keysArray != null){
                        ArrayList<String> ids = KeyboardDatabaseHandler.updateKeyboardsDatabaseWithJSON(Main.getAppContext(), jObject.toString());
                        if(ids != null) {
                            for (String id : ids) {
                                JsonObject newJsonObject = DownloadUtil.downloadJson(UpdateService.getKeyboardUrl(id));
                                KeyboardDatabaseHandler.updateOrSaveKeyboard(Main.getAppContext(), newJsonObject.toString());
                            }
                        }
                    }
//                }
//                catch (JSONException e){

//                        Log.e(TAG, " Second JSONException UpdateService: " + e.toString());
//                            UpdateFragment.getSharedInstance().endProgress(false, failText);
//                }



            if (onComplete)
                getApplicationContext().sendBroadcast(new Intent(UpdateService.BROAD_CAST_DOWN_COMP));

        }
    }

    static public String getKeyboardUrl() {
        return kBaseURL + kVersionUrlTag + kKeyboardUrlTag;
    }

    static public String getKeyboardUrl(String keyboardKey) {
        return kBaseURL + kVersionUrlTag + kKeyboardUrlTag + keyboardKey;
    }
}
