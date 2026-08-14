package com.niccher.p2p_copier_app;

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
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.niccher.p2p_copier_app.activities.Auth_New_Or_Continue;
import com.niccher.p2p_copier_app.fragments.Fragment_History_Files;
import com.niccher.p2p_copier_app.fragments.Fragment_History_Overview;
import com.niccher.p2p_copier_app.fragments.Fragment_Home;
import com.niccher.p2p_copier_app.fragments.Fragment_Profile;
import com.niccher.p2p_copier_app.fragments.Fragment_History_Text;
import com.niccher.p2p_copier_app.fragments.Fragment_Settings;
import com.niccher.p2p_copier_app.fragments.Fragment_About;
import com.niccher.p2p_copier_app.fragments.Fragment_Credits;
import com.niccher.p2p_copier_app.utils.Helpers;
import com.niccher.p2p_copier_app.utils.SharedPrefs;
import com.niccher.p2p_copier_app.viewmodels.HomeViewModel;

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
            updateToolbarForFragment(fragmentTag);
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
            } else if (itemId == R.id.navigation_history_overview) {
                fragmentTag = "history_overview";
            } else if (itemId == R.id.navigation_profile) {
                fragmentTag = "profile";
            } else {
                fragmentTag = "home";
            }

            viewModel.selectFragment(fragmentTag);
            return true;
        });

        // Initialize with Home if first run
        if (savedInstanceState == null) {
            viewModel.selectFragment("home");
        }
    }

    private void updateToolbarForFragment(String tag) {
        if (getSupportActionBar() == null) return;

        boolean isSubPage = tag.equals("settings") || tag.equals("about") || tag.equals("credits");

        if (toolbar != null) {
            toolbar.setVisibility(android.view.View.VISIBLE);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(isSubPage);
        getSupportActionBar().setHomeButtonEnabled(isSubPage);

        String title = "P2P Copier";
        switch (tag) {
            case "home": title = "Home"; break;
            case "settings": title = "Settings"; break;
            case "about": title = "About App & Technical Specs"; break;
            case "credits": title = "App Credits"; break;
            case "history_files": title = "Uploaded"; break;
            case "history_overview": title = "Activity Log"; break;
            case "profile": title = "Profile & Session"; break;
        }
        getSupportActionBar().setTitle(title);
        
        // Hide bottom nav for sub-pages to focus on interaction
        bottomNavigationView.setVisibility(isSubPage ? android.view.View.GONE : android.view.View.VISIBLE);
        
        // Refresh menu to hide/show items
        invalidateOptionsMenu();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String currentFragment = viewModel.getSelectedFragment().getValue();
        boolean isMainTab = "home".equals(currentFragment) || "history_files".equals(currentFragment)
                || "history_overview".equals(currentFragment) || "profile".equals(currentFragment);
        
        MenuItem itemToken = menu.findItem(R.id.menu_current_token);
        MenuItem itemBackend = menu.findItem(R.id.menu_backend_config);
        MenuItem itemSettings = menu.findItem(R.id.menu_settings);
        MenuItem itemAbout = menu.findItem(R.id.menu_about);
        MenuItem itemLogout = menu.findItem(R.id.menu_logout);
        MenuItem itemCredits = menu.findItem(R.id.menu_credits);
        
        if (itemToken != null) itemToken.setVisible(isMainTab);
        if (itemBackend != null) itemBackend.setVisible(isMainTab);
        if (itemSettings != null) itemSettings.setVisible(isMainTab);
        if (itemAbout != null) itemAbout.setVisible(isMainTab);
        if (itemLogout != null) itemLogout.setVisible(isMainTab);
        if (itemCredits != null) itemCredits.setVisible(isMainTab);
        
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_current_token) {
            String token = Helpers.get_prefs_sess("auth_auth_code_id", this);
            String authCode = Helpers.get_prefs_sess("auth_auth_code", this);
            String display = "Session Token (ID): " + (token.isEmpty() ? "Not connected" : token)
                    + "\n\nAuth Code: " + (authCode.isEmpty() ? "Not connected" : authCode);
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Current Session Token")
                    .setMessage(display)
                    .setPositiveButton("Copy Token", (dialog, which) -> {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        if (clipboard != null) {
                            ClipData clip = ClipData.newPlainText("Token", token);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(this, "Token copied!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Close", null)
                    .show();
            return true;

        } else if (id == R.id.menu_backend_config) {
            Intent configIntent = new Intent(this, com.niccher.p2p_copier_app.activities.BackendConfigActivity.class);
            startActivity(configIntent);
            return true;

        } else if (id == R.id.menu_settings) {
            viewModel.selectFragment("settings");
            return true;

        } else if (id == R.id.menu_about) {
            viewModel.selectFragment("about");
            return true;

        } else if (id == R.id.menu_logout) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Disconnect Session?")
                    .setMessage("Are you sure you want to log out from the active session?")
                    .setPositiveButton("Logout", (dialog, which) -> Helpers.logoutSession(this))
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;

        } else if (id == R.id.menu_credits) {
            viewModel.selectFragment("credits");
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
            case "history_overview":
                return new Fragment_History_Overview();
            case "profile":
                return new Fragment_Profile();
            case "settings":
                return new Fragment_Settings();
            case "about":
                return new Fragment_About();
            case "credits":
                return new Fragment_Credits();
            default:
                return new Fragment_Home();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Redundant, removed
    }

    public void goToSelectedFragment(Fragment selectedFragm) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, selectedFragm);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        String currentFragment = viewModel.getSelectedFragment().getValue();
        if (currentFragment != null && (currentFragment.equals("settings") || 
            currentFragment.equals("about") || currentFragment.equals("credits"))) {
            viewModel.selectFragment("home");
        } else {
            super.onBackPressed();
        }
    }
}
