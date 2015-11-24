package org.distantshoresmedia.utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by Acts Media Inc on 3/12/14.
 */
public class DownloadUtil {

    protected static final int TIMEOUT_SECONDS = 10;
    private static final String TAG = "URLDownloadUtil";


    private static String run(String url) throws IOException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static JsonObject downloadJson(String url) {

        try {
            return (new JsonParser()).parse(run(url)).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
//        r
//        OkHttpClient client = new OkHttpClient();
//
//        String run(String url) throws IOException {
//            Request request = new Request.Builder()
//                    .url(url)
//                    .build();
//
//            Response response = client.newCall(request).execute();
//            return response.body().string();

//        Call<TypedByteArray> object =  createClient(url).getJson();
//        Log.i(TAG, "Got here");
//        try {
//            Response response = object.execute();
//            Log.i(TAG, "Got here");
//            return null;
//        }
//        catch (IOException e){
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//
//
//    public interface TKClient {
//
//        @GET("/")
//        Call getJson();
//    }
//
//    protected static TKClient getRestBuilder(OkHttpClient optionClient, String url){
//
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(url)
//                .client(optionClient)
//                .build();
//
//        return retrofit.create(TKClient.class);
////        if(optionClient != null){
////            retrofit.setClient(new OkClient(optionClient));
////        }
////        return retrofit;
//    }
//
//    protected static TKClient createClient(String url){
//
//        return getRestBuilder(getCustomClient(TIMEOUT_SECONDS), url);
//    }
//
////    protected static TKClient createClient(RestAdapter.Builder builder){
////        return builder.build().create(TKClient.class);
////    }
//
//
//    protected static OkHttpClient getCustomClient(int timeoutSeconds){
//
//        OkHttpClient client = new OkHttpClient();
//        client.setConnectTimeout(timeoutSeconds, TimeUnit.SECONDS);
//        client.setReadTimeout(timeoutSeconds, TimeUnit.SECONDS);
//        client.setWriteTimeout(timeoutSeconds, TimeUnit.SECONDS);
//        return client;
//    }
//}
