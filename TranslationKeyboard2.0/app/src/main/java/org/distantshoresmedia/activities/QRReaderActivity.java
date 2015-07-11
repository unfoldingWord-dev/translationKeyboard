package org.distantshoresmedia.activities;

import android.app.Activity;
import android.graphics.PointF;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import org.distantshoresmedia.translationkeyboard20.R;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.Inflater;

public class QRReaderActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener {

    private static final String TAG = "QRReaderActivity";

    private QRCodeReaderView mydecoderview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_reader);

        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {

        handleTextFound(text);
    }


    // Called when your device have no camera
    @Override
    public void cameraNotFound() {

    }

    // Called when there's no QR codes in the camera preview image
    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mydecoderview.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mydecoderview.getCameraManager().stopPreview();
    }

    private void handleTextFound(String text){

        byte[] data = Base64.decode(text, Base64.DEFAULT);
        String finalText = deCompressData(data);

//        String text1 = null;
//        try {
//            text1 = new String(data1, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }

    private String deCompressData(byte[] compressedBytes) {

        String deCompressedData = null;
        try {
//            if (compressedBytes.length() > 200) {
            byte[] output = new byte[10000];
            Inflater decompresser = new Inflater();
            decompresser.setInput(compressedBytes);
//            byte[] result = str.getBytes();
            int resultLength = decompresser.inflate(output);
            decompresser.end();

            // Decode the bytes into a String
            String outputString = new String(output, 0, resultLength, "UTF-8");
            System.out.println("Deflated String:" + outputString);
//            }
        }
        catch (Exception e) {
            e.printStackTrace();
            deCompressedData = null;
        }

        return deCompressedData;
    }
}
