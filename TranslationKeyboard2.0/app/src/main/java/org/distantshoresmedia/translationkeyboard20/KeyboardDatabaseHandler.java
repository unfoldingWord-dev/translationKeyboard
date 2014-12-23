package org.distantshoresmedia.translationkeyboard20;

import android.content.Context;

import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.model.BaseKeyboard;
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
import java.util.Map;

/**
 * Created by Fechner on 12/18/14.
 */
public class KeyboardDatabaseHandler {

    private static final String kAvailableKeyboardsFileName = "available_keyboards";
    private static final String kDownloadedKeyboardsFileName = "downloaded_keyboards";
    private static final String kInstalledKeyboardsFileName = "installed_keyboards";
    private static final String kDefaultKeyboardFileName = "default_keyboard";

    private static final String kKeyboardExtensionName = ".tk";

    private static Context currentContext;

    //region File Name Helper Methods

    /**
     *
     * @param fileName
     * @return
     */
    private static String addKeyboardExtension(String fileName){
        return fileName + kKeyboardExtensionName;
    }

    private static String addKeyboardExtension(Long fileName){
        String name = fileName + kKeyboardExtensionName;
        return name;
    }

    /**
     *
     * @return File name for the available keyboards file
     */
    private static String getAvailableKeyboardFileName(){
        return addKeyboardExtension(kAvailableKeyboardsFileName);
    }
    /**
     *
     * @return File name for the downloaded keyboards file
     */
    private static String getDownloadedKeyboardFileName(){
        return addKeyboardExtension(kDownloadedKeyboardsFileName);
    }
    /**
     *
     * @return File name for the installed keyboards file
     */
    private static String getInstalledKeyboardFileName(){
        return addKeyboardExtension(kInstalledKeyboardsFileName);
    }

    /**
     *
     * @return File name for the Default keyboard file */
    private static String getDefaultKeyboardFileName(){
        return addKeyboardExtension(kDefaultKeyboardFileName);
    }

    private static String getKeyboardIDFileName(String id){
        String fileName = addKeyboardExtension(id);
        return fileName;
    }
    /**
     *
     * @param jsonString
     * @return File name from a BaseKeyboard JSON String
     */
    private static String getKeyboardFileName(String jsonString){

        String fileName = addKeyboardExtension(BaseKeyboard.getKeyboardIDFromJSONString(jsonString));
        return fileName;
    }
    /**
     *
     * @param keyboard
     * @return  File name from an AvailableKeyboard object
     */
    private static String getKeyboardFileName(AvailableKeyboard keyboard){

        String fileName = addKeyboardExtension(BaseKeyboard.getKeyboardNameFromJSONString(keyboard.getLanguageName()));
        return fileName;
    }

    //endregion


    //region Public Setup

    /**
     *
     * @param context
     */
    public static void initializeDatabase(Context context){

        currentContext = context;

        if(false){//keyboardsHaveBeenSaved(context)){
            System.out.println("KeyboardsAlreadySaved");
        }
        else{
            System.out.println("Keyboards Will Be Intialized");
            initializeKeyboards(context);
        }

    }
    //endregion

    //region Public General Use

