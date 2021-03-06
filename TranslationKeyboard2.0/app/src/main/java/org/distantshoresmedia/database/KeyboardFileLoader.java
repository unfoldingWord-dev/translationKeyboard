package org.distantshoresmedia.database;

import android.content.Context;

import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.model.BaseKeyboard;
import org.distantshoresmedia.utilities.TKPreferenceManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Fechner on 12/31/14.
 */
public class KeyboardFileLoader {


    //region Initialization

    /**
     *  loads the preloaded keyboards and saves them to the private application files.
     * @param context
     */
    public static void initializeKeyboards(Context context){

        preLoadAvailableKeyboards(context);
        preLoadDownloadedKeyboards(context);
        preLoadInstalledKeyboards(context);

        String json = FileLoader.getJSONStringFromAssets(context, FileNameHelper.getAvailableKeyboardsFileName());
        AvailableKeyboard[] keyboards = AvailableKeyboard.getKeyboardsFromJsonString(json);

        savePreloadedKeyboards(context, keyboards);
    }

    public static boolean preLoadAvailableKeyboards(Context context){

        String fileName = FileNameHelper.getAvailableKeyboardsFileName();
        String json = FileLoader.getJSONStringFromAssets(context, fileName);
        FileLoader.saveFileToApplicationFiles(context, json, fileName);

        return true;
    }

    public static boolean preLoadDownloadedKeyboards(Context context){

        String fileName = FileNameHelper.getDownloadedKeyboardsFileName();
        String json = FileLoader.getJSONStringFromAssets(context, fileName);
        FileLoader.saveFileToApplicationFiles(context, json, fileName);

        return true;
    }

    public static boolean preLoadInstalledKeyboards(Context context){

        String fileName = FileNameHelper.getInstalledKeyboardsFileName();
        String json = FileLoader.getJSONStringFromAssets(context, fileName);
        FileLoader.saveFileToApplicationFiles(context, json, fileName);

        return true;
    }

