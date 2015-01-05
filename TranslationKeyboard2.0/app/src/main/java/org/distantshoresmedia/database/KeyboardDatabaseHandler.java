package org.distantshoresmedia.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.distantshoresmedia.keyboard.KeyboardSwitcher;
import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.model.BaseKeyboard;
import org.distantshoresmedia.translationkeyboard20.KeyboardDownloader;
import org.distantshoresmedia.translationkeyboard20.UpdateFragment;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Fechner on 12/18/14.
 */
public class KeyboardDatabaseHandler {

    private static final String TAG = "org.distantshoresmedia.model.translationkeyboard20";

    private static Context currentContext;

    //region Public Setup

    /**
     *
     * @param context
     * @return boolean of whether or not it initialized
     */
    public static boolean initializeDatabaseIfNecessary(Context context){

        currentContext = context;

        boolean hasBeenSaved = KeyboardFileLoader.hasSavedKeyboards(context);
        if(hasBeenSaved){
            System.out.println("KeyboardsAlreadySaved");
        }
        else{
            System.out.println("Keyboards Will Be Initialized");
            KeyboardFileLoader.initializeKeyboards(context);
        }

        boolean didInitialize = ! hasBeenSaved;
        return didInitialize;
    }
    //endregion

    //region Public General Use

    public static void installedKeyboardHasState(AvailableKeyboard keyboard, boolean isChecked){

        KeyboardDataHandler.setKeyboardAvailabilityState(currentContext, keyboard, isChecked);
    }

    public static boolean hasDownloadedKeyboard(AvailableKeyboard keyboard){

        String id = Long.toString(keyboard.getId());
        boolean hasKeyboard = KeyboardDataHandler.getDownloadedKeyboardsDictionary(currentContext).containsKey(id);

        return hasKeyboard;
    }

    public static boolean hasInstalledKeyboard(AvailableKeyboard keyboard){

        String id = Long.toString(keyboard.getId());
        boolean hasKeyboard = KeyboardDataHandler.getInstalledKeyboardDictionary(currentContext).containsKey(id);

        return hasKeyboard;
    }

    public static BaseKeyboard getKeyboardWithID(String id){

        String json = KeyboardFileLoader.loadKeyboardFromFiles(currentContext, id);

        BaseKeyboard keyboard = BaseKeyboard.getKeyboardFromJsonString(json);

        return keyboard;
    }
    public static String getKeyboardIdWithLocal(Locale locale){

        return KeyboardDataHandler.findKeyboardIdForLocal(currentContext, locale);
    }

    public static AvailableKeyboard[] getInstalledKeyboards(){

        return KeyboardDataHandler.getInstalledKeyboardsArray(currentContext);
    }

    public static boolean updateKeyboardsDatabaseWithJSON(Context context, String newKeyboardsJson){

        UpdateFragment.getSharedInstance().setProgress(20, "Comparing Updates");
        Log.i(TAG, "Is updating available keyboards.");

        double currentUpdatedDate = KeyboardFileLoader.getUpdatedDate(context);
        double newUpdatedDate = AvailableKeyboard.getUpdatedTimeFromJSONString(newKeyboardsJson);

        if(Math.round(currentUpdatedDate) >= Math.round(newUpdatedDate)){
            Log.i(TAG, "keyboards up to date");
            UpdateFragment.getSharedInstance().endProgress(true, "Up To Date");
            return true;
        }
        else {
            Log.i(TAG, "Keyboards will be updated");
            UpdateFragment.getSharedInstance().setProgress(30, "Updating");
            return updateKeyboards(context);
        }
    }

    public static void updateOrSaveKeyboard(Context context, String json){

        KeyboardFileLoader.saveKeyboardJson(context, json);
    }

    //endregion


    //region Private General Use


    private static void didInstallKeyboard() {

        KeyboardDataHandler.invalidateLoadedKeyboardsAvailable();

        KeyboardSwitcher.getInstance().makeKeyboards(true);
        UpdateFragment.getSharedInstance().endProgress(true, "Updated");
    }

    private static boolean updateKeyboards(Context context){

        Map<String, AvailableKeyboard> availableKeyboards = KeyboardDataHandler.getAvailableKeyboardsDictionary(context);
        Map<String, AvailableKeyboard> downloadedKeyboards = KeyboardDataHandler.getDownloadedKeyboardsDictionary(context);
        Map<String, AvailableKeyboard> installedKeyboards = KeyboardDataHandler.getInstalledKeyboardDictionary(context);

        boolean isUpdated = KeyboardDataHandler.keyboardListsAreCurrentWithEachOther(availableKeyboards, downloadedKeyboards);

        Log.i(TAG, "Will check for keyboardUpdates, isUpdated: " + isUpdated);
        Log.i(TAG, "availableKeyboards count: " + availableKeyboards.size());
        Log.i(TAG, "downloadedKeyboards count: " + downloadedKeyboards.size());
        Log.i(TAG, "installedKeyboards count: " + installedKeyboards.size());


        if(! isUpdated) {
            System.out.println("is updating downloaded Keyboards");

            ArrayList<String> deleteKeys = new ArrayList<String>();

            for (String key : downloadedKeyboards.keySet()){
                if(!availableKeyboards.containsKey(key)){
                    deleteKeys.add(key);
                }
            }

            if(deleteKeys.size() > 0){
                for(String key : deleteKeys){
                    if(downloadedKeyboards.containsKey(key)){
                        KeyboardDataHandler.deleteKeyboardWithId(context, key);
                    }
                }
            }

            for (String key : availableKeyboards.keySet()){

                if(downloadedKeyboards.keySet().contains(key) && ! TimeHelper.isCurrent(downloadedKeyboards.get(key).getUpdated(), availableKeyboards.get(key).getUpdated())){
                    Log.i(TAG, "Is downloading updated keyboard with id: " + key);
                    KeyboardDataHandler.updateAvailableKeyboard(context, availableKeyboards.get(key));

                    UpdateFragment.getSharedInstance().setProgress(40, "Downloading new keyboard for: " + availableKeyboards.get(key).getLanguageName());
                    Log.i(TAG, "Will Download/update keyboard id: " + availableKeyboards.get(key).getId());
                    KeyboardDownloader.getSharedInstance().downloadKeyboard(Long.toString(availableKeyboards.get(key).getId()));
                }
            }
        }

        KeyboardDataHandler.updateAvailableKeyboards(context);

        return isUpdated;
    }

    //endregion

}
