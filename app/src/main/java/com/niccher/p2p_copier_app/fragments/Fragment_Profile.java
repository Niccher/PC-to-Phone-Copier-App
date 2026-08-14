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
import com.niccher.p2p_copier_app.utils.DeviceMetricsHelper;
import com.niccher.p2p_copier_app.utils.Helpers;
import com.niccher.p2p_copier_app.utils.Konstants;
import com.niccher.p2p_copier_app.utils.SharedPrefs;

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

        btnAbout = view.findViewById(R.id.btn_profile_about);
        btnLogout = view.findViewById(R.id.btn_profile_logout);
        btnDeleteAll = view.findViewById(R.id.btn_profile_delete_all);

        loadProfileData();

        if (btnAbout != null) {
            btnAbout.setOnClickListener(v -> {
                new androidx.lifecycle.ViewModelProvider(requireActivity())
                        .get(com.niccher.p2p_copier_app.viewmodels.HomeViewModel.class)
                        .selectFragment("about");
            });
        }
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
                .setPositiveButton("Logout", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        Context context = requireContext();
        Helpers.set_prefs_sess("auth_status", "False", context);
        Helpers.set_prefs_sess("auth_type", "", context);
        Helpers.set_prefs_sess("auth_auth_code", "", context);
        Helpers.set_prefs_sess("auth_message", "", context);
        Helpers.set_prefs_sess("auth_auth_code_id", "", context);
        Helpers.set_prefs_sess("auth_time", "", context);

        Toast.makeText(context, "Session disconnected", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(context, Auth_New_Or_Continue.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void confirmDeleteAllData() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete All App Data?")
                .setMessage("WARNING: This will clear all saved session tokens, device metrics cache, and server configuration. This action cannot be undone.")
                .setPositiveButton("Delete Everything", (dialog, which) -> performDeleteAllData())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performDeleteAllData() {
        Context context = requireContext();
        Konstants kon = new Konstants();

        // Clear SharedPreferences
        context.getSharedPreferences(kon.shared_pref_auth, Context.MODE_PRIVATE).edit().clear().apply();
        context.getSharedPreferences(kon.shared_pref_device, Context.MODE_PRIVATE).edit().clear().apply();
        context.getSharedPreferences(Konstants.PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply();

        SharedPrefs customPrefs = new SharedPrefs(context);
        customPrefs.putBoolean("dark_theme", false);
        customPrefs.putBoolean("biometric_enabled", true);

        Toast.makeText(context, "All app data cleared successfully", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(context, Auth_New_Or_Continue.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
