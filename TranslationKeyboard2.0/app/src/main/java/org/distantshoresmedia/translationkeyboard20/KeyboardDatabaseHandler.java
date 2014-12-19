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

    //region File Name Helper Methods

    /**
     *
     * @param fileName
     * @return
     */
    private static String addKeyboardExtension(String fileName){
        return fileName + kKeyboardExtensionName;
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

    /**
     *
     * @param jsonString
     * @return File name from a BaseKeyboard JSON String
     */
    private static String getKeyboardFileName(String jsonString){

        String fileName = addKeyboardExtension(BaseKeyboard.getKeyboardNameFromJSONString(jsonString));
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

    public static boolean updateKeyboardsDatabaseWithJSON(Context context, String newKeyboards){

        String currentKeyboards = getJSONStringForAvailableKeyboards(context);

        double currentUpdatedDate = AvailableKeyboard.getUpdatedTimeFromJSONString(currentKeyboards);
        double newUpdatedDate = AvailableKeyboard.getUpdatedTimeFromJSONString(newKeyboards);

        if(Math.round(currentUpdatedDate) >= Math.round(newUpdatedDate)){
            return true;
        }

        return false;
    }

    //endregion

    //region Private Setup

    /**
     *
     * @param context
     */
    private static void initializeKeyboards(Context context){

        saveKeyboardsFile(context, getAvailableKeyboardFileName());
        saveKeyboardsFile(context, getDownloadedKeyboardFileName());
        saveKeyboardsFile(context, getInstalledKeyboardFileName());

        String defaultKeyboardJSONString = getDefaultFileString(context, getDefaultKeyboardFileName());
        if(defaultKeyboardJSONString != null) {

            saveFile(defaultKeyboardJSONString, BaseKeyboard.getKeyboardNameFromJSONString(defaultKeyboardJSONString), context);
        }
        else{
            System.out.println("initializeKeyboards error with File: " + getDefaultKeyboardFileName());
        }
    }

    private static void saveKeyboardsFile(Context context, String fileName){

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

            System.out.println("initializeKeyboards SavedString = " + resultString);
        }
        catch (IOException e){
            System.out.println("initializeKeyboards IOException: " + e.toString());
            return null;
        }

        return resultString;
    }

    //endregion

    //region Private General Use

    private static boolean updateKeyboards(Context context){

        Map<String, AvailableKeyboard> currentKeyboards = makeKeyboardsDictionary(context, getAvailableKeyboardFileName());
        Map<String, AvailableKeyboard> downloadedKeyboards = makeKeyboardsDictionary(context, getDownloadedKeyboardFileName());
        Map<String, AvailableKeyboard> installedKeyboards = makeKeyboardsDictionary(context, getInstalledKeyboardFileName());

        boolean needsUpdate;

        for (Map.Entry<String, AvailableKeyboard> keyboard : downloadedKeyboards.entrySet())
        {
            AvailableKeyboard downloadedKeyboard = keyboard.getValue();

            AvailableKeyboard newKeyboard = currentKeyboards.get(keyboard.getKey());

            if(isCurrent(downloadedKeyboard.getUpdated(), newKeyboard.getUpdated()));
        }

        return false;
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
            System.out.println("getKeyboardNameFromJSONString JSONException: " + e.toString());
        }

        return null;
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

    /**
     *
     * @param fileString
     * @param fileName
     * @param context
     */
    private static void saveFile(String fileString, String fileName, Context context){

        System.out.println("Attempting to save file named:" + fileName + " file: " + fileString );

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


    private static boolean isCurrent(Date oldTime, Date newTime){

        if(oldTime.before(newTime)){
            return false;
        }
        else{
            return true;
        }
    }
}
