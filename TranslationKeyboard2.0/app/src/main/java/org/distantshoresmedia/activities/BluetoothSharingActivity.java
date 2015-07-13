package org.distantshoresmedia.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.distantshoresmedia.translationkeyboard20.R;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class BluetoothSharingActivity extends ActionBarActivity {

    private static final String TAG = "BluetoothSharingAct";
    private BluetoothSPP bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_sharing);
        bluetooth = new BluetoothSPP(getApplicationContext());

        bluetooth.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                Log.i(TAG, "data: " + message);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!bluetooth.isBluetoothEnabled()) {
            // Do somthing if bluetooth is disable
        } else {
            // Do something if bluetooth is already enable
        }
    }

    private void startBluetoothService(){

        bluetooth.startService(BluetoothState.DEVICE_ANDROID);

        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bluetooth.connect(data);
                bluetooth.send("Message", true);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bluetooth.setupService();
                bluetooth.startService(BluetoothState.DEVICE_ANDROID);
                startBluetoothService();
            } else {
                // Do something if user doesn't choose any device (Pressed back)
            }
        }
    }

}
