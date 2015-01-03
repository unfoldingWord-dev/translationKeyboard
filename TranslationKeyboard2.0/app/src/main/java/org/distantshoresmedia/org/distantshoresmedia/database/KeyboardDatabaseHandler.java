package org.distantshoresmedia.org.distantshoresmedia.database;

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

    private static final String TAG = "KeyboardDatabaseHandler";

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
            initializeKeyboards(context);
        }

        boolean didInitialize = ! hasBeenSaved;
        return didInitialize;
    }
    //endregion

    //region Public General Use

    public static String getKeyboardIdWithLocal(Locale locale){

        updateCurrentKeyboards();

        for(AvailableKeyboard keyboard : currentKeyboards){

            Locale keyboardLocal = keyboard.getKeyboardAsLocale();

            String localeLanguage = locale.getLanguage();
            String keyboardCountry = keyboardLocal.getCountry();

            if(localeLanguage.compareToIgnoreCase(keyboardCountry) == 0){

                return Long.toString(keyboard.getId());
            }
            System.out.println("Desired locale: " + locale.toString() + " keyboardLocal: " + keyboardLocal.toString());

        }

        return "1";
    }


    private static void updateCurrentKeyboards(){

            Map<String, AvailableKeyboard> installedKeyboards = getInstalledKeyboardDictionary();

            AvailableKeyboard[] keyboards = new AvailableKeyboard[installedKeyboards.size()];

            int i = installedKeyboards.size() - 1;
            for(String key : installedKeyboards.keySet()){

                keyboards[i] = installedKeyboards.get(key);
                i--;
            }

            currentKeyboards = keyboards;
    }

    public static AvailableKeyboard getCurrentKeyboard(){

        updateCurrentKeyboards();
        return currentKeyboards[currentKeyboardIndex];
    }

    public static AvailableKeyboard[] getInstalledKeyboards(){

        Map<String, AvailableKeyboard> availableKeyboards = getInstalledKeyboardDictionary();

        AvailableKeyboard[] keyboards = new AvailableKeyboard[availableKeyboards.size()];

        int i = 0;
        for( AvailableKeyboard keyboard : availableKeyboards.values()){

            keyboards[i] = keyboard;
            i++;
        }

        return keyboards;
    }

    public static boolean updateKeyboardsDatabaseWithJSON(Context context, String newKeyboardsJson){

        UpdateFragment.getSharedInstance().setProgress(20, "Comparing Updates");
        Log.i(TAG, "Is updating available keyboards.");

        String currentKeyboards = getJSONStringForAvailableKeyboards(context);

        double currentUpdatedDate = AvailableKeyboard.getUpdatedTimeFromJSONString(currentKeyboards);
        double newUpdatedDate = AvailableKeyboard.getUpdatedTimeFromJSONString(newKeyboardsJson);

        if(Math.round(currentUpdatedDate) >= Math.round(newUpdatedDate)){
            Log.i(TAG, "keyboards up to date");
            UpdateFragment.getSharedInstance().endProgress(true, "Up To Date");
            return true;
        }
        else {
            Log.i(TAG, "Keyboards will be updated");
            UpdateFragment.getSharedInstance().setProgress(30, "Updating");
            return updateKeyboards(context, newKeyboardsJson);
        }
    }

    public static boolean updateOrSaveKeyboard(Context context, String keyboardJSON){

        UpdateFragment.getSharedInstance().setProgress(60, "Saving Keyboard" );

        String fileName = getKeyboardFileName(keyboardJSON);

        Log.i(TAG, "Attempting to save Keyboard named: " + fileName);
        saveFile(keyboardJSON, fileName, context);

        String keyboardID = Long.toString(BaseKeyboard.getKeyboardIDFromJSONString(keyboardJSON));

        didInstallKeyboardWithId(keyboardID);

        return true;
    }

    //endregion

    //region Private Setup

    /**
     *
     * @param context
     */
    private static void initializeKeyboards(Context context){

        saveDefaultKeyboardsFile(context, getAvailableKeyboardFileName());
        saveDefaultKeyboardsFile(context, getDownloadedKeyboardFileName());
        saveDefaultKeyboardsFile(context, getInstalledKeyboardFileName());

        String defaultKeyboardJSONString = getDefaultFileString(context, getDefaultKeyboardFileName());
        if(defaultKeyboardJSONString != null) {

            saveFile(defaultKeyboardJSONString, getKeyboardFileName(defaultKeyboardJSONString), context);
        }
        else{
            System.out.println("initializeKeyboards error with File: " + getDefaultKeyboardFileName());
        }
    }

    private static void saveDefaultKeyboardsFile(Context context, String fileName){

        String keyboardsJSONString = getDefaultFileString(context, fileName);
        if(keyboardsJSONString != null) {
            saveFile(keyboardsJSONString, fileName, context);
        }
        else{
            System.out.println("saveKeyboardsFile error with File: " + fileName);
        }
    }


    //endregion


    //region Private General Use


    private static void didInstallKeyboardWithId(String key) {

        Map<String, AvailableKeyboard> availableKeyboards = getAvailableKeyboardsDictionary();
        Map<String, AvailableKeyboard> downloadedKeyboards = getDownloadedKeyboardsDictionary();
        Map<String, AvailableKeyboard> installedKeyboards = getInstalledKeyboardDictionary();

        downloadedKeyboardsDictionary.put(key, availableKeyboards.get(key));

        installedKeyboardDictionary.put(key, availableKeyboards.get(key));

        KeyboardSwitcher.getInstance().makeKeyboards(true);
        UpdateFragment.getSharedInstance().endProgress(true, "Updated");
    }

    private static boolean updateKeyboards(Context context){

        String jsonAvailableString = getJSONStringFromFile(context, getAvailableKeyboardFileName());
        System.out.println("JSON Available String: " + jsonAvailableString);
        setAvailableKeyboardsDictionary(jsonAvailableString);
        Map<String, AvailableKeyboard> availableKeyboards = getAvailableKeyboardsDictionary();
        Map<String, AvailableKeyboard> downloadedKeyboards = getDownloadedKeyboardsDictionary();
        Map<String, AvailableKeyboard> installedKeyboards = getInstalledKeyboardDictionary();

        boolean isUpdated = keyboardListsAreCurrentWithEachOther(availableKeyboards, downloadedKeyboards);

        System.out.println("Will check for keyboardUpdates;");
        System.out.println("availableKeyboards count: " + availableKeyboards.size());
        System.out.println("downloadedKeyboards count: " + downloadedKeyboards.size());
        System.out.println("installedKeyboards count: " + installedKeyboards.size());


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
                        downloadedKeyboards.remove(key);
                        deleteFile(context, getKeyboardIDFileName(key));
                    }
                    if(installedKeyboards.containsKey(key)){
                        installedKeyboards.remove(key);
                    }
                }
            }

            for (String key : availableKeyboards.keySet()){


                if(downloadedKeyboards.keySet().contains(key) && ! isCurrent(downloadedKeyboards.get(key).getUpdated(), availableKeyboards.get(key).getUpdated())){
                    System.out.println("Is downloading updated keyboard with id: " + key);
                    downloadedKeyboards.put(key, availableKeyboards.get(key));

                    UpdateFragment.getSharedInstance().setProgress(40, "Downloading new keyboard for: " + availableKeyboards.get(key).getLanguageName());
                    System.out.println("Will Download/update keyboard id: " + availableKeyboards.get(key).getId());
                    KeyboardDownloader.getSharedInstance().downloadKeyboard(Long.toString(availableKeyboards.get(key).getId()));
                }

            }

            saveKeyboards(context, downloadedKeyboards, getDownloadedKeyboardFileName());
        }

        if(! keyboardListsAreCurrentWithEachOther(downloadedKeyboards, installedKeyboards)){

            System.out.println("Is updating installed keyboards");
            for (String key : installedKeyboards.keySet()){
                installedKeyboards.put(key, availableKeyboards.get(key));
            }
            saveKeyboards(context, installedKeyboards, getInstalledKeyboardFileName());
        }

        updateCurrentKeyboards();

        return isUpdated;
    }


    private static String createJSONStringForKeyboards(AvailableKeyboard[] keyboards){

        String jsonString = "{\nkeyboards:[\n";

        for(AvailableKeyboard keyboard : keyboards){

            jsonString += keyboard.getObjectAsJSONString();
        }

        jsonString = jsonString.substring(0, jsonString.length() - 1);
        jsonString += "\n]\n}";

        return jsonString;
    }


    //endregion

}
