package org.distantshoresmedia.sideloading;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import org.distantshoresmedia.activities.ShowQrCodeActivity;
import org.distantshoresmedia.adapters.ShareAdapter;
import org.distantshoresmedia.database.FileLoader;
import org.distantshoresmedia.translationkeyboard20.R;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import ar.com.daidalos.afiledialog.FileChooserDialog;

/**
 * Created by Fechner on 7/6/15.
 */
public class SideLoadingSharer {

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

    public SideLoadingSharer(Activity activity, SideLoaderListener listener) {
        this.activity = activity;
        this.listener = listener;

    }

    public void startSharing(String text, String fileName){
        this.shareText = text;
        this.fileName = fileName;

        View titleView = View.inflate(activity.getApplicationContext(), R.layout.alert_title, null);
        ((TextView) titleView.findViewById(R.id.alert_title_text_view)).setText("Select Share Method");

        List<String> optionsList = (sdCardIsPresent())? Arrays.asList("QR Code", "Choose Directory", "Save to SD Card")
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

    }

    private void startNFCShareAction(){

    }

    private void startWIFIShareAction(){

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
