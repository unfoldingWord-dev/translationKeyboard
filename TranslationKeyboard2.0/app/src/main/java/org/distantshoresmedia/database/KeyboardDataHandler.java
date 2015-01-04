package org.distantshoresmedia.database;

import android.content.Context;

import org.distantshoresmedia.model.AvailableKeyboard;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Fechner on 1/2/15.
 */
public class KeyboardDataHandler {

    private static Map<String, AvailableKeyboard> availableKeyboardsDictionary = null;
    private static Map<String, AvailableKeyboard> downloadedKeyboardsDictionary = null;
    private static Map<String, AvailableKeyboard> installedKeyboardDictionary = null;


    //region Dictionary Methods

    protected static void invalidateLoadedKeyboardsAvailable(){
        availableKeyboardsDictionary = null;
        downloadedKeyboardsDictionary = null;
        installedKeyboardDictionary = null;
    }
    /**
     *
     * @param context
     * @return
     */
    protected static Map<String, AvailableKeyboard> getAvailableKeyboardsDictionary(Context context){

        if(availableKeyboardsDictionary == null){
            AvailableKeyboard[] keyboards = KeyboardFileLoader.getAvailableKeyboards(context);
            availableKeyboardsDictionary = makeKeyboardsDictionary(context, keyboards);
        }

        return availableKeyboardsDictionary;
    }


    /**
     *
     * @param context
     * @return
     */
    protected static Map<String, AvailableKeyboard> getDownloadedKeyboardsDictionary(Context context){

        if(downloadedKeyboardsDictionary == null){
            AvailableKeyboard[] keyboards = KeyboardFileLoader.getDownloadedKeyboards(context);
            downloadedKeyboardsDictionary = makeKeyboardsDictionary(context, keyboards);
        }

        return downloadedKeyboardsDictionary;
    }


    /**
     *
     * @param context
     * @return
     */
    protected static Map<String, AvailableKeyboard> getInstalledKeyboardDictionary(Context context){

        if(installedKeyboardDictionary == null){
            AvailableKeyboard[] keyboards = KeyboardFileLoader.getInstalledKeyboards(context);
            installedKeyboardDictionary = makeKeyboardsDictionary(context, keyboards);
        }

        return installedKeyboardDictionary;
    }
    //endregion

    protected static AvailableKeyboard[] getInstalledKeyboardsArray(Context context){

        return getKeyboardsArrayFromDictionary(context, getAvailableKeyboardsDictionary(context));
    }


    protected static void addInstalledKeyboard(Context context, AvailableKeyboard keyboard){

        String id = Long.toString(keyboard.getId());
        getDownloadedKeyboardsDictionary(context).put(id, keyboard);
        getInstalledKeyboardDictionary(context).put(id, keyboard);

        updateAvailableKeyboards(context);
    }
    /**
     *
     * @param context
     * @param keyboard
     */
    protected static void updateAvailableKeyboard(Context context, AvailableKeyboard keyboard){

        String id = Long.toString(keyboard.getId());
        getAvailableKeyboardsDictionary(context).put(id, keyboard);

        if(getDownloadedKeyboardsDictionary(context).containsKey(id)){
            downloadedKeyboardsDictionary.put(id, keyboard);
        }
        if(getInstalledKeyboardDictionary(context).containsKey(id)){
            installedKeyboardDictionary.put(id, keyboard);
        }
    }

    /**
     *
     * @param context
     * @param id
     */
    protected static void deleteKeyboardWithId(Context context, String id){

        FileLoader.deleteFile(context, FileNameHelper.getKeyboardIDFileName(id));

        if(getAvailableKeyboardsDictionary(context).containsKey(id)){
            availableKeyboardsDictionary.remove(id);
        }
        if(getDownloadedKeyboardsDictionary(context).containsKey(id)){
            downloadedKeyboardsDictionary.remove(id);
        }
        if(getInstalledKeyboardDictionary(context).containsKey(id)){
            installedKeyboardDictionary.remove(id);
        }

    }

    /**
     *
     * @param context
     */
    protected static void updateAvailableKeyboards(Context context){

        KeyboardFileLoader.saveAvailableKeyboards(context, getKeyboardsArrayFromDictionary(context, getAvailableKeyboardsDictionary(context)));
        KeyboardFileLoader.saveDownloadedKeyboards(context, getKeyboardsArrayFromDictionary(context, getDownloadedKeyboardsDictionary(context)));
        KeyboardFileLoader.saveInstalledKeyboards(context, getKeyboardsArrayFromDictionary(context, getInstalledKeyboardDictionary(context)));
        invalidateLoadedKeyboardsAvailable();
    }

    /**
     *
     * @param locale
     * @return The desired id, or null if it's not found
     */
    protected static String findKeyboardIdForLocal(Context context, Locale locale){

        for(AvailableKeyboard keyboard : getInstalledKeyboardDictionary(context).values()){

            Locale keyboardLocal = keyboard.getKeyboardAsLocale();

            String localeLanguage = locale.getLanguage();
            String keyboardCountry = keyboardLocal.getCountry();

            if(localeLanguage.compareToIgnoreCase(keyboardCountry) == 0){
                return Long.toString(keyboard.getId());
            }
            System.out.println("Desired locale: " + locale.toString() + " keyboardLocal: " + keyboardLocal.toString());
        }

        return null;
    }



    /**
     *
     * @param context
     * @return
     */
    protected static AvailableKeyboard[] getKeyboardsArrayFromDictionary(Context context,
                                                                           Map<String, AvailableKeyboard> keyboardsDictionary){

        AvailableKeyboard[] keyboards = new AvailableKeyboard[keyboardsDictionary.size()];

        int i = 0;
        for(AvailableKeyboard keyboard : keyboardsDictionary.values()){
            keyboards[i] = keyboard;
            i++;
        }

        return keyboards;
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
    protected static boolean keyboardListsAreCurrentWithEachOther(Map<String, AvailableKeyboard> updatedKeyboardMap,
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
