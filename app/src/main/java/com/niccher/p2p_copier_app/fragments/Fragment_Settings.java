package com.niccher.p2p_copier_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.niccher.p2p_copier_app.R;
import com.niccher.p2p_copier_app.utils.SharedPrefs;
import com.niccher.p2p_copier_app.viewmodels.HomeViewModel;

public class Fragment_Settings extends Fragment {

    private MaterialButtonToggleGroup toggleBiometric, toggleDarkTheme;
    private SharedPrefs prefs;
    private HomeViewModel viewModel;

    public Fragment_Settings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = new SharedPrefs(requireContext());
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);



        toggleBiometric = view.findViewById(R.id.toggle_group_biometric);
        toggleDarkTheme = view.findViewById(R.id.toggle_group_dark_theme);

        View btnBackend = view.findViewById(R.id.btn_open_backend_config);
        if (btnBackend != null) {
            btnBackend.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(requireContext(), com.niccher.p2p_copier_app.activities.BackendConfigActivity.class);
                startActivity(intent);
            });
        }

        // Initial States
        boolean bioEnabled = prefs.getBoolean("biometric_enabled", true);
        boolean darkEnabled = prefs.getBoolean("dark_theme", false);
        
        toggleBiometric.check(bioEnabled ? R.id.btn_bio_enable : R.id.btn_bio_disable);
        toggleDarkTheme.check(darkEnabled ? R.id.btn_dark_enable : R.id.btn_dark_disable);

        toggleBiometric.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                boolean enable = checkedId == R.id.btn_bio_enable;
                prefs.putBoolean("biometric_enabled", enable);
                showAppliedSnackbar(view, enable ? "Biometric Security Enabled" : "Biometric Security Disabled");
            }
        });

        toggleDarkTheme.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                boolean enable = checkedId == R.id.btn_dark_enable;
                if (prefs.getBoolean("dark_theme", false) != enable) {
                    prefs.putBoolean("dark_theme", enable);
                    AppCompatDelegate.setDefaultNightMode(enable
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO);
                    showAppliedSnackbar(view, enable ? "Dark Theme Applied" : "Light Theme Applied");
                }
            }
        });
    }

    private void showAppliedSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                .show();
    }
}
