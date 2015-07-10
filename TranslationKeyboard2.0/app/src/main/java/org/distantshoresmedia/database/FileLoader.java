package org.distantshoresmedia.database;

import android.content.Context;
import android.util.Log;

import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.model.BaseKeyboard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by Fechner on 12/31/14.
 */
public class FileLoader {

    private static final String TAG = "FileLoader";

    //region Out Methods
    /**
     *
     * @param fileSequence
     * @param fileName
     * @param context
     */
    protected static void saveFileToApplicationFiles(Context context, CharSequence fileSequence, String fileName){

        Log.i(TAG, "Attempting to save file named:" + fileName);

        try {
            File file = new File(context.getFilesDir(), fileName);

            if (!file.exists()) {
                file.createNewFile();
            }
            String fileString = fileSequence.toString();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileString);
            bw.close();

//            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
//            outputStream.write(fileString.getBytes());
//            outputStream.close();
        }
        catch (FileNotFoundException e){
            Log.e(TAG, "saveFileToApplicationFiles FileNotFoundException: " + e.toString());
        }
        catch (IOException e){
            Log.e(TAG, "saveFileToApplicationFiles IOException: " + e.toString());
        }

        Log.i(TAG, "File saving was successful.");
    }

    public static void saveFile(CharSequence fileSequence, String dirName, String fileName){

        Log.i(TAG, "Attempting to save file named:" + fileName);

        try {
            File file = new File(dirName, fileName);

            if (!file.exists()) {
                file.createNewFile();
            }
            String fileString = fileSequence.toString();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileString);
            bw.close();
        }
        catch (FileNotFoundException e){
            Log.e(TAG, "saveFileToApplicationFiles FileNotFoundException: " + e.toString());
        }
        catch (IOException e){
            Log.e(TAG, "saveFileToApplicationFiles IOException: " + e.toString());
        }

        Log.i(TAG, "File saving was successful.");
    }

    //endregion


    //region In Method


    /**
     *
     * @param context
     * @param fileName
     * @return
     */
    protected static String getJSONStringFromApplicationFiles(Context context, String fileName){

        if(context == null || fileName == null){
            return null;
        }
        try{

            InputStream fileStream = context.openFileInput(fileName);

            String resultString =  getStringFromInputStream(fileStream, fileName).toString();
            return resultString;
        }
        catch (IOException e){
            Log.e(TAG, "initializeKeyboards IOException: " + e.toString());
            return null;
        }
    }

    /**
     *
     * @param context
     * @param fileName
     * @return
     */
    protected static String getJSONStringFromAssets(Context context, String fileName){

        try{
            InputStream fileStream = context.getAssets().open(fileName);

            String resultString = getStringFromInputStream(fileStream, fileName).toString();
            return resultString;
        }
        catch (IOException e){
            Log.e(TAG, "initializeKeyboards IOException: " + e.toString());
            return null;
        }
    }

    /**
     *
     * @param fileStream
     * @param fileName
     * @return The String from the Stream or null if there's an error.
     */
    private static CharSequence getStringFromInputStream(InputStream fileStream, String fileName){

        String resultString = "";

        try{

            BufferedReader in = new BufferedReader(new InputStreamReader(fileStream, "utf-8"));
            String str;

            while ((str = in.readLine()) != null) {
                resultString += str;
            }

            in.close();
        }
        catch (FileNotFoundException e){
            Log.e(TAG, "getStringFromInputStream file name: " + fileName + " FileNotFoundException: " + e.toString());
            return null;
        }
        catch (IOException e){
            Log.e(TAG, "getStringFromInputStream file name: " + fileName + " IOException: " + e.toString());
            return null;
        }

        return resultString;
    }


    protected static void deleteFile(Context context, String fileName){

        context.deleteFile(fileName);
    }


    //endregion
}
