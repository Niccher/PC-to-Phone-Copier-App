package com.niccher.pctophonecopier;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.niccher.pctophonecopier.activities.Auth_New_Or_Continue;
import com.niccher.pctophonecopier.fragments.Fragment_History_Files;
import com.niccher.pctophonecopier.fragments.Fragment_Home;
import com.niccher.pctophonecopier.fragments.Fragment_History_Text;
import com.niccher.pctophonecopier.utils.Helpers;
import com.niccher.pctophonecopier.utils.SharedPrefs;
import com.niccher.pctophonecopier.viewmodels.HomeViewModel;

public class HomePage extends AppCompatActivity {

    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;
    MaterialToolbar toolbar;
    HomeViewModel viewModel;
    SharedPrefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply persisted dark theme before setContentView
        prefs = new SharedPrefs(this);
        boolean isDark = prefs.getBoolean("dark_theme", false);
        AppCompatDelegate.setDefaultNightMode(isDark
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_home_page);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        frameLayout = findViewById(R.id.frame);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Observe ViewModel LiveData
        viewModel.getSelectedFragment().observe(this, fragmentTag -> {
            Fragment selectedFragment = getFragmentForTag(fragmentTag);
            goToSelectedFragment(selectedFragment);
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            String fragmentTag;

            if (itemId == R.id.navigation_home) {
                fragmentTag = "home";
            } else if (itemId == R.id.navigation_history_files) {
                fragmentTag = "history_files";
            } else {
                fragmentTag = "home";
            }

            viewModel.selectFragment(fragmentTag);
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_dark_theme) {
            boolean currentlyDark = prefs.getBoolean("dark_theme", false);
            boolean newMode = !currentlyDark;
            prefs.putBoolean("dark_theme", newMode);
            AppCompatDelegate.setDefaultNightMode(newMode
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, newMode ? "Dark theme enabled" : "Light theme enabled", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.menu_current_token) {
            String token = Helpers.get_prefs_sess("auth_auth_code_id", this);
            String authCode = Helpers.get_prefs_sess("auth_auth_code", this);
            String display = "Session Token (ID): " + (token.isEmpty() ? "Not connected" : token)
                    + "\n\nAuth Code: " + (authCode.isEmpty() ? "Not connected" : authCode);
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Current Session Token")
                    .setMessage(display)
                    .setPositiveButton("Copy Token", (dialog, which) -> {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Token", token);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(this, "Token copied!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Close", null)
                    .show();
            return true;

        } else if (id == R.id.menu_logout) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Disconnect")
                    .setMessage("This will clear your session token. You will need to scan a new QR code to reconnect.")
                    .setPositiveButton("Disconnect", (dialog, which) -> {
                        Helpers.set_prefs_sess("auth_auth_code_id", "", this);
                        Helpers.set_prefs_sess("auth_auth_code", "", this);
                        Helpers.set_prefs_sess("auth_type", "", this);
                        Intent intent = new Intent(this, Auth_New_Or_Continue.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Fragment getFragmentForTag(String tag) {
        switch (tag) {
            case "home":
                return new Fragment_Home();
            case "history_files":
                return new Fragment_History_Files();
            default:
                return new Fragment_Home();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        goToSelectedFragment(new Fragment_Home());
    }

    public void goToSelectedFragment(Fragment selectedFragm) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, selectedFragm);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }
}
