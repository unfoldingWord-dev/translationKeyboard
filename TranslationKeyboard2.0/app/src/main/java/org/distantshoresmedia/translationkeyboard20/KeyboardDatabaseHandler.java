package org.distantshoresmedia.translationkeyboard20;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Fechner on 12/18/14.
 */
public class KeyboardDatabaseHandler {

    private static final String kKeyboardsFileName = "keyboards";
    private static final String kDefaultKeyboardFileName = "default_keyboard";

    private static final String kKeyboardExtensionName = ".tk";

    private static String getKeyboardFileName(){
        return kKeyboardsFileName + kKeyboardExtensionName;
    }
    private static String getDefaultKeyboardFileName(){
        return kDefaultKeyboardFileName + kKeyboardExtensionName;
    }



    public static void initializeDatabase(Context context){

        if(false){//keyboardsHaveBeenSaved(context)){
            System.out.println("KeyboardsAlreadySaved");
        }
        else{
            System.out.println("Keyboards Will Be Intialized");
            initializeKeyboards(context);
        }

    }

    /**
     * *
     * @param context
     * @return true if the keyboards file exists.
     */
    private static boolean keyboardsHaveBeenSaved(Context context){

        try{
            FileInputStream file = context.openFileInput(getKeyboardFileName());
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

    public static String getJSONStringForDefaultKeyboard(Context context) {

        return getJSONStringFromFile(context, getDefaultKeyboardFileName());
    }

    public static String getJSONStringForKeyboards(Context context) {

        return getJSONStringFromFile(context, getKeyboardFileName());
    }

    public static String getJSONStringFromFile(Context context, String fileName){

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

    private static void initializeKeyboards(Context context){

        String resultString = "";

        try{

            InputStream fileStream = context.getAssets().open(getKeyboardFileName());

            int size = fileStream.available();

            byte[] buffer = new byte[size];

            fileStream.read(buffer);

            fileStream.close();

            resultString = new String(buffer, "UTF-8");

            System.out.println("initializeKeyboards SavedString = " + resultString);
        }
        catch (IOException e){
            System.out.println("initializeKeyboards IOException: " + e.toString());
        }

        saveFile(resultString, getKeyboardFileName(), context);
    }

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




}
