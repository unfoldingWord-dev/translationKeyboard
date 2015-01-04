package org.distantshoresmedia.translationkeyboard20;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.distantshoresmedia.database.KeyboardDatabaseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Fechner on 11/28/14.
 */
public class KeyboardDownloader {

    static final String TAG = "KeyboardDownloader";

    static final String kBaseURL = "http://remote.actsmedia.com/api/";
    static final String kVersionUrlTag = "v1/";
    static final String kKeyboardUrlTag = "keyboard/";

    static final String kIdTag = "id";

    private Context context;

    static private KeyboardDownloader sharedInstance = null;


    //region Public Methods

    static public KeyboardDownloader getSharedInstance(){

        if(sharedInstance == null){
            sharedInstance = new KeyboardDownloader();
        }
        return sharedInstance;
    }

    public void updateKeyboards(Context context) {
        System.out.println("Will Download");
        this.context = context;

        UpdateFragment.getSharedInstance().setProgress(10, "Finding Most Recent Updates");
        getJSONFromUrl(this.context, getKeyboardUrl());
    }

    public void downloadKeyboard(String keyboardID){

        getJSONFromUrl(this.context, getKeyboardUrl(keyboardID));
    }

    //endregion

    //region URL Builders

    static public String getKeyboardUrl() {
        return kBaseURL + kVersionUrlTag + kKeyboardUrlTag;
    }

    static public String getKeyboardUrl(String keyboardKey) {
        return kBaseURL + kVersionUrlTag + kKeyboardUrlTag + keyboardKey;
    }

    //endregion

    //region Generic data methods

    public void getJSONFromUrl(Context context, String url) {

        Log.i(TAG, "will attempt to download URL: " + url);
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            new DownloadWebpageTask().execute(url);
        }else{
            System.out.println("Network Error");
        }
    }

    public void parseJSONString(String json){


        try{
            JSONObject jObject = new JSONObject(json);
            JSONArray keysArray = jObject.getJSONArray("keyboards");

            // this means it's the available keyboards json
            if(keysArray != null){
               KeyboardDatabaseHandler.updateKeyboardsDatabaseWithJSON(this.context, json);
            }
        }
        catch (JSONException e){
            try{
                JSONObject jObject = new JSONObject(json);
                String jArray = jObject.getString("keyboard_id");

                // this means it's an actual keyboard
                if(jArray.length() > 0){
                    KeyboardDatabaseHandler.updateOrSaveKeyboard(this.context, json);
                }
            }
            catch (JSONException ex) {
                Log.e(TAG, " JSONException: " + ex.toString());
                UpdateFragment.getSharedInstance().endProgress(false, "Update Failed.");
            }
        }
    }

    //endregion

    //region Downloader

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadUrl(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
//            System.out.println("Result: " + result);
            parseJSONString(result);
        }

        private String downloadUrl(String myUrl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 400000;

            try {
                URL url = new URL(myUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(20000 /* milliseconds */);
                conn.setConnectTimeout(30000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("Network", "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len).trim();

                return contentAsString;

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }
    }

    //endregion


}
