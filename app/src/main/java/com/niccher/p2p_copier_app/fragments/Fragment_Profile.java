package com.niccher.p2p_copier_app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.niccher.p2p_copier_app.R;
import com.niccher.p2p_copier_app.activities.Auth_New_Or_Continue;
import com.niccher.p2p_copier_app.interfaces.RetrofitInterface;
import com.niccher.p2p_copier_app.model.api.ApiResponse;
import com.niccher.p2p_copier_app.utils.DeviceMetricsHelper;
import com.niccher.p2p_copier_app.utils.Helpers;
import com.niccher.p2p_copier_app.utils.Konstants;
import com.niccher.p2p_copier_app.utils.ServiceGenerator;
import com.niccher.p2p_copier_app.utils.SharedPrefs;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Profile extends Fragment {

    private TextView txtDeviceModel, txtDeviceUuid, txtStatusLabel;
    private TextView txtAuthCode, txtAuthType, txtAuthTime, txtServerUrl;
    private TextView txtOs, txtScreenRes, txtAppVersion;
    private Button btnAbout, btnLogout, btnDeleteAll;
    private View dotStatus;

    public Fragment_Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtDeviceModel = view.findViewById(R.id.txt_device_model);
        txtDeviceUuid = view.findViewById(R.id.txt_device_uuid);
        txtStatusLabel = view.findViewById(R.id.txt_status_label);
        dotStatus = view.findViewById(R.id.dot_status);

        txtAuthCode = view.findViewById(R.id.txt_profile_auth_code);
        txtAuthType = view.findViewById(R.id.txt_profile_auth_type);
        txtAuthTime = view.findViewById(R.id.txt_profile_auth_time);
        txtServerUrl = view.findViewById(R.id.txt_profile_server_url);

        txtOs = view.findViewById(R.id.txt_profile_os);
        txtScreenRes = view.findViewById(R.id.txt_profile_screen_res);
        txtAppVersion = view.findViewById(R.id.txt_profile_app_version);

        btnLogout = view.findViewById(R.id.btn_profile_logout);
        btnDeleteAll = view.findViewById(R.id.btn_profile_delete_all);

        loadProfileData();

        btnLogout.setOnClickListener(v -> confirmLogout());
        btnDeleteAll.setOnClickListener(v -> confirmDeleteAllData());
    }

    private void loadProfileData() {
        Context context = requireContext();

        String brand = Build.BRAND != null ? Build.BRAND : "";
        String model = Build.MODEL != null ? Build.MODEL : "";
        txtDeviceModel.setText(brand + " " + model);

        String uuid = Helpers.get_prefs_dev("dev_uuid", context);
        txtDeviceUuid.setText(TextUtils.isEmpty(uuid) ? "Device UUID: Not Registered" : "UUID: " + uuid);

        String authStatus = Helpers.get_prefs_sess("auth_status", context);
        String authCode = Helpers.get_prefs_sess("auth_auth_code", context);
        String authType = Helpers.get_prefs_sess("auth_type", context);
        String authTime = Helpers.get_prefs_sess("auth_time", context);

        boolean isConnected = "True".equalsIgnoreCase(authStatus) || !TextUtils.isEmpty(authCode);

        if (isConnected) {
            dotStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
            txtStatusLabel.setText("Connected (" + (authCode.isEmpty() ? "Session Active" : authCode) + ")");
            txtAuthCode.setText(authCode.isEmpty() ? "Active" : authCode);

            try {
                RetrofitInterface api = ServiceGenerator.createService(RetrofitInterface.class, context);
                Map<String, String> params = new HashMap<>();
                params.put("var_dev_uuid", uuid);
                params.put("var_auth_code_id", Helpers.get_prefs_sess("auth_auth_code_id", context));

                api.checkSessionStatus(params).enqueue(new Callback<ApiResponse<com.google.gson.JsonObject>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<com.google.gson.JsonObject>> call, Response<ApiResponse<com.google.gson.JsonObject>> response) {
                        if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                            dotStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFF44336));
                            txtStatusLabel.setText("Session Expired / Revoked");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<com.google.gson.JsonObject>> call, Throwable t) {}
                });
            } catch (Exception ignored) {}
        } else {
            dotStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFF44336));
            txtStatusLabel.setText("Disconnected");
            txtAuthCode.setText("Not connected");
        }

        txtAuthType.setText(TextUtils.isEmpty(authType) ? "Unknown" : ("code_qr".equalsIgnoreCase(authType) ? "QR Code Scan" : "Manual Code"));
        txtAuthTime.setText(TextUtils.isEmpty(authTime) ? "Not available" : authTime);
        txtServerUrl.setText(Konstants.getBaseUrl(context));

        txtOs.setText("Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")");
        txtScreenRes.setText(DeviceMetricsHelper.getScreenResolution(context));
        txtAppVersion.setText("v1.1 (2)");
    }

    private void confirmLogout() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Disconnect Session?")
                .setMessage("Are you sure you want to log out from the active session? You will need to scan QR or enter a code to reconnect.")
                .setPositiveButton("Logout", (dialog, which) -> Helpers.logoutSession(requireContext()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDeleteAllData() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete All App Data & Reset?")
                .setMessage("WARNING: This will clear all saved session tokens, device identity cache, and server configuration. This action cannot be undone.")
                .setPositiveButton("Delete Everything", (dialog, which) -> Helpers.deleteAllAppDataAndReset(requireContext()))
                .setNegativeButton("Cancel", null)
                .show();
    }
}
