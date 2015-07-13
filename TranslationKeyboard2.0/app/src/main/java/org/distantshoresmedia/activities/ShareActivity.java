package org.distantshoresmedia.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.util.Base64;

import org.distantshoresmedia.adapters.ShareAdapter;
import org.distantshoresmedia.database.FileLoader;
import org.distantshoresmedia.database.KeyboardDatabaseHandler;
import org.distantshoresmedia.fragments.ShareSelectionFragment;
import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.sideloading.SideLoadingDataPreparer;
import org.distantshoresmedia.sideloading.Zipper;
import org.distantshoresmedia.translationkeyboard20.R;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

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
                                        encodeAndDecode(getData());
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

    private void encodeAndDecode(String text){

        String encodedText = Zipper.encodeToBase64ZippedString(text);
        String decodedText = Zipper.decodeFromBase64EncodedString(encodedText);

        Log.i(TAG, "Did it worked!");


//        try {
//            // Encode a String into bytes
//            String inputString = text;
//            byte[] input = inputString.getBytes("UTF-8");
//
//            // Compress the bytes
//            byte[] output1 = new byte[input.length];
//            Deflater compresser = new Deflater();
//            compresser.setInput(input);
//            compresser.finish();
//            int compressedDataLength = compresser.deflate(output1);
//            compresser.end();
//
//            String str = Base64.encodeToString(output1, Base64.DEFAULT);
//            System.out.println("Deflated String:" + str);
//
//            byte[] output2 = Base64.decode(str, Base64.DEFAULT);
//
//            Inflater decompresser = new Inflater();
//            decompresser.setInput(output2);
//
//            byte[] finalResult = new byte[0];
//            byte[] result = new byte[8192];
//            while (!decompresser.finished()) {
//                int byteCount = decompresser.inflate(result, 0, 8192);
//                byte[] currentResult = new byte[finalResult.length + result.length];
//
//                System.arraycopy(finalResult, 0, currentResult, 0, finalResult.length);
//                System.arraycopy(result, finalResult.length, currentResult, 0, result.length);
//                finalResult = currentResult;
//
//            }
////            byte[] result = new byte[10000];
////            int resultLength = decompresser.inflate(result);
//            decompresser.end();
//
//            // Decode the bytes into a String
//            String outputString = new String(finalResult, "UTF-8");
//            outputString = outputString.trim();
//            System.out.println("Deflated String:" + outputString);
//        } catch (UnsupportedEncodingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (DataFormatException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
////        byte[] result = new byte[8192];
////        while (!decompresser.finished()) {
////            int byteCount = decompresser.inflate(result);
////            dec.write(result, 0, byteCount);
////        }

    }


}