    public static BaseKeyboard getKeyboardWithID( String id){

//        Map<String, AvailableKeyboard> installedKeyboards = makeKeyboardsDictionary(currentContext, getJSONStringFromFile(currentContext, getKeyboardIDFileName(id)));

        String jsonString = getJSONStringFromFile(currentContext, getKeyboardIDFileName(id));

        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            BaseKeyboard desiredKeyboard = BaseKeyboard.getKeyboardFromJsonObject(jsonObj);

            return desiredKeyboard;
        }
        catch (JSONException e){

            System.out.println("getKeyboardWithID JSONException: " + e.toString());
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

    public static boolean updateKeyboardsDatabaseWithJSON(Context context, String newKeyboardsjson){

        String currentKeyboards = getJSONStringForAvailableKeyboards(context);

        double currentUpdatedDate = AvailableKeyboard.getUpdatedTimeFromJSONString(currentKeyboards);
        double newUpdatedDate = AvailableKeyboard.getUpdatedTimeFromJSONString(newKeyboardsjson);

        if(Math.round(currentUpdatedDate) >= Math.round(newUpdatedDate)){
            return true;
        }
        else {
            return updateKeyboards(context, newKeyboardsjson);
        }
    }

    public static boolean updateOrSaveKeyboard(Context context, String keyboardJSON){

        String fileName = getKeyboardFileName(keyboardJSON);

        System.out.println("Attempting to save Keyboard named: " + fileName);
        saveFile(keyboardJSON, fileName, context);

        String keyboardID = Long.toString(BaseKeyboard.getKeyboardIDFromJSONString(keyboardJSON));

        didInstallKeyboardWithId(keyboardID);

        return true;
    }

    public static boolean hasDownloadedKeyboard(AvailableKeyboard keyboard){

        Map<String, AvailableKeyboard> downloadedKeyboards = makeKeyboardsDictionary(currentContext,
                getJSONStringFromFile(currentContext, getDownloadedKeyboardFileName()));

        boolean hasKeyboard = (downloadedKeyboards.keySet().contains(Long.toString(keyboard.getId())));

        return hasKeyboard;
    }

    public static boolean hasInstalledKeyboard(AvailableKeyboard keyboard){

        Map<String, AvailableKeyboard> installedKeyboards = makeKeyboardsDictionary(currentContext,
                getJSONStringFromFile(currentContext, getInstalledKeyboardFileName()));

        boolean hasKeyboard = (installedKeyboards.keySet().contains(Long.toString(keyboard.getId())));

        return hasKeyboard;
    }



    public static AvailableKeyboard[] getAvailableKeyboards(){

        String keyboardsJson = getJSONStringForAvailableKeyboards(currentContext);
        try{
            AvailableKeyboard[] keyboards = AvailableKeyboard.getKeyboardsFromJsonObject(new JSONObject(keyboardsJson));
            return keyboards;
        }
        catch (JSONException e){
            System.out.println("getAvailableKeyboards JSONExcseption: " + e.toString());
            return null;
        }
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

    /**
     *
     * @param context
     * @param fileName
     * @return
     */
    private static String getDefaultFileString(Context context, String fileName){

        String resultString = "";

        try{

            InputStream fileStream = context.getAssets().open(fileName);

            int size = fileStream.available();

            byte[] buffer = new byte[size];

            fileStream.read(buffer);

            fileStream.close();

            resultString = new String(buffer, "UTF-8");

            System.out.println("initializeKeyboards SavedString = " + resultString.substring(0, 10));
        }
        catch (IOException e){
            System.out.println("initializeKeyboards IOException: " + e.toString());
            return null;
        }

        return resultString;
    }

    //endregion

    //region Private General Use

    private static boolean updateKeyboards(Context context, String jsonString) {

        saveFile(jsonString, getAvailableKeyboardFileName(), context);
        return updateKeyboards(context);
    }

    private static void didInstallKeyboardWithId(String key) {

        Map<String, AvailableKeyboard> availableKeyboards = makeKeyboardsDictionary(currentContext,
                getJSONStringFromFile(currentContext, getAvailableKeyboardFileName()));
        Map<String, AvailableKeyboard> downloadedKeyboards = makeKeyboardsDictionary(currentContext,
                getJSONStringFromFile(currentContext, getDownloadedKeyboardFileName()));
        Map<String, AvailableKeyboard> installedKeyboards = makeKeyboardsDictionary(currentContext,
                getJSONStringFromFile(currentContext, getInstalledKeyboardFileName()));

        downloadedKeyboards.put(key, availableKeyboards.get(key));
        installedKeyboards.put(key, availableKeyboards.get(key));

        saveKeyboards(currentContext, downloadedKeyboards, kDownloadedKeyboardsFileName);
        saveKeyboards(currentContext, installedKeyboards, kInstalledKeyboardsFileName);
    }

    private static boolean updateKeyboards(Context context){

        String jsonAvailableString = getJSONStringFromFile(context, getAvailableKeyboardFileName());
        System.out.println("JSON Available String: " + jsonAvailableString);
        Map<String, AvailableKeyboard> availableKeyboards = makeKeyboardsDictionary(context,jsonAvailableString);
        Map<String, AvailableKeyboard> downloadedKeyboards = makeKeyboardsDictionary(context,
                getJSONStringFromFile(context, getDownloadedKeyboardFileName()));
        Map<String, AvailableKeyboard> installedKeyboards = makeKeyboardsDictionary(context,
                getJSONStringFromFile(context, getInstalledKeyboardFileName()));

        boolean isUpdated = keyboardListsAreCurrentWithEachOther(availableKeyboards, downloadedKeyboards);

        System.out.println("Will check for keyboardUpdates;");
        System.out.println("availableKeyboards count: " + availableKeyboards.size());
        System.out.println("downloadedKeyboards count: " + downloadedKeyboards.size());
        System.out.println("installedKeyboards count: " + installedKeyboards.size());

        if(! isUpdated) {

            for (String key : availableKeyboards.keySet()){


                if(downloadedKeyboards.keySet().contains(key) && ! isCurrent(downloadedKeyboards.get(key).getUpdated(), availableKeyboards.get(key).getUpdated())){
                    downloadedKeyboards.put(key, availableKeyboards.get(key));

                    System.out.println("Will Download/update keyboard id: " + availableKeyboards.get(key).getId());
                    KeyboardDownloader.getSharedInstance().downloadKeyboard(Long.toString(availableKeyboards.get(key).getId()));
                }

            }

            saveKeyboards(context, downloadedKeyboards, kDownloadedKeyboardsFileName);
        }

        if(! keyboardListsAreCurrentWithEachOther(downloadedKeyboards, installedKeyboards)){

            for (String key : installedKeyboards.keySet()){
                installedKeyboards.put(key, availableKeyboards.get(key));
            }
            saveKeyboards(context, installedKeyboards, kInstalledKeyboardsFileName);
        }

        return isUpdated;
    }

    private static boolean keyboardListsAreCurrentWithEachOther(Map<String, AvailableKeyboard> updatedKeyboardMap,
                                                                Map<String, AvailableKeyboard> subjectKeyboardMap){

        for (Map.Entry<String, AvailableKeyboard> keyboard : subjectKeyboardMap.entrySet())
        {
            AvailableKeyboard subjectKeyboard = keyboard.getValue();

            AvailableKeyboard newKeyboard = updatedKeyboardMap.get(keyboard.getKey());

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
            System.out.print("keyboardsHaveBeenSaved() IOException: " + e.toString());
        }

        return false;
    }

    /**
     *
     * @param context
     * @param fileName
     * @return
     */
    private static String getJSONStringFromFile(Context context, String fileName){

        if(!keyboardsHaveBeenSaved(context)){
            initializeKeyboards(context);
        }

        String resultString = "";

        try{
            InputStream fileStream = context.openFileInput(fileName);

            int size = fileStream.available();

            byte[] buffer = new byte[size];

            fileStream.read(buffer);

            fileStream.close();

            resultString = new String(buffer, "UTF-8");

        }
        catch (FileNotFoundException e){
            System.out.println("getKeyboardsJSONString FileNotFoundException: " + e.toString());
            return null;
        }
        catch (IOException e){
            System.out.println("getKeyboardsJSONString IOException: " + e.toString());
            return null;
        }

        return resultString;
    }

    private static void saveKeyboards(Context context,  Map<String, AvailableKeyboard> keyboards, String fileName){

        AvailableKeyboard[] keyboardsArray = new AvailableKeyboard[keyboards.size()];

        int i = 0;
        for(Map.Entry<String, AvailableKeyboard> keyboard : keyboards.entrySet()){

            keyboardsArray[i] = keyboard.getValue();
        }


        saveFile(createJSONStringForKeyboards(keyboardsArray), fileName, context);
    }

    /**
     *
     * @param fileString
     * @param fileName
     * @param context
     */
    private static void saveFile(String fileString, String fileName, Context context){

        System.out.println("Attempting to save file named:" + fileName);

        try {
            File file = new File(context.getFilesDir(), fileName);

            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(fileString.getBytes());
            fos.close();
        }
        catch (FileNotFoundException e){
            System.out.println("saveFile FileNotFoundException: " + e.toString());
        }
        catch (IOException e){
            System.out.println("saveFile IOException: " + e.toString());
        }
        System.out.println("File saving was successful.");
    }

    //endregion


    //region Helper Methods

    private static boolean isCurrent(Date oldTime, Date newTime){

        if(oldTime.before(newTime)){
            return false;
        }
        else{
            return true;
        }
    }
    //endregion
}
