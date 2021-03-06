package com.door43.tools.reporting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

/**
 * This class provides some utility methods for handling files
 */
public class FileUtilities {
    /**
     * Converts an input stream into a string
     * @param is the input stream
     * @return
     * @throws Exception
     */
    public static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    /**
     * Returns the contents of a file as a string
     * @param f
     * @return
     * @throws Exception
     */
    public static String getStringFromFile(File f) throws IOException {
        return getStringFromFile(f.getAbsolutePath());
    }

    /**
     * Returns the contents of a file as a string
     * @param filePath the path to the file
     * @return
     * @throws Exception
     */
    public static String getStringFromFile (String filePath) throws IOException {
        File fl = new File(filePath);
        if (fl.exists()) {
            FileInputStream fin = new FileInputStream(fl);
            String ret = convertStreamToString(fin);
            //Make sure you close all streams.
            fin.close();
            return ret;
        } else {
            return null;
        }
    }

    /**
     * Recursively deletes a direcotry or just deletes the file
     * @param fileOrDirectory
     */
    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }

    public static void write(File file) throws IOException {

    }

    /**
     * Attempts to move a file or directory. If moving fails it will try to copy instead.
     * @param sourceFile
     * @param destFile
     * @return
     */
    public static boolean moveOrCopy(File sourceFile, File destFile) {

        try {
            FileInputStream inStream = new FileInputStream(sourceFile);
            FileOutputStream outStream = new FileOutputStream(destFile);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
            return true;
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }


}
