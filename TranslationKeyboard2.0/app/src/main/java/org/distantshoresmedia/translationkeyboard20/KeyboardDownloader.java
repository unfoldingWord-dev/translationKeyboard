package org.distantshoresmedia.translationkeyboard20;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.distantshoresmedia.model.BaseKeyboard;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Fechner on 11/28/14.
 */
public class KeyboardDownloader {

    static final String kBaseURL = "http://remote.actsmedia.com/api/";
    static final String kVersionUrlTag = "v1/";
    static final String kKeyboardUrlTag = "keyboard/";

    static final String kIdTag = "id";

    private Context context;

    static public ArrayList<BaseKeyboard> keyboards = new ArrayList<BaseKeyboard>();


//    public JSONObject keyboards;


    public void downloadKeyboards(Context context) {
        System.out.println("Will Download");
        this.context = context;
        getJSONFromUrl(this.context, getKeyboardUrl("1"));
    }

    static public String getKeyboardUrl() {
        return kBaseURL + kVersionUrlTag + kKeyboardUrlTag;
    }

    static public String getKeyboardUrl(String keyboardKey) {
        return kBaseURL + kVersionUrlTag + kKeyboardUrlTag + keyboardKey;
    }

    public void parseJSONString(String json){

        try{
            JSONObject jObject = new JSONObject(json);
            JSONArray keysArray = jObject.getJSONArray("keyboards");

            if(keysArray != null){
                parseKeyboardsInfo(keysArray);
            }
        }
        catch (JSONException e){
            try{
                JSONObject jObject = new JSONObject(json);
                String jArray = jObject.getString("keyboard_id");

                if(jArray.length() > 0){
                    parseKeyboardType(jObject);
                }
            }
            catch (JSONException ex) {
                System.out.println(" JSONException: " + ex.toString());
            }
        }
    }

    public void parseKeyboardsInfo(JSONArray keyboardsArray){

//        System.out.println("KeysInfo: " + keyboardsArray.toString());

        for(int i = 0; i < keyboardsArray.length(); i++){
            try {
                JSONObject board = keyboardsArray.getJSONObject(i);
                Integer keyboardID = board.getInt(kIdTag);
                getJSONFromUrl(this.context, getKeyboardUrl(keyboardID.toString()));
            }
            catch (JSONException e) {
                System.out.println(" JSONException: " + e.toString());
            }
        }
    }

    public void parseKeyboardType(JSONObject keyObj){

//        System.out.println("KeysInfo: " + keyObj.toString());
        BaseKeyboard newKeyboard = BaseKeyboard.getKeyboardFromJsonObject(keyObj);
        keyboards.add(newKeyboard);
        System.out.println("Keyboard: " + newKeyboard.toString());
    }


    public void getJSONFromUrl(Context context, String url) {

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            new DownloadWebpageTask().execute(url);
        }else{
            System.out.println("Network Error");
        }
    }

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
            System.out.println("Result: " + result);
            parseJSONString(result);
        }

        private String downloadUrl(String myUrl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            int len = 500000;

            try {
                URL url = new URL(myUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                Log.d("Network", "The response is: " + response);
                is = conn.getInputStream();

                // Convert the InputStream into a string
                String contentAsString = readIt(is, len);

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
}
