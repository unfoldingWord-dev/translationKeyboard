package org.distantshoresmedia.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.distantshoresmedia.adapters.ShareAdapter;
import org.distantshoresmedia.database.FileLoader;
import org.distantshoresmedia.database.KeyboardDataHandler;
import org.distantshoresmedia.database.KeyboardDatabaseHandler;
import org.distantshoresmedia.fragments.ShareSelectionFragment;
import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.sideloading.SideLoadType;
import org.distantshoresmedia.sideloading.SideLoader;
import org.distantshoresmedia.sideloading.SideLoadingDataPreparer;
import org.distantshoresmedia.translationkeyboard20.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import ar.com.daidalos.afiledialog.FileChooserActivity;

public class LoadActivity extends ActionBarActivity {

    private static final String TAG = "LoadActivity";
    private ShareSelectionFragment selectionFragment;

    AvailableKeyboard[] keyboards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        Uri uri = getIntent().getData();

        if(uri != null){
            File keyboardFile = new File(uri.getPath());
            loadFile(keyboardFile);
        }
        else {
            showShareSelector();
        }
    }


    private void showShareSelector(){

        View titleView = View.inflate(getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Select Share Method");

        AlertDialog dialogue = new AlertDialog.Builder(this, R.style.ActionBarTheme)
                .setCustomTitle(titleView)
                .setAdapter(new ShareAdapter(getApplicationContext(), Arrays.asList(new String[]{"Load From Storage", "QR Code", "Bluetooth", "NFC"})),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                    case 0: {
                                        findKeyboards();
                                        break;
                                    }
                                    case 1: {
                                        startActivity(new Intent(getApplicationContext(), QRReaderActivity.class));
                                        break;
                                    }
                                    default: {
                                        dialog.cancel();
                                    }
                                }
//                                prepareData();
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

    private void findKeyboards(){

        Intent intent = new Intent(this, FileChooserActivity.class);
        intent.putExtra(FileChooserActivity.INPUT_SHOW_CONFIRMATION_ON_SELECT, true);
        intent.putExtra(FileChooserActivity.INPUT_REGEX_FILTER, ".*tk");
        this.startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            boolean fileCreated = false;
            String filePath = "";

            Bundle bundle = data.getExtras();
            if(bundle != null)
            {
                if(bundle.containsKey(FileChooserActivity.OUTPUT_NEW_FILE_NAME)) {
                    fileCreated = true;
                    File folder = (File) bundle.get(FileChooserActivity.OUTPUT_FILE_OBJECT);
                    String name = bundle.getString(FileChooserActivity.OUTPUT_NEW_FILE_NAME);
                    filePath = folder.getAbsolutePath() + "/" + name;
                    loadFile(new File(folder.getAbsolutePath(), name));
                } else {
                    fileCreated = false;
                    File file = (File) bundle.get(FileChooserActivity.OUTPUT_FILE_OBJECT);
                    filePath = file.getAbsolutePath();
                    loadFile(file);
                }
            }
        }
    }

    private void loadFile(File file){

        String fileText = FileLoader.getJSONStringFromFile(file);

        SideLoader.loadedContent(this, fileText);
    }

    private void findKeyboards(final String json){

        int numberOfKeyboards = getNumberOfKeyboards(json);
        String keyboardText = (numberOfKeyboards == 1)? "Keyboard" : "Keyboards";

        View titleView = View.inflate(getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Import " + numberOfKeyboards + " " + keyboardText + "?");

        AlertDialog dialogue = new AlertDialog.Builder(this)
                .setCustomTitle(titleView)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveKeyboards(json);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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

        KeyboardDataHandler.sideLoadKeyboards(getApplicationContext(), json);
        Log.i(TAG, "keyboard Loaded");
    }
}
