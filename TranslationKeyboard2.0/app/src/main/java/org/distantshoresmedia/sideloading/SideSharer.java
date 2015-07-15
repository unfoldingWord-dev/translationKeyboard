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

        List<String> optionsList = Arrays.asList("QR Code", "Bluetooth", "Choose Directory", "Save to SD Card", "Other");

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
                                        type = SideLoadType.SIDE_LOAD_TYPE_BLUETOOTH;
                                        break;
                                    }
                                    case 2: {
                                        type = SideLoadType.SIDE_LOAD_TYPE_STORAGE;
                                        break;
                                    }
                                    case 3: {
                                        type = SideLoadType.SIDE_LOAD_TYPE_SD_CARD;
                                        break;
                                    }
                                    case 4: {
                                        type = SideLoadType.SIDE_LOAD_TYPE_NFC;
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
                default: {

                }
            }
        }
    }

    private void startBluetoothShareAction(){

//        Intent intent = new Intent(activity.getApplicationContext(), BluetoothSharingActivity.class)
//                .putExtra(BluetoothSharingActivity.TEXT_PARAM, shareText);
//
//        activity.startActivityForResult(intent, 0);

        Uri fileUri = FileLoader.createTemporaryFile(activity.getApplicationContext(), getZippedText(), fileName);

        int currentAPIVersion = android.os.Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Intent sharingIntent = new Intent(
                    android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent
                    .setComponent(new ComponentName(
                            "com.android.bluetooth",
                            "com.android.bluetooth.opp.BluetoothOppLauncherActivity"));
            sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            activity.startActivity(sharingIntent);
        } else {
//            ContentValues values = new ContentValues();
//            values.put(BluetoothShare.URI, uri.toString());
//            Toast.makeText(getBaseContext(), "URi : " + uri,
//                    Toast.LENGTH_LONG).show();
//            values.put(BluetoothShare.DESTINATION, deviceAddress);
//            values.put(BluetoothShare.DIRECTION,
//                    BluetoothShare.DIRECTION_OUTBOUND);
//            Long ts = System.currentTimeMillis();
//            values.put(BluetoothShare.TIMESTAMP, ts);
//            getContentResolver().insert(BluetoothShare.CONTENT_URI,
//                    values);
        }
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

       new AsyncTask<Void, Void, String>(){
           protected String doInBackground(Void... params) {
               try {
                   ServerSocket serverSocket = new ServerSocket(8988);
                   Log.d(TAG, "Server: Socket opened");
                   Socket client = serverSocket.accept();
                   Log.d(TAG, "Server: connection done");
                   String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                   File folder = new File(extStorageDirectory, "Download");
                   File file = new File(folder,"wifixyz-" + System.currentTimeMillis()+".apk");
                   try {
                       file.createNewFile();
                   } catch (IOException e1) {
                       e1.printStackTrace();
                   }

                   Log.d(TAG, "server: copying files " + file.toString());
                   InputStream inputstream = client.getInputStream();
//                   copyFile(inputstream, new FileOutputStream(file));
                   serverSocket.close();
                   return file.getAbsolutePath();
               } catch(IOException e) {
                   Log.e(TAG, e.getMessage());
                   e.printStackTrace();
                   return null;
               }
           }
       }.execute();
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

    public static boolean sdCardIsPresent() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

}
