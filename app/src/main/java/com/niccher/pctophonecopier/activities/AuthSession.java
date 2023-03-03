package com.niccher.pctophonecopier.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.niccher.pctophonecopier.R;

public class AuthSession extends AppCompatActivity {

    Button btn_scan_qr, btn_scan_verify_code;
    //MaterialButton btn_scan_verify_code;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_session);

        btn_scan_verify_code = findViewById(R.id.btn_auth_verify_code);
        btn_scan_qr = findViewById(R.id.btn_auth_scan_qr);

        btn_scan_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init_qr_scan();
            }
        });

        btn_scan_verify_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                code_verify();
            }
        });
    }

    private void init_qr_scan(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan text to copy");
        integrator.setOrientationLocked(false);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();

        CameraManager camma= (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String camid=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                camid=camma.getCameraIdList()[0];
                //camma.setTorchMode(camid,true);
            }catch (CameraAccessException ex){
                //Toast.makeText(this, "Error--> "+ex.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Unable to lock the camera", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void code_verify(){
        Toast.makeText(this, "Pending", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, result.getContents(),Toast.LENGTH_LONG).show();
                final String pars=String.valueOf(result.getContents());
                Log.e("On Get", "Parsed data>: "+pars);
                builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("QR Contents...");
                builder.setMessage(result.getContents());
                builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        try {
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Kopied", pars);
                            if (clipboard == null || clip == null) return;
                            clipboard.setPrimaryClip(clip);
                            //Toast.makeText(getContext(), "Copied >\n"+selectedText, Toast.LENGTH_SHORT).show();

                        } catch (Exception ex){
                            Log.e("On Copy", "onClick: "+ex.getMessage());
                        }

                        try {
                            ClipboardManager clipboard = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                clipboard = getSystemService(ClipboardManager.class);
                            }
                            ClipData clip = ClipData.newPlainText("Kopied", pars);
                            if (clipboard == null || clip == null) return;
                            clipboard.setPrimaryClip(clip);

                        } catch (Exception ex){
                            Log.e("On Copy 3", "onClick: "+ex.getMessage());
                            Toast.makeText(AuthSession.this, "", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                android.app.AlertDialog alertdialog = builder.create();
                alertdialog.show();

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}