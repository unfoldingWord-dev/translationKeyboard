package org.distantshoresmedia;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import org.json.*;

/**
 * Created by Fechner on 11/28/14.
 */
public class KeyboardDownloader {

    static final String kBaseURL = "http://remote.actsmedia.com/api/";
    static final String kVersionUrlTag = "v1/";
    static final String kKeyboardUrlTag = "keyboard/";
    static final private String kKeyboardKey = "keyboards";
    static final String kDirName = "assets";

    static final String kIdTag = "id";
    
    public static void updateKeyboards(){
        
        purgeDirectory(kDirName);
        
        String json = getStringFromUrl(kBaseURL + kVersionUrlTag + kKeyboardUrlTag);
        
        saveFile(json, FileNameHelper.getAvailableKeyboardsFileName());
        saveFile(json, FileNameHelper.getDownloadedKeyboardsFileName());
        
        downloadKeyboards(json);
    }
    
    public static void purgeDirectory(String directory){
        
        System.out.println("Will try to purge dir: " + directory);
        File dir = new File(directory);
        
         for (File file: dir.listFiles()){ 
             System.out.println("Name: " + file.getName());
             if (!file.isDirectory() && !file.getName().equals("installed_keyboards.tk")){
                 file.delete();
             }
         }
    }
    
    public static void downloadKeyboards(String availableKeyboardJson){
        
        JSONObject jsonObj = new JSONObject(availableKeyboardJson);
        
        JSONArray jsonArray = jsonObj.getJSONArray(kKeyboardKey);
        
        ArrayList<String> idList = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject rowObj = jsonArray.getJSONObject(i);
            Long rowId = rowObj.getLong(kIdTag);
            idList.add(Long.toString(rowId));
        }
        
        for(String id : idList){
            
            String json = getStringFromUrl(kBaseURL + kVersionUrlTag + kKeyboardUrlTag + id);
            saveFile(json, FileNameHelper.getKeyboardIDFileName(id));
        }
    }
    
    public static String getStringFromUrl(String url){
    URL u;
      InputStream is = null;
      DataInputStream dis;
      String s;
      
      String finalString = "";
 
      try {

         u = new URL(url);

         is = u.openStream();        
 
         dis = new DataInputStream(new BufferedInputStream(is));

         while ((s = dis.readLine()) != null) {
            System.out.println(s);
            finalString += s;
         }
 
      } catch (MalformedURLException mue) {
 
         System.out.println("Ouch - a MalformedURLException happened.");
         mue.printStackTrace();
         System.exit(1);
         return null;
 
      } catch (IOException ioe) {
 
         System.out.println("Oops- an IOException happened.");
         ioe.printStackTrace();
         System.exit(1);
         return null;
 
      } finally {

         try {
            is.close();
         } catch (IOException ioe) {
            return null;
         }
 
      }
      
      return finalString;
    }

    public static void saveFile(String fileString, String fileName) {

        fileName = kDirName + File.separator + fileName;
        System.out.println("fileName: " + fileName);
        try {
            File file = new File(fileName);

            // if file doesnt exists, then create it 
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileString);
            bw.close();
            System.out.println("Done writing to " + fileName); //For testing 
            
        } catch (IOException e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }
    }
}
