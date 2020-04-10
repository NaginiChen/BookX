package com.example.bookx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

public class MainActivity extends AppCompatActivity {

    TextView barcodeResult;
    Button scanBtn;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barcodeResult = findViewById(R.id.barcodeText);
        scanBtn = findViewById(R.id.scanBarcodeBtn);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanBarcode(view);
            }
        });
    }

    public void scanBarcode(View v){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, ScanBarcodeActivity.class);
            startActivityForResult(intent, 0);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},  PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, ScanBarcodeActivity.class);
                startActivityForResult(intent, 0);
            }
            else {
                Toast.makeText(getBaseContext(), "CAMERA DENIED", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0){
            if(resultCode == CommonStatusCodes.SUCCESS){
                if(data != null){
                    Barcode barcode = data.getParcelableExtra("barcode");
                    barcodeResult.setText("Barcode value: " + barcode.displayValue);
                }
                else{
                    barcodeResult.setText("No barcode found");
                }
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


}
