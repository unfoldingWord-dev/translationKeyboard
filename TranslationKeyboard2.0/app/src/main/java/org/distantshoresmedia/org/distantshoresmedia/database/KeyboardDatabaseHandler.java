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

    private static AvailableKeyboard[] currentKeyboards = null;

    private static int currentKeyboardIndex = 0;

    //region keyboards dictionaries
    private static Map<String, AvailableKeyboard> availableKeyboardsDictionary = null;
    private static Map<String, AvailableKeyboard> getAvailableKeyboardsDictionary(){

        if(availableKeyboardsDictionary == null){
            availableKeyboardsDictionary = makeKeyboardsDictionary(currentContext,
                getJSONStringFromFile(currentContext, getAvailableKeyboardFileName()));
    }

        return availableKeyboardsDictionary;
    }
    private static void setAvailableKeyboardsDictionary(String jsonString){

        availableKeyboardsDictionary =makeKeyboardsDictionary(currentContext,jsonString);
    }

    private static Map<String, AvailableKeyboard> downloadedKeyboardsDictionary = null;
    private static Map<String, AvailableKeyboard> getDownloadedKeyboardsDictionary(){

        if(downloadedKeyboardsDictionary == null){
            downloadedKeyboardsDictionary = makeKeyboardsDictionary(currentContext,
                    getJSONStringFromFile(currentContext, getDownloadedKeyboardFileName()));
        }

        return downloadedKeyboardsDictionary;
    }
    private static void setDownloadedKeyboardsDictionary(String jsonString){

        downloadedKeyboardsDictionary =makeKeyboardsDictionary(currentContext,jsonString);
    }

    private static Map<String, AvailableKeyboard> installedKeyboardDictionary = null;
    private static Map<String, AvailableKeyboard> getInstalledKeyboardDictionary(){

        if(installedKeyboardDictionary == null){
            installedKeyboardDictionary = makeKeyboardsDictionary(currentContext,
                    getJSONStringFromFile(currentContext, getInstalledKeyboardFileName()));
        }

        return installedKeyboardDictionary;
    }
    private static void setInstalledKeyboardDictionary(String jsonString){

        installedKeyboardDictionary =makeKeyboardsDictionary(currentContext,jsonString);
    }
    //endregion


    //region Public Setup

    /**
     *
     * @param context
     * @return boolean of whether or not it initialized
     */
    public static boolean initializeDatabaseIfNecessary(Context context){

        currentContext = context;

        boolean hasBeenSaved = keyboardsHaveBeenSaved(context);
        if(keyboardsHaveBeenSaved(context)){
            System.out.println("KeyboardsAlreadySaved");
        }
        else{
            System.out.println("Keyboards Will Be Intialized");
            initializeKeyboards(context);
        }

        Map<String, AvailableKeyboard> availableKeyboards = getAvailableKeyboardsDictionary();
        Map<String, AvailableKeyboard> downloadedKeyboards = getDownloadedKeyboardsDictionary();
        Map<String, AvailableKeyboard> installedKeyboards = getInstalledKeyboardDictionary();

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

    public static void calculateCurrentKeyboardIndex(boolean reset, boolean next) {

        Log.i(TAG, "Changed from index: " + currentKeyboardIndex);
        if(reset){
            currentKeyboardIndex = 0;
            return;
        }
        if(! next){
                currentKeyboardIndex = (currentKeyboardIndex == 0)?
                        currentKeyboards.length - 1 : --currentKeyboardIndex;
        }
        else{
            currentKeyboardIndex = (currentKeyboardIndex + 1) % currentKeyboards.length;
        }

        Log.i(TAG, " To index: " + currentKeyboardIndex);
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

    public static BaseKeyboard getKeyboardWithID( String id){

        Log.i(TAG, "Requesting keyboard with ID: " + id);
//        Map<String, AvailableKeyboard> installedKeyboards = makeKeyboardsDictionary(currentContext, getJSONStringFromFile(currentContext, getKeyboardIDFileName(id)));

        String jsonString = getJSONStringFromFile(currentContext, getKeyboardIDFileName(id));

        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            BaseKeyboard desiredKeyboard = BaseKeyboard.getKeyboardFromJsonObject(jsonObj);

            return desiredKeyboard;
        }
        catch (JSONException e){

            Log.i(TAG, "getKeyboardWithID JSONException: " + e.toString());
            return null;
        }
    }

    /**
     *
     * @param context
     * @param keyboard
     * @return
     */
    public static String getJSONStringForKeyboard(Context context, AvailableKeyboard keyboard) {

        return getJSONStringFromFile(context, getKeyboardFileName(keyboard));
    }

    /**
     *
     * @param context
     * @return
     */
    public static String getJSONStringForAvailableKeyboards(Context context) {

        return getJSONStringFromFile(context, getAvailableKeyboardFileName());
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

    public static boolean hasInstalledKeyboard(AvailableKeyboard keyboard){

        Map<String, AvailableKeyboard> installedKeyboards = getInstalledKeyboardDictionary();

        boolean hasKeyboard = (installedKeyboards.keySet().contains(Long.toString(keyboard.getId())));

        return hasKeyboard;
    }

    public static boolean hasDownloadedKeyboard(AvailableKeyboard keyboard){

        Map<String, AvailableKeyboard> downloadedKeyboards =getDownloadedKeyboardsDictionary();

        boolean hasKeyboard = (downloadedKeyboards.keySet().contains(Long.toString(keyboard.getId())));

        return hasKeyboard;
    }

    public static void installedKeyboardHasState(AvailableKeyboard keyboard, boolean active){

        Map<String, AvailableKeyboard> installedKeyboards = getInstalledKeyboardDictionary();

        boolean hasKeyboard = (installedKeyboards.keySet().contains(Long.toString(keyboard.getId())));

        // we're good
        if(hasKeyboard == active){
            return;
        }
        // needs to download the keyboard
        else if(active && !hasKeyboard){
            KeyboardDownloader.getSharedInstance().downloadKeyboard(Long.toString(keyboard.getId()));
        }
        // needs to delete the keyboard from the installed keyboards
        else{
            installedKeyboards.remove(Long.toString(keyboard.getId()));
            installedKeyboardDictionary = installedKeyboards;
            saveKeyboardAvailability();
        }

    }

    public static AvailableKeyboard[] getAvailableKeyboards(){

        Map<String, AvailableKeyboard> availableKeyboards = getAvailableKeyboardsDictionary();

        AvailableKeyboard[] keyboards = new AvailableKeyboard[availableKeyboards.size()];

        int i = 0;
        for(AvailableKeyboard keyboard : availableKeyboards.values()){

            keyboards[i] = keyboard;
            i++;
        }

        return keyboards;
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

    private static void saveKeyboardAvailability(){

        Map<String, AvailableKeyboard> availableKeyboards = getAvailableKeyboardsDictionary();
        Map<String, AvailableKeyboard> downloadedKeyboards = getDownloadedKeyboardsDictionary();
        Map<String, AvailableKeyboard> installedKeyboards = getInstalledKeyboardDictionary();


        saveKeyboards(currentContext, availableKeyboards, getAvailableKeyboardFileName());
        saveKeyboards(currentContext, downloadedKeyboards, getDownloadedKeyboardFileName());
        saveKeyboards(currentContext, installedKeyboards, getInstalledKeyboardFileName());
        updateCurrentKeyboards();
    }

    private static boolean updateKeyboards(Context context, String jsonString) {

        saveFile(jsonString, getAvailableKeyboardFileName(), context);
        return updateKeyboards(context);
    }

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

    private static boolean keyboardListsAreCurrentWithEachOther(Map<String, AvailableKeyboard> updatedKeyboardMap,
                                                                Map<String, AvailableKeyboard> subjectKeyboardMap){

        for (Map.Entry<String, AvailableKeyboard> keyboard : subjectKeyboardMap.entrySet())
        {
            AvailableKeyboard subjectKeyboard = keyboard.getValue();

            AvailableKeyboard newKeyboard = updatedKeyboardMap.get(keyboard.getKey());

            if(newKeyboard == null || subjectKeyboard == null){
                return false;
            }

            if(! isCurrent(subjectKeyboard.getUpdated(), newKeyboard.getUpdated())){
                return false;
            }
        }

        return true;
    }

    private static Map<String, AvailableKeyboard> makeKeyboardsDictionary(Context context, String jsonString){


        try {
            AvailableKeyboard[] keyboards = AvailableKeyboard.getKeyboardsFromJsonObject(new JSONObject(jsonString));

            Map<String, AvailableKeyboard> keyboardsDictionary = new HashMap<String, AvailableKeyboard>();

            for(AvailableKeyboard keyboard : keyboards){
                keyboardsDictionary.put(Integer.toString((int) keyboard.getId()), keyboard);
            }
            return keyboardsDictionary;

        }
        catch (JSONException e){
            System.out.println("makeKeyboardsDictionary JSONException: " + e.toString());
        }

        return null;
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
    /**
     * *
     * @param context
     * @return true if the keyboards file exists.
     */
    private static boolean keyboardsHaveBeenSaved(Context context){

        try{
            FileInputStream file = context.openFileInput(getAvailableKeyboardFileName());
            file.close();
            return true;
        }
        catch (FileNotFoundException e ){
            System.out.println("Keyboard File Not Yet Saved");
        }
        catch (IOException e){
            Log.i(TAG, "keyboardsHaveBeenSaved() IOException: " + e.toString());
        }

        return false;
    }



    private static void deleteFile(Context context, String fileName){

        context.deleteFile(fileName);
    }

    private static void saveKeyboards(Context context,  Map<String, AvailableKeyboard> keyboards, String fileName){

        AvailableKeyboard[] keyboardsArray = new AvailableKeyboard[keyboards.size()];

        int i = 0;
        for(Map.Entry<String, AvailableKeyboard> keyboard : keyboards.entrySet()){

            keyboardsArray[i] = keyboard.getValue();
            i++;
        }


        saveFile(createJSONStringForKeyboards(keyboardsArray), fileName, context);
    }



    //endregion

}
