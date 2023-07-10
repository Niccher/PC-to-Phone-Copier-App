package com.niccher.pctophonecopier.fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.activities.Handle_Text_2_Image;
import com.niccher.pctophonecopier.activities.Handle_Texts;

import static androidx.core.content.ContextCompat.getSystemService;

public class Fragment_Home extends Fragment {

    AlertDialog.Builder builder;

    ConstraintLayout layout_type_type, layout_type_pasted, layout_type_qr_scan, layout_type_img_scan, layout_type_upload;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.frag_home,container,false);

        layout_type_type = view.findViewById(R.id.option_1);
        layout_type_pasted = view.findViewById(R.id.option_2);
        layout_type_img_scan = view.findViewById(R.id.option_3);
        layout_type_qr_scan = view.findViewById(R.id.option_4);
        layout_type_upload = view.findViewById(R.id.option_5);

        layout_type_qr_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init_qr_scanner();
            }
        });

        layout_type_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_to_handle_texts = new Intent(getActivity(), Handle_Texts.class);
                startActivity(go_to_handle_texts);
            }
        });

        layout_type_pasted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_to_handle_texts = new Intent(getActivity(), Handle_Texts.class);
                startActivity(go_to_handle_texts);
            }
        });

        layout_type_img_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_to_handle_img_scan = new Intent(getActivity(), Handle_Text_2_Image.class);
                startActivity(go_to_handle_img_scan);
            }
        });

        layout_type_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "layout_type_upload", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void init_qr_scanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(Fragment_Home.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan text to copy");
        integrator.setOrientationLocked(false);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        //integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();

        CameraManager camma= (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        String camid=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                //camid=camma.getCameraIdList(0);
                camid=camma.getCameraIdList()[0];
                //camma.setTorchMode(camid,true);
            }catch (CameraAccessException ex){
                Toast.makeText(getActivity(), "Error--> "+ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(getActivity(), "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getActivity(), result.getContents(),Toast.LENGTH_LONG).show();
                final String pars=String.valueOf(result.getContents());
                Log.e("On Get", "Parsed data>: "+pars);
                builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setTitle("QR Contents...");
                builder.setMessage(result.getContents());
                builder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        try {
                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Kopied", pars);
                            if (clipboard == null ) return;
                            clipboard.setPrimaryClip(clip);
                            //Toast.makeText(getContext(), "Copied >\n"+selectedText, Toast.LENGTH_SHORT).show();

                        } catch (Exception ex){
                            Log.e("On Copy", "onClick: "+ex.getMessage());
                        }

                        try {
                            ClipboardManager clipboard = getSystemService(getContext(), ClipboardManager.class);ClipData clip = ClipData.newPlainText("Kopied", pars);
                            if (clipboard == null || clip == null) return;
                            clipboard.setPrimaryClip(clip);

                        } catch (Exception ex){
                            Log.e("On Copy 3", "onClick: "+ex.getMessage());
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
