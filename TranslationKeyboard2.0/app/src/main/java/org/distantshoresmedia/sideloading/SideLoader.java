package org.distantshoresmedia.sideloading;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.distantshoresmedia.database.KeyboardDataHandler;
import org.distantshoresmedia.translationkeyboard20.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Fechner on 7/6/15.
 */
public class SideLoader {

    private static final String TAG = "SideLoader";

//    public interface SideLoaderListener{
//        void sideLoadingSucceeded(String response);
//        void sideLoadingFailed(String errorMessage);
//    }
//
//
//    public static void startSideLoading(SideLoadType type, SideLoaderListener listener){
//
//        switch (type){
//            case SIDE_LOAD_TYPE_BLUETOOTH:{
//                startSideLoadBluetooth(listener);
//                break;
//            }
//            case SIDE_LOAD_TYPE_NFC:{
//                startSideLoadNFC(listener);
//                break;
//            }
//            case SIDE_LOAD_TYPE_WIFI:{
//                startSideLoadWIFI(listener);
//                break;
//            }
//            case SIDE_LOAD_TYPE_STORAGE:{
//                startSideLoadStorage(listener);
//                break;
//            }
//            default:{
//
//            }
//        }
//    }
//
//    private static void startSideLoadBluetooth(SideLoaderListener listener){
//
//    }
//
//    private static void startSideLoadNFC(SideLoaderListener listener){
//
//    }
//
//    private static void startSideLoadWIFI(SideLoaderListener listener){
//
//    }
//
//    private static void startSideLoadStorage(SideLoaderListener listener){
//
//    }
//
//    public static void startDataSharing(SideLoadType type, String text, SideLoaderListener listener){
//
//        switch (type){
//            case SIDE_LOAD_TYPE_BLUETOOTH:{
//                startShareBluetooth(text, listener);
//                break;
//            }
//            case SIDE_LOAD_TYPE_NFC:{
//                startShareNFC(text, listener);
//                break;
//            }
//            case SIDE_LOAD_TYPE_WIFI:{
//                startShareWIFI(text, listener);
//                break;
//            }
//            case SIDE_LOAD_TYPE_STORAGE:{
//                startShareStorage(text, listener);
//                break;
//            }
//            default:{
//
//            }
//        }
//    }
//
//    private static void startShareBluetooth(String shareText, SideLoaderListener listener){
//
//    }
//
//    private static void startShareNFC(String shareText, SideLoaderListener listener){
//
//    }
//
//    private static void startShareWIFI(String shareText, SideLoaderListener listener){
//
//    }
//
//    private static void startShareStorage(String shareText, SideLoaderListener listener){
//
//    }

    public static void loadedContent(Activity activity, String text){

        findKeyboards(activity, text);
    }

    private static void findKeyboards(final Activity activity, final String json){

        int numberOfKeyboards = getNumberOfKeyboards(json);
        String keyboardText = (numberOfKeyboards == 1)? "Keyboard" : "Keyboards";

        View titleView = View.inflate(activity, R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Import " + numberOfKeyboards + " " + keyboardText + "?");

        AlertDialog dialogue = new AlertDialog.Builder(activity)
                .setCustomTitle(titleView)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveKeyboards(activity, json);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private static int getNumberOfKeyboards(String json){

        int numberOfKeyboards = 0;
        try {
            JSONArray availableKeyboards = new JSONObject(json).getJSONArray("keyboards");

            for(int i = 0; i < availableKeyboards.length(); i++){
                JSONObject available = availableKeyboards.getJSONObject(i);
                JSONObject keyboards = available.getJSONObject("keyboards");
                JSONArray variants = keyboards.getJSONArray("keyboard_variants");
                numberOfKeyboards += variants.length();
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        return numberOfKeyboards;
    }

    private static void saveKeyboards(Activity activity, String json){

        KeyboardDataHandler.sideLoadKeyboards(activity.getApplicationContext(), json);
        Log.i(TAG, "keyboard Loaded");
    }
}
