package com.niccher.p2p_copier_app.activities;

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
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.niccher.p2p_copier_app.HomePage;
import com.niccher.p2p_copier_app.R;
import com.niccher.p2p_copier_app.interfaces.RetrofitInterface;
import com.niccher.p2p_copier_app.model.Mod_Auth;
import com.niccher.p2p_copier_app.model.api.ApiResponse;
import com.niccher.p2p_copier_app.model.Mod_Device_Id;
import com.niccher.p2p_copier_app.utils.Helpers;
import com.niccher.p2p_copier_app.utils.Konstants;
import com.niccher.p2p_copier_app.utils.ServiceGenerator;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthSession extends AppCompatActivity {

    Button btn_scan_qr;
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

        btn_scan_qr = findViewById(R.id.btn_auth_scan_qr);

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
    }

    private void init_qr_scan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan the QR code on your PC screen");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scanning cancelled", Toast.LENGTH_SHORT).show();
            } else {
                String rawResult = String.valueOf(result.getContents()).trim();
                Log.d("AuthSession", "Scanned raw QR: " + rawResult);

                String pairingCode = rawResult;
                String serverUrl = null;

                if (rawResult.startsWith("{") && rawResult.endsWith("}")) {
                    try {
                        JSONObject json = new JSONObject(rawResult);
                        if (json.has("srv")) {
                            serverUrl = json.optString("srv", "").trim();
                        }
                        if (json.has("code")) {
                            pairingCode = json.optString("code", pairingCode).trim();
                        } else if (json.has("sess")) {
                            pairingCode = json.optString("sess", pairingCode).trim();
                        }
                    } catch (JSONException e) {
                        Log.e("AuthSession", "JSON QR parse error", e);
                    }
                } else if (rawResult.contains("---")) {
                    pairingCode = rawResult.substring(0, rawResult.indexOf("---"));
                }

                if (serverUrl != null && !serverUrl.isEmpty()) {
                    boolean saved = Konstants.saveBackendFromUrl(AuthSession.this, serverUrl);
                    if (saved) {
                        ServiceGenerator.rebuildService(AuthSession.this);
                        Toast.makeText(AuthSession.this, "Server connected: " + serverUrl, Toast.LENGTH_SHORT).show();
                    }
                }

                tryAuthWithDeviceCheck("code_qr", pairingCode);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void tryAuthWithDeviceCheck(final String authType, final String authCode) {
        createDeviceID(new Runnable() {
            @Override
            public void run() {
                tryAuth(authType, authCode);
            }
        });
    }

    private void createDeviceID() {
        createDeviceID(null);
    }

    private void createDeviceID(@Nullable final Runnable onComplete) {
        retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class, this);

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

        Call<ApiResponse<Mod_Device_Id>> call = retrofitInterface.createDevice(parameters);

        call.enqueue(new Callback<ApiResponse<Mod_Device_Id>>() {
            @Override
            public void onResponse(Call<ApiResponse<Mod_Device_Id>> call, Response<ApiResponse<Mod_Device_Id>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Mod_Device_Id> apiResponse = response.body();
                    Mod_Device_Id postResponse = apiResponse.getData();
                    if (postResponse != null) {
                        Helpers.set_prefs_dev("dev_status", postResponse.getDev_status() != null ? postResponse.getDev_status() : "success", AuthSession.this);
                        Helpers.set_prefs_dev("dev_time", postResponse.getDev_time() != null ? postResponse.getDev_time() : "", AuthSession.this);
                        Helpers.set_prefs_dev("dev_message", postResponse.getDev_message() != null ? postResponse.getDev_message() : "", AuthSession.this);
                        if (postResponse.getDev_uuid() != null && !postResponse.getDev_uuid().isEmpty()) {
                            Helpers.set_prefs_dev("dev_uuid", postResponse.getDev_uuid(), AuthSession.this);
                        }
                    }
                }
                if (onComplete != null) {
                    onComplete.run();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Mod_Device_Id>> call, Throwable t) {
                Log.e("AuthSession", "createDeviceID failure", t);
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }

    public void checkDevice(){
        SharedPreferences sharedPreferences = getSharedPreferences(kon.shared_pref_device, Context.MODE_PRIVATE);
        String state = sharedPreferences.getString("dev_status","failed");
        if (state.equals("success")){
            // Device registered
        }else {
            retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class, this);
            createDeviceID();
        }
    }

    private void tryAuth(String auth_type, String auth_code) {
        retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class, this);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("var_auth_type", auth_type);
        parameters.put("var_auth_code", auth_code);
        parameters.put("var_dev_uuid", Helpers.get_prefs_dev("dev_uuid", this));

        Call<ApiResponse<Mod_Auth>> call = retrofitInterface.createRegister(parameters);

        call.enqueue(new Callback<ApiResponse<Mod_Auth>>() {
            @Override
            public void onResponse(Call<ApiResponse<Mod_Auth>> call, Response<ApiResponse<Mod_Auth>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(AuthSession.this, "Authentication failed (Server HTTP " + response.code() + ")", Toast.LENGTH_LONG).show();
                    return;
                }

                ApiResponse<Mod_Auth> apiResponse = response.body();
                Mod_Auth postResponse = apiResponse.getData();

                if (!apiResponse.isSuccess() || postResponse == null || "False".equalsIgnoreCase(postResponse.getAuth_status())) {
                    String msg = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Verification failed. Please try again";
                    Toast.makeText(AuthSession.this, msg, Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    if ("True".equalsIgnoreCase(postResponse.getAuth_status())) {
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
                    } else {
                        Toast.makeText(AuthSession.this, "Verification failed. \nPlease try again", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(AuthSession.this, ex.getMessage() + " - Unknown error occurred", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Mod_Auth>> call, Throwable t) {
                Toast.makeText(AuthSession.this, t.getMessage() + "\nUnknown error occurred, please try again", Toast.LENGTH_LONG).show();
            }
        });
    }
}