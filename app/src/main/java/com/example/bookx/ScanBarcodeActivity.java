package com.example.bookx;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;

// imports for the Google Vision API to work
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import androidx.core.app.ActivityCompat;

// Source code used: https://www.youtube.com/watch?v=czmEC5akcos
// Source code used: Lect9RequestPermission2 (lecture code)

public class ScanBarcodeActivity extends Activity{
    SurfaceView cameraPreview;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);
        cameraPreview = findViewById(R.id.camera_preview); // blank activity used before the camera is opened

        createCameraSource();
    }

    private void createCameraSource(){
        // set up camera settings (e.g. we have auto focus turned on)
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();
        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override // checks app permission if the user granted access to the camera
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if(ActivityCompat.checkSelfPermission(ScanBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED  ){
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override // when you destroy or pause the activity
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        // detects any barcodes that appear
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override // if you find a barcode, put it into an intent and send it back to the previous activity
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(barcodes.size() > 0){
                    Intent intent = new Intent();
                    intent.putExtra("barcode", (Parcelable) barcodes.valueAt(0));
                    setResult(CommonStatusCodes.SUCCESS, intent);
                    finish(); // ends this activity and goes back into the stack
                }

            }
        });

    }
}