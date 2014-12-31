package org.distantshoresmedia.org.distantshoresmedia.database;

import android.content.Context;

import org.distantshoresmedia.model.AvailableKeyboard;

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

        AvailableKeyboard[] keyboards = getPreloadedAvailableKeyboards(context);

        savePreloadedKeyboards(context, keyboards);
    }

    /**
     *
     * @param context
     * @return returns the preloaded available keyboards
     */
    private static AvailableKeyboard[] getPreloadedAvailableKeyboards(Context context){

        String fileName = FileNameHelper.getAvailableKeyboardsFileName();
        String json = FileLoader.getJSONStringFromAssets(context, fileName);
        FileLoader.saveFileToApplicationFiles(context, json, fileName);

        AvailableKeyboard[] keyboards = AvailableKeyboard.getKeyboardsFromJsonString(json);

        return keyboards;
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
     * @return The available keyboards
     */
    private static AvailableKeyboard[] getAvailableKeyboards(Context context){

        String fileName = FileNameHelper.getAvailableKeyboardsFileName();
        String json = FileLoader.getJSONStringFromApplicationFiles(context, fileName);

        AvailableKeyboard[] keyboards = AvailableKeyboard.getKeyboardsFromJsonString(json);

        return keyboards;
    }

    /**
     *
     * @param context
     * @return The keyboards that have been downloaded
     */
    private static AvailableKeyboard[] getDownloadedKeyboards(Context context){

        String fileName = FileNameHelper.getDownloadedKeyboardsFileName();
        String json = FileLoader.getJSONStringFromApplicationFiles(context, fileName);

        AvailableKeyboard[] keyboards = AvailableKeyboard.getKeyboardsFromJsonString(json);

        return keyboards;
    }

    /**
     *
     * @param context
     * @return the keyboards the user has chosen to install and are available to use
     */
    private static AvailableKeyboard[] getInstalledKeyboards(Context context){

        String fileName = FileNameHelper.getInstalledKeyboardsFileName();
        String json = FileLoader.getJSONStringFromApplicationFiles(context, fileName);

        AvailableKeyboard[] keyboards = AvailableKeyboard.getKeyboardsFromJsonString(json);

        return keyboards;
    }

    //endregion

}
