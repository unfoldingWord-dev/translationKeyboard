package org.distantshoresmedia.translationkeyboard20;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Fechner on 11/28/14.
 */
public class KeyboardDownloader {

    static public void getJSON(){

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpost = new HttpPost("http://www.impaxis-securities.com/securities/cours-actions/cours.json");
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/json");
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String response;
        try {
            response = httpClient.execute(httpost, responseHandler);
            JSONArray arr = new JSONArray(response);
            int arrLength = arr.length();
            if(arrLength > 0)
            {
                for(int i = 0; i < arrLength; i++)
                {
                    JSONObject item = arr.getJSONObject(i);
                    String code = item.getString("code");
                    String coursjour = item.getString("coursjour");
                    String variation = item.getString("variation");
                    //Either insert to a DB or add to an array list and return it
                }
            }
        }
        catch (ClientProtocolException e) {
            //Issue with web server
        }
        catch (IOException e) {
            //Issue with request
        }
        catch (JSONException e) {
            //ISSUE Parsing JSON from site
        }
    }
}

//DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
//HttpPost httppost = new HttpPost(http://someJSONUrl/jsonWebService);
//// Depends on your web service
//        httppost.setHeader("Content-type", "application/json");
//
//        InputStream inputStream = null;
//        String result = null;
//        try {
//        HttpResponse response = httpclient.execute(httppost);
//        HttpEntity entity = response.getEntity();
//
//        inputStream = entity.getContent();
//        // json is UTF-8 by default
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
//        StringBuilder sb = new StringBuilder();
//
//        String line = null;
//        while ((line = reader.readLine()) != null)
//        {
//        sb.append(line + "\n");
//        }
//        result = sb.toString();
//        } catch (Exception e) {
//        // Oops
//        }
//        finally {
//        try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
//        }

//public class ImageManager {
//
//    private final String PATH = "/data/data/com.helloandroid.imagedownloader/";  //put the downloaded file here
//
//
//    public void DownloadFromUrl(String imageURL, String fileName) {  //this is the downloader method
//        try {
//            URL url = new URL("http://yoursite.com/&quot; + imageURL); //you can write here any link
//                    File file = new File(fileName);
//
//            long startTime = System.currentTimeMillis();
//            Log.d("ImageManager", "download begining");
//            Log.d("ImageManager", "download url:" + url);
//            Log.d("ImageManager", "downloaded file name:" + fileName);
//                        /* Open a connection to that URL. */
//            URLConnection ucon = url.openConnection();
//
//                        /*
//                         * Define InputStreams to read from the URLConnection.
//                         */
//            InputStream is = ucon.getInputStream();
//            BufferedInputStream bis = new BufferedInputStream(is);
//
//                        /*
//                         * Read bytes to the Buffer until there is nothing more to read(-1).
//                         */
//            ByteArrayBuffer baf = new ByteArrayBuffer(50);
//            int current = 0;
//            while ((current = bis.read()) != -1) {
//                baf.append((byte) current);
//            }
//
//                        /* Convert the Bytes read to a String. */
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(baf.toByteArray());
//            fos.close();
//            Log.d("ImageManager", "download ready in"
//                    + ((System.currentTimeMillis() - startTime) / 1000)
//                    + " sec");
//
//        } catch (IOException e) {
//            Log.d("ImageManager", "Error: " + e);
//        }
//
//    }
//}
