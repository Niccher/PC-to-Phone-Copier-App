package com.niccher.pctophonecopier.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.niccher.pctophonecopier.HomePage;
import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.interfaces.RetrofitInterface;
import com.niccher.pctophonecopier.model.Mod_Auth;
import com.niccher.pctophonecopier.model.Mod_Device_Id;
import com.niccher.pctophonecopier.utils.Helpers;
import com.niccher.pctophonecopier.utils.Konstants;
import com.niccher.pctophonecopier.utils.ServiceGenerator;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthSession extends AppCompatActivity {

    Button btn_scan_qr, btn_scan_verify_code;
    TextInputEditText typed_auth_code;
    AlertDialog.Builder builder;
    Konstants kon;
    Gson gson = null;

    RetrofitInterface retrofitInterface;

    SharedPreferences pref_Auth = null;
    SharedPreferences pref_Device = null;
    SharedPreferences.Editor sharedEditor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_session);

        btn_scan_verify_code = findViewById(R.id.btn_auth_verify_code);
        btn_scan_qr = findViewById(R.id.btn_auth_scan_qr);
        typed_auth_code = findViewById(R.id.ed_auth_scan_code);

        kon = new Konstants();
        gson = new GsonBuilder()
                .setLenient()
                .create();

        pref_Auth = getSharedPreferences(kon.shared_pref_auth, Context.MODE_PRIVATE);
        pref_Device = getSharedPreferences(kon.shared_pref_device, Context.MODE_PRIVATE);

        checkDevice();

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

    private void init_qr_scan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan the QR on the website");
        integrator.setOrientationLocked(false);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();

        CameraManager camma = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String camid = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                camid = camma.getCameraIdList()[0];
                //camma.setTorchMode(camid,true);
            } catch (CameraAccessException ex) {
                Toast.makeText(this, "Unable to lock the camera", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void code_verify() {
        String auth_inserted_code = typed_auth_code.getText().toString().trim();

        if (auth_inserted_code.isEmpty()) {
            Toast.makeText(AuthSession.this, "Please type the code on the website.", Toast.LENGTH_LONG).show();
        }else{
            tryAuth("code_num", auth_inserted_code);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                String auth_qr_code = String.valueOf(result.getContents());
                tryAuth("code_qr", auth_qr_code);
                Log.e("On Get", "Parsed data>: " + auth_qr_code);

                /*try {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Kopied", auth_qr_code);
                    if (clipboard == null || clip == null) return;
                    clipboard.setPrimaryClip(clip);
                    //Toast.makeText(getContext(), "Copied >\n"+selectedText, Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    ClipboardManager clipboard = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        clipboard = getSystemService(ClipboardManager.class);
                    }
                    ClipData clip = ClipData.newPlainText("Kopied", auth_qr_code);
                    if (clipboard == null || clip == null) return;
                    clipboard.setPrimaryClip(clip);
                }*/

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void createDeviceID() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("device_Board", String.valueOf(Build.BOARD)+"");
        parameters.put("device_Bootloader", String.valueOf(Build.BOOTLOADER)+"");
        parameters.put("device_Brand", String.valueOf(Build.BRAND)+"");
        parameters.put("device_Device", String.valueOf(Build.DEVICE)+"");
        parameters.put("device_Display", String.valueOf(Build.DISPLAY)+"");
        parameters.put("device_Fingerprint", String.valueOf(Build.FINGERPRINT)+"");
        parameters.put("device_Hardware", String.valueOf(Build.HARDWARE)+"");
        parameters.put("device_Host", String.valueOf(Build.HOST)+"");
        parameters.put("device_Manufacturer", String.valueOf(Build.MANUFACTURER)+"");
        parameters.put("device_Model", String.valueOf(Build.MODEL)+"");
        parameters.put("device_Product", String.valueOf(Build.PRODUCT)+"");
        parameters.put("device_Tags", String.valueOf(Build.TAGS)+"");
        parameters.put("device_Type", String.valueOf(Build.TYPE)+"");
        parameters.put("device_User", String.valueOf(Build.USER)+"");
        parameters.put("device_Serial", String.valueOf(Build.SERIAL)+"");

        Call<Mod_Device_Id> call = retrofitInterface.createDevice(parameters);

        call.enqueue(new Callback<Mod_Device_Id>() {
            @Override
            public void onResponse(Call<Mod_Device_Id> call, Response<Mod_Device_Id> response) {
                Mod_Device_Id postResponse = response.body();

                sharedEditor = pref_Device.edit();
                sharedEditor.putString("dev_status", postResponse.getDev_status());
                sharedEditor.putString("dev_time", postResponse.getDev_time());
                sharedEditor.putString("dev_message", postResponse.getDev_message());
                sharedEditor.putString("dev_uuid", postResponse.getDev_uuid());
                sharedEditor.apply();
            }

            @Override
            public void onFailure(Call<Mod_Device_Id> call, Throwable t) {
                Toast.makeText(AuthSession.this,  t.getMessage()+"\nCreateDeviceID\nUnknown error occurred, please try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void checkDevice(){
        SharedPreferences sharedPreferences = getSharedPreferences(kon.shared_pref_device, Context.MODE_PRIVATE);
        String state = sharedPreferences.getString("dev_status","failed");
        if (state.equals("success")){
            Toast.makeText(AuthSession.this, "Registered", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(AuthSession.this, "Un Registered", Toast.LENGTH_LONG).show();
            Retrofit retrof = new Retrofit.Builder()
                    .baseUrl(kon.str_device_action)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(ServiceGenerator.getUnsafeOkHttpClient())
                    .build();

            retrofitInterface = retrof.create(RetrofitInterface.class);
            createDeviceID();
        }
    }

    private void tryAuth(String auth_type, String auth_code) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(kon.str_auth_action)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(ServiceGenerator.getUnsafeOkHttpClient())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("var_auth_type", auth_type);
        parameters.put("var_auth_code", auth_code);
        parameters.put("var_dev_uuid", Helpers.get_prefs_dev("dev_uuid", this));

        Call<Mod_Auth> call = retrofitInterface.createRegister(parameters);

        call.enqueue(new Callback<Mod_Auth>() {
            @Override
            public void onResponse(Call<Mod_Auth> call, Response<Mod_Auth> response) {
                Mod_Auth postResponse = response.body();

                if (postResponse.getAuth_type().equals("False") || postResponse.getAuth_message().equals("unknown error")) {
                    Toast.makeText(AuthSession.this, "Unexpected request, please try again", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        if (postResponse.getAuth_status().equals("True")) {
                            Helpers.set_prefs_sess("auth_status", postResponse.getAuth_status(), AuthSession.this);
                            Helpers.set_prefs_sess("auth_type", postResponse.getAuth_type(), AuthSession.this);
                            Helpers.set_prefs_sess("auth_auth_code", postResponse.getAuth_auth_code(), AuthSession.this);
                            Helpers.set_prefs_sess("auth_message", postResponse.getAuth_message(), AuthSession.this);
                            Helpers.set_prefs_sess("auth_auth_code_id", postResponse.getAuth_auth_code_id(), AuthSession.this);
                            Helpers.set_prefs_sess("auth_time", postResponse.getAuth_time(), AuthSession.this);

                            Intent to_home = new Intent(AuthSession.this, HomePage.class);
                            to_home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(to_home);
                            AuthSession.this.finish();
                        }else{
                            Toast.makeText(AuthSession.this, "Verification failed. \nPlease try again", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(AuthSession.this, ex.getMessage() + "Unknown error occurred", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Mod_Auth> call, Throwable t) {
                Toast.makeText(AuthSession.this, t.getMessage() + "\nUnknown error occurred, please try again", Toast.LENGTH_LONG).show();
            }
        });
    }
}