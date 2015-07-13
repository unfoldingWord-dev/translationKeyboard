package org.distantshoresmedia.sideloading;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import org.distantshoresmedia.activities.LoadActivity;
import org.distantshoresmedia.adapters.ShareAdapter;
import org.distantshoresmedia.database.FileLoader;
import org.distantshoresmedia.database.KeyboardDataHandler;
import org.distantshoresmedia.translationkeyboard20.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import ar.com.daidalos.afiledialog.FileChooserDialog;

/**
 * Created by Fechner on 7/6/15.
 */
public class SideLoader {

    private static final String TAG = "SideLoader";

    private LoadActivity activity;

    public SideLoader(LoadActivity activity) {
        this.activity = activity;

    }

    public void startLoading(){
        View titleView = View.inflate(activity.getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Select Share Method");

        List<String> optionsList = (sdCardIsPresent())? Arrays.asList("QR Code", "Choose Directory", "Load from SD Card")
                : Arrays.asList("QR Code", "Choose Directory");

        AlertDialog dialogue = new AlertDialog.Builder(activity)
                .setCustomTitle(titleView)
                .setAdapter(new ShareAdapter(activity.getApplicationContext(), optionsList),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SideLoadType type;
                                switch (which) {
                                    case 0: {
                                        type = SideLoadType.SIDE_LOAD_TYPE_QR_CODE;
                                        break;
                                    }
                                    case 1: {
                                        type = SideLoadType.SIDE_LOAD_TYPE_STORAGE;
                                        break;
                                    }
                                    case 2: {
                                        type = SideLoadType.SIDE_LOAD_TYPE_SD_CARD;
                                        break;
                                    }
                                    case 3: {
                                        type = SideLoadType.SIDE_LOAD_TYPE_BLUETOOTH;
                                        break;
                                    }
                                    default: {
                                        type = SideLoadType.SIDE_LOAD_TYPE_NONE;
                                        dialog.cancel();
                                    }
                                }
                                if(type != SideLoadType.SIDE_LOAD_TYPE_NONE) {
                                    startSideLoading(type);
                                }
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create();
        dialogue.show();
    }

    private void startSideLoading(SideLoadType type){

        switch (type) {
            case SIDE_LOAD_TYPE_BLUETOOTH: {
                startBluetoothLoadAction();
                break;
            }
            case SIDE_LOAD_TYPE_NFC: {
                startNFCLoadAction();
                break;
            }
            case SIDE_LOAD_TYPE_WIFI: {
                startWIFILoadAction();
                break;
            }
            case SIDE_LOAD_TYPE_STORAGE: {
                startStorageLoadAction();
                break;
            }
            case SIDE_LOAD_TYPE_SD_CARD:{
                startSDCardLoadAction();
                break;
            }
            case SIDE_LOAD_TYPE_QR_CODE:{
                startQRCodeAction();
                break;
            }
            default: {

            }
        }
    }

    private void startBluetoothLoadAction(){

    }

    private void startNFCLoadAction(){

    }

    private void startWIFILoadAction(){

    }

    private void startQRCodeAction(){

        QRCodeReaderView readerView = activity.getDecoderView();
        readerView.setVisibility(View.VISIBLE);
        readerView.getCameraManager().startPreview();
    }

    private void startStorageLoadAction(){
        loadStorage(null);
    }

    private void startSDCardLoadAction(){
        loadStorage("/" + activity.getString(R.string.english_ime_name));
    }

    private void loadStorage(String optionalDir){

        String finalDir = Environment.getExternalStorageDirectory().getPath();
        if(optionalDir != null && new File(finalDir + optionalDir).exists()){
            finalDir += optionalDir;
        }
        FileChooserDialog dialog = new FileChooserDialog(activity, finalDir);
        dialog.setFilter(".*tk");
        dialog.addListener(new FileChooserDialog.OnFileSelectedListener() {
            @Override
            public void onFileSelected(Dialog source, File file) {
                loadFile(file);
                source.dismiss();
            }

            @Override
            public void onFileSelected(Dialog source, File folder, String name) {
                loadFile(new File(folder.getAbsolutePath() + name));
                source.dismiss();
            }
        });
        dialog.show();
    }

    private void loadFile(File file){

        String fileText = FileLoader.getStringFromFile(file);
        textWasFound(unzipText(fileText));
    }

    private void showSuccessAlert(boolean success){

        new AlertDialog.Builder(activity)
                .setTitle("Load Status")
                .setMessage((success)? "Loading was successful" : "Loading failed")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        activity.finish();
                    }
                })
                .show();
    }

    public String unzipText(String text){
        return Zipper.decodeFromBase64EncodedString(text);
    }

    public void textWasFound(final String json){

        int numberOfKeyboards = getNumberOfKeyboards(json);
        String keyboardText = (numberOfKeyboards == 1)? "Keyboard" : "Keyboards";

        View titleView = View.inflate(activity.getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Import " + numberOfKeyboards + " " + keyboardText + "?");

        AlertDialog dialogue = new AlertDialog.Builder(activity)
                .setCustomTitle(titleView)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveKeyboards(json);
                        showSuccessAlert(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startLoading();
                    }
                })
                .show();
    }

    private int getNumberOfKeyboards(String json){

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

    private void saveKeyboards(String json){

        KeyboardDataHandler.sideLoadKeyboards(activity.getApplicationContext(), json);
        Log.i(TAG, "keyboard Loaded");
    }

    public static boolean sdCardIsPresent() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

}
