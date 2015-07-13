package org.distantshoresmedia.activities;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import org.distantshoresmedia.sideloading.Zipper;
import org.distantshoresmedia.translationkeyboard20.R;

public class QRReaderActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener {

    private static final String TAG = "QRReaderActivity";

    private QRCodeReaderView mydecoderview;

    private boolean hasFoundData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_reader);

        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        if(!hasFoundData) {
            handleTextFound(text);
            hasFoundData = true;
        }
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

        String decodedText = Zipper.decodeFromBase64EncodedString(text);
        if(decodedText != null) {
//            SideLoadingSharer.loadedContent(this, decodedText);
        }
    }
}
