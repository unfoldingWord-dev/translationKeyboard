package org.distantshoresmedia.org.distantshoresmedia.database;

import android.content.Context;
import android.util.Log;

import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.model.BaseKeyboard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Fechner on 12/31/14.
 */
public class FileLoader {

    private static final String TAG = "FileLoader";



    //region Out Methods
    /**
     *
     * @param fileString
     * @param fileName
     * @param context
     */
    protected static void saveFileToApplicationFiles(Context context, String fileString, String fileName){

        Log.i(TAG, "Attempting to save file named:" + fileName);

        try {
            File file = new File(context.getFilesDir(), fileName);

            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(fileString.getBytes());
            outputStream.close();
        }
        catch (FileNotFoundException e){
            System.out.println("saveFileToApplicationFiles FileNotFoundException: " + e.toString());
        }
        catch (IOException e){
            System.out.println("saveFileToApplicationFiles IOException: " + e.toString());
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

        try{
            InputStream fileStream = context.openFileInput(fileName);

            String resultString =  getStringFromInputStream(fileStream, fileName);
            return resultString;
        }
        catch (IOException e){
            System.out.println("initializeKeyboards IOException: " + e.toString());
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

            String resultString =  getStringFromInputStream(fileStream, fileName);
            return resultString;
        }
        catch (IOException e){
            System.out.println("initializeKeyboards IOException: " + e.toString());
            return null;
        }
    }

    /**
     *
     * @param fileStream
     * @param fileName
     * @return The String from the Stream or null if there's an error.
     */
    private static String getStringFromInputStream(InputStream fileStream, String fileName){

        String resultString = "";

        try{
            int size = fileStream.available();
            byte[] buffer = new byte[size];

            fileStream.read(buffer);

            fileStream.close();

            resultString = new String(buffer, "UTF-8");

        }
        catch (FileNotFoundException e){
            System.out.println("getStringFromInputStream file name: " + fileName + " FileNotFoundException: " + e.toString());
            return null;
        }
        catch (IOException e){
            System.out.println("getStringFromInputStream file name: " + fileName + " IOException: " + e.toString());
            return null;
        }

        return resultString;
    }


    //endregion
}
