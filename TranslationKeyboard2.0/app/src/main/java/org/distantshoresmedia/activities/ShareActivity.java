package org.distantshoresmedia.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.distantshoresmedia.adapters.ShareAdapter;
import org.distantshoresmedia.database.FileLoader;
import org.distantshoresmedia.database.KeyboardDatabaseHandler;
import org.distantshoresmedia.fragments.ShareSelectionFragment;
import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.sideloading.SideLoadingDataPreparer;
import org.distantshoresmedia.translationkeyboard20.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import ar.com.daidalos.afiledialog.FileChooserActivity;
import ar.com.daidalos.afiledialog.FileChooserDialog;

public class ShareActivity extends ActionBarActivity {

    private static final String TAG = "ShareActivity";
    private ShareSelectionFragment selectionFragment;

    AvailableKeyboard[] keyboards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        setupData();
        addFragment();
    }

    private void setupData(){
        keyboards = KeyboardDatabaseHandler.getInstalledKeyboards();
    }

    private void addFragment(){

        selectionFragment = ShareSelectionFragment.newInstance(keyboards);

        getSupportFragmentManager().beginTransaction().add(R.id.share_fragment_frame, selectionFragment).commit();
    }

    public void shareClicked(View view) {

        showShareSelector();
    }

    private void showShareSelector(){

        View titleView = View.inflate(getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Select Share Method");

        AlertDialog dialogue = new AlertDialog.Builder(this)
                .setCustomTitle(titleView)
                .setAdapter(new ShareAdapter(getApplicationContext(), Arrays.asList(new String[]{"Save to Storage", "QR Code", "Bluetooth", "NFC"})),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                    case 0: {
                                        chooseDirectory();
                                        break;
                                    }
                                    case 1: {
//                                        startActivity(Sharer.getSMSShareIntent(getApplicationContext(), shareText));
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

    private void chooseDirectory(){

        Intent intent = new Intent(this, FileChooserActivity.class);
        intent.putExtra(FileChooserActivity.INPUT_FOLDER_MODE, true);
        intent.putExtra(FileChooserActivity.INPUT_SHOW_CONFIRMATION_ON_SELECT, true);
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
                } else {
                    fileCreated = false;
                    File file = (File) bundle.get(FileChooserActivity.OUTPUT_FILE_OBJECT);
                    filePath = file.getAbsolutePath();
                }
                saveToFile(filePath, getData());
            }
//
//            String message = fileCreated? "File created" : "File opened";
//            message += ": " + filePath;
//            Toast toast = Toast.makeText(AFileDialogTestingActivity.this, message, Toast.LENGTH_LONG);
//            toast.show();
        }
    }

    private String getData() {

        JSONObject requestedKeyboardData = SideLoadingDataPreparer.getSideLoadingJson(getApplicationContext(), selectionFragment.getSelectedKeyboards());

        if (requestedKeyboardData != null){
            String data = requestedKeyboardData.toString();
            return data;
        }
        else{
            return null;
        }
    }

    private void saveToFile(String path, String text){

        FileLoader.saveFile(text, path, "test.tk");
    }

    private String compressData(String uncompressedData) {
        String compressedData = null;
        try {
            if (uncompressedData.length() > 200) {

                System.out.println("Actual Length:" + uncompressedData.length());

                byte[] originalBytes = uncompressedData.getBytes("UTF-8");

                Deflater deflater = new Deflater();
                deflater.setInput(originalBytes);
                deflater.finish();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                while (!deflater.finished()) {
                    int byteCount = deflater.deflate(buf);
                    baos.write(buf, 0, byteCount);
                }
                deflater.end();

                byte[] compressedBytes = baos.toByteArray();

                compressedData = new String(compressedBytes, 0, compressedBytes.length);

                System.out.println("Compressed data length:" + compressedBytes.length);
            }
        }
        catch (Exception e) {
            compressedData = null;
        }
        return compressedData;
    }

    private String deCompressData(String compressedData) {

        byte[] compressedBytes = compressedData.getBytes();
        String deCompressedData = null;
        try {
            if (compressedData.length() > 200) {

                Inflater decompresser = new Inflater();
                decompresser.setInput(compressedData.getBytes(), 0, compressedBytes.length);

                ByteArrayOutputStream dec = new ByteArrayOutputStream();

                byte[] result = new byte[8192];
                while (!decompresser.finished()) {
                    int byteCount = decompresser.inflate(result);
                    dec.write(result, 0, byteCount);
                }

                byte[] endResult = dec.toByteArray();

                int resultLength = compressedBytes.length;
                decompresser.end();

                String outStr = dec.toString("UTF-8");
                deCompressedData = outStr;
            }
        }
        catch (Exception e) {
            deCompressedData = null;
        }

        return deCompressedData;
    }

    private void compressText(String text){

        try {
            System.out.println("Actual Length:" + text.length());

            byte[] originalBytes = text.getBytes("UTF-8");

            Deflater deflater = new Deflater();
            deflater.setInput(originalBytes);
            deflater.finish();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            while (!deflater.finished()) {
                int byteCount = deflater.deflate(buf);
                baos.write(buf, 0, byteCount);
            }
            deflater.end();

            byte[] compressedBytes = baos.toByteArray();

            String compressedData = new String(compressedBytes, 0, compressedBytes.length);

            System.out.println("Compressed data length:" + compressedBytes.length);

            //Decompresses the data
            byte[] theBytes = compressedData.getBytes();

            Inflater decompresser = new Inflater();
            decompresser.setInput(theBytes, 0, compressedBytes.length);

            ByteArrayOutputStream dec = new ByteArrayOutputStream();

            byte[] result = new byte[8192];
            while (!decompresser.finished()) {
                int byteCount = decompresser.inflate(result);
                dec.write(result, 0, byteCount);
            }

            byte[] endResult = dec.toByteArray();

            int resultLength = compressedBytes.length;
            decompresser.end();

            String outStr = dec.toString("UTF-8");
            System.out.println("Decompressed data length:" + outStr.length());
        }
         catch (UnsupportedEncodingException e){
             e.printStackTrace();
         }
        catch (DataFormatException e){
            e.printStackTrace();
        }
    }

}