    public static boolean preloadIsUpdated(Context context){

        String fileName = FileNameHelper.getAvailableKeyboardsFileName();
        String preloadedJson = FileLoader.getJSONStringFromAssets(context, fileName);

        try {
            double preloadUpdated = (new JSONObject(preloadedJson)).getDouble("updated_at");
            return (TKPreferenceManager.getLastUpdatedDate(context) < preloadUpdated);
        }
        catch (JSONException e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean reloadPreload(Context context){

        preLoadAvailableKeyboards(context);
        preLoadDownloadedKeyboards(context);

        String json = FileLoader.getJSONStringFromAssets(context, FileNameHelper.getAvailableKeyboardsFileName());
        AvailableKeyboard[] keyboards = AvailableKeyboard.getKeyboardsFromJsonString(json);

        savePreloadedKeyboards(context, keyboards);
        return true;
    }

    /**
     *
     *  saves preloaded keyboards from the passed array of keyboards
     * @param context
     * @param keyboards
     */
    private static void savePreloadedKeyboards(Context context, AvailableKeyboard[] keyboards){

        for(AvailableKeyboard keyboard : keyboards){

            String fileName = FileNameHelper.getKeyboardFileName(keyboard);
            String json = FileLoader.getJSONStringFromAssets(context, fileName);

            FileLoader.saveFileToApplicationFiles(context, json, fileName);
        }
    }

    //endregion

    //region General Use

    /**
     *
     * @param context
     * @return
     */
    protected static double getUpdatedDate(Context context){

        String fileName = FileNameHelper.getAvailableKeyboardsFileName();
        String json = FileLoader.getJSONStringFromApplicationFiles(context, fileName);

        return AvailableKeyboard.getUpdatedTimeFromJSONString(json);
    }

    protected static void saveAvailableKeyboards(Context context, String json){

        FileLoader.saveFileToApplicationFiles(context, json, FileNameHelper.getAvailableKeyboardsFileName());
    }

    protected static void saveAvailableKeyboards(Context context, AvailableKeyboard[] keyboards){

        saveKeyboards(context, keyboards, FileNameHelper.getAvailableKeyboardsFileName());
    }
    protected static void saveDownloadedKeyboards(Context context, AvailableKeyboard[] keyboards){

        saveKeyboards(context, keyboards, FileNameHelper.getDownloadedKeyboardsFileName());
    }

    protected static void saveInstalledKeyboards(Context context, AvailableKeyboard[] keyboards){

        saveKeyboards(context, keyboards, FileNameHelper.getInstalledKeyboardsFileName());
    }

    /**
     *
     * @param context
     * @param keyboards
     * @param fileName
     */
    protected static void saveKeyboards(Context context, AvailableKeyboard[] keyboards, String fileName){

        String json = createJSONStringForKeyboards(keyboards);
        FileLoader.saveFileToApplicationFiles(context, json, fileName);
    }

    /**
     * @param keyboards
     * @return
     */
    private static String createJSONStringForKeyboards(AvailableKeyboard[] keyboards){

        JSONObject allJson = new JSONObject();
        JSONArray keyboardsJson = new JSONArray();

        for(AvailableKeyboard keyboard : keyboards){
            keyboardsJson.put(keyboard.getObjectAsJSONObject());
        }
        try {
            allJson.put("keyboards", keyboardsJson);
            return allJson.toString();
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param context
     * @param json
     */
    protected static void saveKeyboardJson(Context context, String json){

        String keyboardID = Long.toString(BaseKeyboard.getKeyboardIDFromJSONString(json));
        String fileName = FileNameHelper.getKeyboardIDFileName(keyboardID);

        FileLoader.saveFileToApplicationFiles(context, json, fileName);
    }

    /**
     *
     * @param context
     * @return
     */
    protected static boolean hasSavedKeyboards(Context context){

        return (getAvailableKeyboards(context) != null);
    }
    /**
     *
     * @param context
     * @param keyboard
     * @return json string for the desired keyboard
     */
    protected static String loadKeyboardFromFiles(Context context, AvailableKeyboard keyboard){

        String id = Long.toString(keyboard.getId());
        return getKeyboardForID(context, id);
    }

    /**
     *
     * @param context
     * @param id
     * @return json string for the desired keyboard
     */
    protected static String loadKeyboardFromFiles(Context context, String id){

        return getKeyboardForID(context, id);
    }

    /**
     *
     * @param context
     * @param id
     * @return json string for the desired keyboard
     */
    private static String getKeyboardForID(Context context, String id){

        String fileName = FileNameHelper.getKeyboardIDFileName(id);
        return FileLoader.getJSONStringFromApplicationFiles(context, fileName);
    }

    /**
     *
     * @param context
     * @return The available keyboards
     */
    protected static AvailableKeyboard[] getAvailableKeyboards(Context context){

        String fileName = FileNameHelper.getAvailableKeyboardsFileName();
        String json = FileLoader.getJSONStringFromApplicationFiles(context, fileName);

        if(! jsonIsValid(json)){
            return null;
        }

        return AvailableKeyboard.getKeyboardsFromJsonString(json);
    }

    /**
     *
     * @param context
     * @return The keyboards that have been downloaded
     */
    protected static AvailableKeyboard[] getDownloadedKeyboards(Context context){

        String fileName = FileNameHelper.getDownloadedKeyboardsFileName();
        String json = FileLoader.getJSONStringFromApplicationFiles(context, fileName);

        if(! jsonIsValid(json)){
            return null;
        }

        return AvailableKeyboard.getKeyboardsFromJsonString(json);
    }

    /**
     *
     * @param context
     * @return the keyboards the user has chosen to install and are available to use
     */
    protected static AvailableKeyboard[] getInstalledKeyboards(Context context){

        String fileName = FileNameHelper.getInstalledKeyboardsFileName();
        String json = FileLoader.getJSONStringFromApplicationFiles(context, fileName);

        if(! jsonIsValid(json)){
            return null;
        }

        return AvailableKeyboard.getKeyboardsFromJsonString(json);
    }

    private static boolean jsonIsValid(String json){

        return !(json == null || json.length() < 1);
    }
    //endregion

}
