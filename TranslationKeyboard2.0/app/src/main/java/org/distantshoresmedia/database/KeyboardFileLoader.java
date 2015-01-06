package org.distantshoresmedia.database;

import android.content.Context;

import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.model.BaseKeyboard;

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

        double date = AvailableKeyboard.getUpdatedTimeFromJSONString(json);

        return date;
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
     *
     * @param keyboards
     * @return
     */
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

        boolean hasLoadedKeyboards =  (getAvailableKeyboards(context) == null)? false : true;
        return hasLoadedKeyboards;
    }
    /**
     *
     * @param context
     * @param keyboard
     * @return json string for the desired keyboard
     */
    protected static String loadKeyboardFromFiles(Context context, AvailableKeyboard keyboard){

        String id = Long.toString(keyboard.getId());
        String json = getKeyboardForID(context, id);

        return json;
    }

    /**
     *
     * @param context
     * @param id
     * @return json string for the desired keyboard
     */
    protected static String loadKeyboardFromFiles(Context context, String id){

        String json = getKeyboardForID(context, id);
        return json;
    }

    /**
     *
     * @param context
     * @param id
     * @return json string for the desired keyboard
     */
    private static String getKeyboardForID(Context context, String id){

        String fileName = FileNameHelper.getKeyboardIDFileName(id);
        String json = FileLoader.getJSONStringFromApplicationFiles(context, fileName);

        return json;
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

        AvailableKeyboard[] keyboards = AvailableKeyboard.getKeyboardsFromJsonString(json);

        return keyboards;
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

        AvailableKeyboard[] keyboards = AvailableKeyboard.getKeyboardsFromJsonString(json);

        return keyboards;
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

        AvailableKeyboard[] keyboards = AvailableKeyboard.getKeyboardsFromJsonString(json);

        return keyboards;
    }

    private static boolean jsonIsValid(String json){

        if(json == null || json.length() < 1){
            return false;
        }
        else {
            return true;
        }
    }
    //endregion

}
