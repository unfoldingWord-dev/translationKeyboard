package com.door43.tools.reporting;

import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by Fechner on 11/23/15.
 */
public class GitHubClient {

    private static final String ENDPOINT_URL = "https://api.github.com/repos/unfoldingWord-dev/translationKeyboard";
    private static final String ISSUE_URL_TAG = "/issues";
    protected static final int TIMEOUT_SECONDS = 10;

    public interface GHClient {

        @POST(ISSUE_URL_TAG)
        JsonObject submitIssue(@Header("Authorization") String authorization, @Body IssueBody body);
    }

    protected static RestAdapter.Builder getRestBuilder(@Nullable OkHttpClient optionClient){

        RestAdapter.Builder retrofit = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL);

        if(optionClient != null){
            retrofit.setClient(new OkClient(optionClient));
        }
        return retrofit;
    }

    public static GHClient createClient(){

        return createClient(getRestBuilder(getCustomClient(TIMEOUT_SECONDS)));
    }

    protected static GHClient createClient(RestAdapter.Builder builder){
        return builder.build().create(GHClient.class);
    }

    protected static OkHttpClient getCustomClient(int timeoutSeconds){

        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(timeoutSeconds, TimeUnit.SECONDS);
        client.setReadTimeout(timeoutSeconds, TimeUnit.SECONDS);
        client.setWriteTimeout(timeoutSeconds, TimeUnit.SECONDS);
        return client;
    }

    public static class IssueBody implements Serializable{

        public String title;
        public String body;
        public String[] labels;

        public IssueBody(String title, String body, String[] labels) {
            this.title = title;
            this.body = body;
            this.labels = labels;
        }
    }
}
