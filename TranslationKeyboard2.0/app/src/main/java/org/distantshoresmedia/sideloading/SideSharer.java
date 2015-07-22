package org.distantshoresmedia.sideloading;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.distantshoresmedia.activities.BluetoothSharingActivity;
import org.distantshoresmedia.activities.ShowQrCodeActivity;
import org.distantshoresmedia.adapters.ShareAdapter;
import org.distantshoresmedia.database.FileLoader;
import org.distantshoresmedia.translationkeyboard20.R;
import org.distantshoresmedia.wifiDirect.WiFiDirectActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import ar.com.daidalos.afiledialog.FileChooserDialog;

/**
 * Created by Fechner on 7/6/15.
 */
public class SideSharer {

    private static final String TAG = "SideLoader";

    public static final String SHARE_TEXT_PARAM = "SHARE_TEXT_PARAM";

    private String fileName;
    private String shareText;
    private Activity activity;
    private SideLoaderListener listener;

    public interface SideLoaderListener{
        void sideLoadingSucceeded(String response);
        void sideLoadingFailed(String errorMessage);
        boolean confirmSideLoadingType(SideLoadType type);
    }

    public SideSharer(Activity activity, SideLoaderListener listener) {
        this.activity = activity;
        this.listener = listener;

    }

    public void startSharing(String text, String fileName){
        this.shareText = text;
        this.fileName = fileName;

        View titleView = View.inflate(activity.getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Select Share Method");

//        List<String> optionsList = Arrays.asList("QR Code", "Bluetooth", "Choose Directory", "Save to SD Card", "WiFi Direct", "Other");

        AlertDialog dialogue = new AlertDialog.Builder(activity)
                .setCustomTitle(titleView)
                .setAdapter(new ShareAdapter(activity.getApplicationContext(), SideLoaderTypeHandler.getListOfSideLoadTypes(activity, false)),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                startSideLoading(SideLoaderTypeHandler.getTypeForIndex(activity, which, false));
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

        if(listener.confirmSideLoadingType(type)) {

            switch (type) {
                case SIDE_LOAD_TYPE_BLUETOOTH: {
                    startBluetoothShareAction();
                    break;
                }
                case SIDE_LOAD_TYPE_NFC: {
                    startNFCShareAction();
                    break;
                }
                case SIDE_LOAD_TYPE_WIFI: {
                    startWIFIShareAction();
                    break;
                }
                case SIDE_LOAD_TYPE_STORAGE: {
                    startStorageShareAction();
                    break;
                }
                case SIDE_LOAD_TYPE_QR_CODE: {
                    startQRCodeShareAction();
                    break;
                }
                case SIDE_LOAD_TYPE_SD_CARD:{
                    startSDCardShareAction();
                    break;
                }
                case SIDE_LOAD_TYPE_OTHER:{
                    startShareOtherAction();
                }
                default: {

                }
            }
        }
    }

    private void startShareOtherAction(){

        Uri fileUri = FileLoader.createTemporaryFile(activity.getApplicationContext(), getZippedText(), fileName);
        Intent sharingIntent = new Intent(
                android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        activity.startActivity(sharingIntent);
    }

    private void startBluetoothShareAction(){

        View titleView = View.inflate(activity.getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Start Bluetooth Sharing");

        AlertDialog dialogue = new AlertDialog.Builder(activity)
                .setCustomTitle(titleView)
                .setMessage("Before starting, make sure the device you're sharing with has bluetooth available by going to it's Settings app, selecting bluetooth and confirming it is on and available to pair.")
                .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openBluetoothSharing();
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

    private void openBluetoothSharing(){

        Uri fileUri = FileLoader.createTemporaryFile(activity.getApplicationContext(), getZippedText(), fileName);


        Intent sharingIntent = new Intent(
                android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent
                .setComponent(new ComponentName(
                        "com.android.bluetooth",
                        "com.android.bluetooth.opp.BluetoothOppLauncherActivity"));
        sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        activity.startActivityForResult(sharingIntent, 0);
    }

    private void startNFCShareAction(){

        Uri fileUri = FileLoader.createTemporaryFile(activity.getApplicationContext(), getZippedText(), fileName);

        Intent sharingIntent = new Intent(
                android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
//        sharingIntent
//                .setComponent(new ComponentName(
//                        "com.android.nfchip" +
//                                "",
//                        "com.android.nfc.opp.BeamShareActivity"));
        sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        activity.startActivity(sharingIntent);

    }

    private void startWIFIShareAction(){

        Uri fileUri = FileLoader.createTemporaryFile(activity.getApplicationContext(), getZippedText(), fileName);

        Intent intent = new Intent(activity.getApplicationContext(), WiFiDirectActivity.class)
                .setData(fileUri);
        activity.startActivity(intent);
    }

    private void startStorageShareAction(){

        FileChooserDialog dialog = new FileChooserDialog(activity, Environment.getExternalStorageDirectory().getAbsolutePath());
        dialog.addListener(fileChooserLister);
        dialog.setFolderMode(true);
        dialog.setShowConfirmation(true, false);
        dialog.show();
    }

    private void startSDCardShareAction(){
        FileLoader.saveFileToSDCard(activity.getApplicationContext(), getZippedText(), fileName);
        showSuccessAlert(true);
    }

    private void startQRCodeShareAction(){

        Intent qrIntent = new Intent(activity.getApplicationContext(), ShowQrCodeActivity.class)
                .putExtra(SHARE_TEXT_PARAM, getZippedText());

        this.activity.startActivity(qrIntent);
    }

    private void showSuccessAlert(boolean success){

        new AlertDialog.Builder(activity)
                .setTitle("Share Status")
                .setMessage((success)? "Sharing was successful" : "Sharing failed")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        activity.finish();
                    }
                })
                .show();
    }

    private void saveToFile(String dir){
        FileLoader.saveFile(getZippedText(), dir, fileName);
        showSuccessAlert(true);
    }

    private String getZippedText(){
        return Zipper.encodeToBase64ZippedString(shareText);
    }

    private FileChooserDialog.OnFileSelectedListener fileChooserLister = new FileChooserDialog.OnFileSelectedListener() {
        @Override
        public void onFileSelected(Dialog source, File file) {
            saveToFile(file.getAbsolutePath());
            source.dismiss();
        }

        @Override
        public void onFileSelected(Dialog source, File folder, String name) {
            saveToFile(folder.getAbsolutePath() + "/" + name);
            source.dismiss();
        }
    };

    public void showBluetoothDirectionsDialog(){

        View titleView = View.inflate(activity.getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Next Steps");

        AlertDialog dialogue = new AlertDialog.Builder(activity)
                .setCustomTitle(titleView)
                .setMessage(activity.getString(R.string.bluetooth_direction_text))
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .create();
        dialogue.show();

    }
}
