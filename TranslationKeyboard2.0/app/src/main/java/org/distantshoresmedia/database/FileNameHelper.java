package org.distantshoresmedia.database;

import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.model.BaseKeyboard;

/**
 * Created by Fechner on 12/31/14.
 */
public class FileNameHelper {

    private static final String kKeyboardExtensionName = ".tk";

    private static final String kAvailableKeyboardsFileName = "available_keyboards";
    private static final String kDownloadedKeyboardsFileName = "downloaded_keyboards";
    private static final String kInstalledKeyboardsFileName = "installed_keyboards";
    private static final String kDefaultKeyboardFileName = "default_keyboard";

    //region File Name Helper Methods

    /**
     *
     * @param fileName
     * @return filename with keyboard extension
     */
    private static String addKeyboardExtension(String fileName){
        return fileName + kKeyboardExtensionName;
    }

    /**
     *
     * @param fileId
     * @return file id with keyboard extension
     */
    private static String addKeyboardExtension(Long fileId){
        String name = fileId + kKeyboardExtensionName;
        return name;
    }

    /**
     *
     * @return File name for the available keyboards file
     */
    public static String getAvailableKeyboardsFileName(){
        return addKeyboardExtension(kAvailableKeyboardsFileName);
    }

    /**
     *
     * @return File name for the downloaded keyboards file
     */
    public static String getDownloadedKeyboardsFileName(){
        return addKeyboardExtension(kDownloadedKeyboardsFileName);
    }

    /**
     *
     * @return File name for the installed keyboards file
     */
    public static String getInstalledKeyboardsFileName(){
        return addKeyboardExtension(kInstalledKeyboardsFileName);
    }

    /**
     *
     * @param id
     * @return
     */
    public static String getKeyboardIDFileName(String id){
        String fileName = addKeyboardExtension(id);
        return fileName;
    }
    /**
     *
     * @param jsonString
     * @return File name from a BaseKeyboard JSON String
     */
    public static String getKeyboardFileName(String jsonString){

        String fileName = addKeyboardExtension(BaseKeyboard.getKeyboardIDFromJSONString(jsonString));
        return fileName;
    }
    /**
     *
     * @param keyboard
     * @return  File name from an AvailableKeyboard object
     */
    public static String getKeyboardFileName(AvailableKeyboard keyboard){

        String fileName = addKeyboardExtension(BaseKeyboard.getKeyboardNameFromJSONString(keyboard.getLanguageName()));
        return fileName;
    }

    //endregion
}
