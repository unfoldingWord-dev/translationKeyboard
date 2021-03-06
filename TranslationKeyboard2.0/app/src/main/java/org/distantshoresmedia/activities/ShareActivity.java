package org.distantshoresmedia.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.distantshoresmedia.adapters.KeyboardsAdapter;
import org.distantshoresmedia.database.KeyboardDatabaseHandler;
import org.distantshoresmedia.fragments.ShareSelectionFragment;
import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.sideloading.SideLoadType;
import org.distantshoresmedia.sideloading.SideLoadingDataPreparer;
import org.distantshoresmedia.sideloading.SideSharer;
import org.distantshoresmedia.translationkeyboard20.R;
import org.json.JSONObject;

import java.util.List;

public class ShareActivity extends ActionBarActivity implements KeyboardsAdapter.KeyboardAdapterListener {

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

        SideSharer sharer = new SideSharer(this, new SideSharer.SideLoaderListener() {
            @Override
            public void sideLoadingSucceeded(String response) {

            }

            @Override
            public void sideLoadingFailed(String errorMessage) {

            }

            @Override
            public boolean confirmSideLoadingType(SideLoadType type) {

                if(type == SideLoadType.SIDE_LOAD_TYPE_QR_CODE && selectionFragment.getSelectedKeyboards().size() > 1){
                    showAmountAlert();
                    return false;
                }
                else{
                    return true;
                }
            }
        });

        if(selectionFragment.getSelectedKeyboards() != null && selectionFragment.getSelectedKeyboards().size() > 0) {
            sharer.startSharing(getData(), getFileName());
        }

    }

    private String getData() {

        JSONObject requestedKeyboardData = SideLoadingDataPreparer.getSideLoadingJson(getApplicationContext(), selectionFragment.getSelectedKeyboards());

        if (requestedKeyboardData != null){
            String data = requestedKeyboardData.toString();
            return data;
        }
        else
        {
            return null;
        }
    }

    private String getFileName(){

        List<AvailableKeyboard> keyboards = selectionFragment.getSelectedKeyboards();

        String fileName = (keyboards.size() > 1)? "kb_pack" : keyboards.get(0).getLanguageName();
        return fileName + ".tk";
    }

    private void showAmountAlert(){

        new AlertDialog.Builder(this)
                .setTitle("Keyboard Selection")
                .setMessage("Please select only 1 keyboard for QR sharing")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        new SideSharer(this, null).showBluetoothDirectionsDialog();
        Log.i(TAG, "activity result");
    }

    @Override
    public void rowSelectedOrDeselected() {
//        int numOfKeyboards = selectionFragment.getSelectedKeyboards().size();
    }
}
