package org.distantshoresmedia.org.distantshoresmedia.database;

import android.content.Context;

import org.distantshoresmedia.model.AvailableKeyboard;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fechner on 1/2/15.
 */
public class KeyboardDataMaker {

    private static Map<String, AvailableKeyboard> availableKeyboardsDictionary = null;
    /**
     *
     * @param context
     * @return
     */
    public static Map<String, AvailableKeyboard> getAvailableKeyboardsDictionary(Context context){

        if(availableKeyboardsDictionary == null){
            AvailableKeyboard[] keyboards = KeyboardFileLoader.getAvailableKeyboards(context);
            availableKeyboardsDictionary = makeKeyboardsDictionary(context, keyboards);
        }

        return availableKeyboardsDictionary;
    }

    private static Map<String, AvailableKeyboard> downloadedKeyboardsDictionary = null;
    /**
     *
     * @param context
     * @return
     */
    public static Map<String, AvailableKeyboard> getDownloadedKeyboardsDictionary(Context context){

        if(downloadedKeyboardsDictionary == null){
            AvailableKeyboard[] keyboards = KeyboardFileLoader.getDownloadedKeyboards(context);
            downloadedKeyboardsDictionary = makeKeyboardsDictionary(context, keyboards);
        }

        return downloadedKeyboardsDictionary;
    }

    private static Map<String, AvailableKeyboard> installedKeyboardDictionary = null;
    /**
     *
     * @param context
     * @return
     */
    public static Map<String, AvailableKeyboard> getInstalledKeyboardDictionary(Context context){

        if(installedKeyboardDictionary == null){
            AvailableKeyboard[] keyboards = KeyboardFileLoader.getInstalledKeyboards(context);
            installedKeyboardDictionary = makeKeyboardsDictionary(context, keyboards);
        }

        return installedKeyboardDictionary;
    }


    /**
     *
     * @param context
     * @param jsonString
     * @return
     */
    private static Map<String, AvailableKeyboard> makeKeyboardsDictionary(Context context, String jsonString){

        AvailableKeyboard[] keyboards = AvailableKeyboard.getKeyboardsFromJsonString(jsonString);

        Map<String, AvailableKeyboard> keyboardsDictionary = new HashMap<String, AvailableKeyboard>();

        for(AvailableKeyboard keyboard : keyboards){
            keyboardsDictionary.put(Integer.toString((int) keyboard.getId()), keyboard);
        }
        return keyboardsDictionary;
    }

    /**
     *
     * @param context
     * @param keyboards
     * @return
     */
    private static Map<String, AvailableKeyboard> makeKeyboardsDictionary(Context context, AvailableKeyboard[] keyboards){

        Map<String, AvailableKeyboard> keyboardsDictionary = new HashMap<String, AvailableKeyboard>();

        for(AvailableKeyboard keyboard : keyboards){
            keyboardsDictionary.put(Integer.toString((int) keyboard.getId()), keyboard);
        }
        return keyboardsDictionary;
    }

    /**
     *
     * @param updatedKeyboardMap
     * @param subjectKeyboardMap
     * @return
     */
    private static boolean keyboardListsAreCurrentWithEachOther(Map<String, AvailableKeyboard> updatedKeyboardMap,
                                                                Map<String, AvailableKeyboard> subjectKeyboardMap){

        for (Map.Entry<String, AvailableKeyboard> keyboard : subjectKeyboardMap.entrySet())
        {
            AvailableKeyboard subjectKeyboard = keyboard.getValue();

            AvailableKeyboard newKeyboard = updatedKeyboardMap.get(keyboard.getKey());

            if(newKeyboard == null || subjectKeyboard == null){
                return false;
            }

            if(! TimeHelper.isCurrent(subjectKeyboard.getUpdated(), newKeyboard.getUpdated())){
                return false;
            }
        }

        return true;
    }
}
