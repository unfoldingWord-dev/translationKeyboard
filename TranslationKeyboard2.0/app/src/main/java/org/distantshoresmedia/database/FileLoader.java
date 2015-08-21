package org.distantshoresmedia.database;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.model.BaseKeyboard;
import org.distantshoresmedia.translationkeyboard20.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by PJ Fechner on 12/31/14.
 * Class to load files.
 */
public class FileLoader {

    private static final String TAG = "FileLoader";

    //region Out Methods

    /**
     * Saves file to the application files
     * @param context context to use
     * @param textSequence File text
     * @param fileName File name to use
     */
    protected static void saveFileToApplicationFiles(Context context, CharSequence textSequence, String fileName){

        Log.i(TAG, "Attempting to save file named:" + fileName);

        try {
            File file = new File(context.getFilesDir(), fileName);

            if (!file.exists()) {
                file.createNewFile();
            }
            String fileString = textSequence.toString();
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

    /**
     * Save file to the passed directory
     * @param textSequence text of the file
     * @param dirName directory to place the file
     * @param fileName name of the file
     */
    public static void saveFile(CharSequence textSequence, String dirName, String fileName){

        Log.i(TAG, "Attempting to save file named:" + fileName);

        try {
            File file = new File(dirName, fileName);

            if (!file.exists()) {
                file.createNewFile();
            }
            String fileString = textSequence.toString();
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

    /**
     * Save the passed file to the SD card
     * @param textSequence Text of the file to create
     * @param fileName name of the file
     * @param context context to be used
     */
    public static void saveFileToSDCard(Context context, CharSequence textSequence, String fileName){

        Log.i(TAG, "Attempting to save file named:" + fileName);

        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getString(R.string.app_name), fileName);

            if (!file.exists()) {
                new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getString(R.string.app_name)).mkdirs();
                file.createNewFile();
            }
            String fileString = textSequence.toString();
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

    /**
     * Creates a temporary file in the directory/temp folder.
     * @param context context ot use
     * @param fileText text of the file
     * @param fileName name of the file
     * @return Uri of the created file.
     */
    public static Uri createTemporaryFile(Context context, CharSequence fileText, String fileName){

        clearTemporaryFiles(context);
        Log.i(TAG, "Attempting to save temporary file named:" + fileName);

        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/" + context.getString(R.string.app_name) + "/temp", fileName);

            if (!file.exists()) {
                new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + context.getString(R.string.app_name) + "/temp").mkdirs();
                file.createNewFile();
            }
            String fileString = fileText.toString();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileString);
            bw.close();
            Log.i(TAG, "createTemporaryFile saving was successful.");
            return Uri.fromFile(file);

//            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
//            outputStream.write(fileString.getBytes());
//            outputStream.close();
        }
        catch (FileNotFoundException e){
            Log.e(TAG, "createTemporaryFile FileNotFoundException: " + e.toString());
        }
        catch (IOException e){
            Log.e(TAG, "createTemporaryFile IOException: " + e.toString());
        }
        Log.i(TAG, "createTemporaryFile saving was unsuccessful.");
        return null;
    }

    /**
     * Clears out the temporary files folder
     * @param context context to use
     */
    public static void clearTemporaryFiles(Context context){

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + context.getString(R.string.app_name) + "/temp");
        if(file.exists()){
            final File to = new File(file.getAbsolutePath() + System.currentTimeMillis());
            file.renameTo(to);
            file.delete();
        }
    }

    //endregion


    //region In Method


    /**
     * @param context context to use
     * @param fileName name of the file
     * @return Text of the file requested
     */
    protected static String getJSONStringFromApplicationFiles(Context context, String fileName){

        if(context == null || fileName == null){
            return null;
        }
        try{

            InputStream fileStream = context.openFileInput(fileName);

            CharSequence sequence = getStringFromInputStream(fileStream, fileName);
            if(sequence != null) {
                return sequence.toString();
            }
            else{
                return null;
            }
        }
        catch (IOException e){
            Log.e(TAG, "initializeKeyboards IOException: " + e.toString());
            return null;
        }
    }

    /**
     * @param context context to use
     * @param fileName name of the file to be loaded
     * @return String of the desired file or null if it doesn't exist
     */
    protected static String getJSONStringFromAssets(Context context, String fileName){

        try{
            InputStream fileStream = context.getAssets().open(fileName);

            CharSequence sequence = getStringFromInputStream(fileStream, fileName);
            if(sequence != null) {
                return sequence.toString();
            }
            else{
                return null;
            }
        }
        catch (IOException e){
            Log.e(TAG, "initializeKeyboards IOException: " + e.toString());
            return null;
        }
    }

    /**
     * @param file file to be parsed
     * @return String from the passed file.
     */
    public static String getStringFromFile(File file){

        try{
            FileInputStream fileStream = new FileInputStream(file);

            CharSequence sequence = getStringFromInputStream(fileStream, file.getName());
            if(sequence != null) {
                return sequence.toString();
            }
            else{
                return null;
            }
        }
        catch (IOException e){
            Log.e(TAG, "initializeKeyboards IOException: " + e.toString());
            return null;
        }
    }

    /**
     * @param fileStream Stream from which to get a string
     * @param fileName name of the file
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
