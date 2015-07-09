package org.distantshoresmedia.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.distantshoresmedia.adapters.ShareAdapter;
import org.distantshoresmedia.database.KeyboardDatabaseHandler;
import org.distantshoresmedia.fragments.ShareSelectionFragment;
import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.sideloading.SideLoadingDataPreparer;
import org.distantshoresmedia.translationkeyboard20.R;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
//                                        startActivity(Sharer.getEmailShareIntent(getApplicationContext(), shareText));
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
                                prepareData();
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

    private void prepareData(){

        JSONArray ar = SideLoadingDataPreparer.getSideLoadingJson(getApplicationContext(), selectionFragment.getSelectedKeyboards());

        String data = ar.toString();

        String zipString = compressData(data);
        String unCompressedString = deCompressData(zipString);
        compressText(data);
        Log.i(TAG, "Done");
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
