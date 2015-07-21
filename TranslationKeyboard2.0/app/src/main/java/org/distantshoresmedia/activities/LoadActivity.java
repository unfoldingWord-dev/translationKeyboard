package org.distantshoresmedia.activities;

import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import org.distantshoresmedia.database.FileLoader;
import org.distantshoresmedia.sideloading.SideLoader;
import org.distantshoresmedia.translationkeyboard20.R;

import java.io.File;

public class LoadActivity extends ActionBarActivity implements QRCodeReaderView.OnQRCodeReadListener{

    private static final String TAG = "LoadActivity";

    private boolean hasFoundData = false;
    SideLoader loader;

    private QRCodeReaderView decoderView;
    public QRCodeReaderView getDecoderView() {
        return decoderView;
    }

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        listView = (ListView) findViewById(R.id.side_load_list_view);
        loader = new SideLoader(this, listView);


        Uri uri = getIntent().getData();

        if(uri != null){

            File keyboardFile = new File(uri.getPath());
            loader.textWasFound(loader.unzipText(FileLoader.getStringFromFile(keyboardFile)));
        }
        else {
            decoderView = (QRCodeReaderView) findViewById(R.id.qr_decoder_view);
            decoderView.setOnQRCodeReadListener(this);
            loader.startLoading();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(decoderView != null && decoderView.getCameraManager() != null) {
            decoderView.getCameraManager().stopPreview();
        }
    }

    @Override
    public void cameraNotFound() {

    }

    @Override
    public void onQRCodeRead(String s, PointF[] pointFs) {
        if (!hasFoundData) {

            decoderView.getCameraManager().stopPreview();
            hasFoundData = true;
            loader.textWasFound(loader.unzipText(s));

            decoderView.setVisibility(View.GONE);
        }
    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

    }
}
