package org.distantshoresmedia.database;

import android.content.Context;
import android.util.Log;

import org.distantshoresmedia.model.AvailableKeyboard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Fechner on 1/2/15.
 */
public class KeyboardDataHandler {

    private static final String TAG = "KeyboardDataHandler";

    private static Map<String, AvailableKeyboard> availableKeyboardsDictionary;
    private static Map<String, AvailableKeyboard> downloadedKeyboardsDictionary;
    private static Map<String, AvailableKeyboard> installedKeyboardDictionary;


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

    protected static void setKeyboardAvailabilityState(Context context, AvailableKeyboard keyboard, boolean installed){

        String id = Long.toString(keyboard.getId());
        boolean hasKey = getInstalledKeyboardDictionary(context).containsKey(id);
        if( hasKey && !installed){

            installedKeyboardDictionary.remove(id);
            updateKeyboardAvailability(context);
        }
        else if(!hasKey && installed){

            installedKeyboardDictionary.put(id, keyboard);
            updateKeyboardAvailability(context);
        }
    }

    public static void sideLoadKeyboards(Context context, String keyboardsJson){

        AvailableKeyboard[] keyboards = AvailableKeyboard.getKeyboardsFromJsonString(keyboardsJson);
        if(keyboards == null){
            return;
        }
        getAvailableKeyboardsDictionary(context);
        getDownloadedKeyboardsDictionary(context);

        for(AvailableKeyboard keyboard : keyboards){
            availableKeyboardsDictionary.put(Long.toString(keyboard.getId()), keyboard);
            downloadedKeyboardsDictionary.put(Long.toString(keyboard.getId()), keyboard);
        }

        KeyboardFileLoader.saveAvailableKeyboards(context, getKeyboardsArrayFromDictionary(context, availableKeyboardsDictionary));
        KeyboardFileLoader.saveDownloadedKeyboards(context, getKeyboardsArrayFromDictionary(context, downloadedKeyboardsDictionary));
        createSideLoadedKeyboards(context, keyboardsJson);
    }

    private static void createSideLoadedKeyboards(Context context, String json){

        getAvailableKeyboardsDictionary(context);
        getDownloadedKeyboardsDictionary(context);

        try{
            JSONArray jsonArray = new JSONObject(json).getJSONArray("keyboards");
            for(int i = 0; i < jsonArray.length(); i++){

                JSONObject baseKeyboard = jsonArray.getJSONObject(i).getJSONObject("keyboards");
                KeyboardFileLoader.saveKeyboardJson(context, baseKeyboard.toString());
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    protected static AvailableKeyboard[] getInstalledKeyboardsArray(Context context){

        return getKeyboardsArrayFromDictionary(context, getAvailableKeyboardsDictionary(context));
    }


    protected static void addInstalledKeyboard(Context context, AvailableKeyboard keyboard){

        String id = Long.toString(keyboard.getId());
        getDownloadedKeyboardsDictionary(context).put(id, keyboard);
        getInstalledKeyboardDictionary(context).put(id, keyboard);

        updateKeyboardAvailability(context);
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

        invalidateLoadedKeyboardsAvailable();

    }

    /**
     *
     * @param context
     */
    protected static void updateKeyboardAvailability(Context context){

//        KeyboardFileLoader.saveAvailableKeyboards(context, getKeyboardsArrayFromDictionary(context, getAvailableKeyboardsDictionary(context)));

        Map<String, AvailableKeyboard> availKeys = getAvailableKeyboardsDictionary(context);
        Map<String, AvailableKeyboard> downKeys = getDownloadedKeyboardsDictionary(context);
        Map<String, AvailableKeyboard> instKeys = getInstalledKeyboardDictionary(context);

        ArrayList<String> keysList = new ArrayList<String>();
        for(String id : downKeys.keySet()){
            if(! availKeys.containsKey(id)) {
                keysList.add(id);
            }
        }
        if(keysList.size() > 0){
            for(String key : keysList){
                downKeys.remove(key);
            }
        }

        KeyboardFileLoader.saveDownloadedKeyboards(context, getKeyboardsArrayFromDictionary(context, downKeys));

        keysList = new ArrayList<String>();
        for(String id : instKeys.keySet()){
            if(! availKeys.containsKey(id)) {
                keysList.add(id);
            }
        }
        if(keysList.size() > 0){
            for(String key : keysList){
                instKeys.remove(key);
            }
        }
        KeyboardFileLoader.saveInstalledKeyboards(context, getKeyboardsArrayFromDictionary(context, instKeys));

        invalidateLoadedKeyboardsAvailable();
    }

    protected static void updateAvailableKeyboards(Context context, String jsonString){

        KeyboardFileLoader.saveAvailableKeyboards(context, jsonString);
        invalidateLoadedKeyboardsAvailable();
    }

    /**
     *
     * @param locale
     * @return The desired id, or null if it's not found
     */
    protected static String findKeyboardIdForLocal(Context context, Locale locale){

        if(context == null || locale == null){
            Log.i(TAG, "Context or Locale was null");
            return null;
        }

        for(AvailableKeyboard keyboard : getInstalledKeyboardDictionary(context).values()){

            Locale keyboardLocal = keyboard.getKeyboardAsLocale();

            String localLanguage = locale.getLanguage();
            String localeCountry = locale.getCountry();
            String keyboardLanguage = keyboardLocal.getLanguage();
            String keyboardCountry = keyboardLocal.getCountry();

            boolean isCorrectKeyboard = (localeCountry.equalsIgnoreCase(keyboardCountry) && localLanguage.equalsIgnoreCase(keyboardLanguage));

            if(isCorrectKeyboard){
                return Long.toString(keyboard.getId());
            }
//            Log.i(TAG, "Desired locale: " + locale.toString() + " keyboardLocal: " + keyboardLocal.toString());
        }

        return Long.toString(getInstalledKeyboardsArray(context)[0].getId());
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

        Arrays.sort(keyboards);

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

        if(keyboards == null){
            return null;
        }

        Map<String, AvailableKeyboard> keyboardsDictionary = new HashMap<String, AvailableKeyboard>();

        for(AvailableKeyboard keyboard : keyboards){
            keyboardsDictionary.put(Long.toString(keyboard.getId()), keyboard);
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

        boolean isCurrent = true;
        for(String id : updatedKeyboardMap.keySet()){
            if(! subjectKeyboardMap.containsKey(id)){
                isCurrent = false;
                break;
            }
            if(!isCurrent){
                break;
            }
        }

        return isCurrent;
    }
}
