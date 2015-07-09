package org.distantshoresmedia.activities;

import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.distantshoresmedia.adapters.ShareAdapter;
import org.distantshoresmedia.database.KeyboardDatabaseHandler;
import org.distantshoresmedia.fragments.ShareSelectionFragment;
import org.distantshoresmedia.model.AvailableKeyboard;
import org.distantshoresmedia.translationkeyboard20.R;

import java.util.Arrays;

public class ShareActivity extends ActionBarActivity {

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
}
