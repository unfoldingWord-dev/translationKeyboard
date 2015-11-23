package org.distantshoresmedia.translationkeyboard20;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonObject;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.mime.TypedString;

/**
 * Created by Acts Media Inc on 3/12/14.
 */
public class DownloadUtil {

    protected static final int TIMEOUT_SECONDS = 10;
    private static final String TAG = "URLDownloadUtil";

    public static JsonObject downloadJson(String url){

        JsonObject object =  createClient(url).getJson();
        Log.i(TAG, "Got here");
        return object;
    }

    public interface TKClient {

        @GET("/")
        JsonObject getJson();
    }

    protected static RestAdapter.Builder getRestBuilder(@Nullable OkHttpClient optionClient, String url){

        RestAdapter.Builder retrofit = new RestAdapter.Builder()
                .setEndpoint(url)
                .setLogLevel(RestAdapter.LogLevel.FULL);

        if(optionClient != null){
            retrofit.setClient(new OkClient(optionClient));
        }
        return retrofit;
    }

    protected static TKClient createClient(String url){

        return createClient(getRestBuilder(getCustomClient(TIMEOUT_SECONDS), url));
    }

    protected static TKClient createClient(RestAdapter.Builder builder){
        return builder.build().create(TKClient.class);
    }


    protected static OkHttpClient getCustomClient(int timeoutSeconds){

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(timeoutSeconds, TimeUnit.SECONDS);
        client.setReadTimeout(timeoutSeconds, TimeUnit.SECONDS);
        client.setWriteTimeout(timeoutSeconds, TimeUnit.SECONDS);
        return client;
    }
}
