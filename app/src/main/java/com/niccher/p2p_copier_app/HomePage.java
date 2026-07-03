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
import com.niccher.p2p_copier_app.fragments.Fragment_Home;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(isSubPage);
        getSupportActionBar().setHomeButtonEnabled(isSubPage);

        String title = "P2P Copier";
        switch (tag) {
            case "home": title = "Home"; break;
            case "settings": title = "Settings"; break;
            case "about": title = "About App"; break;
            case "credits": title = "App Credits"; break;
            case "history_files": title = "File History"; break;
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
        boolean isHome = "home".equals(currentFragment);
        
        // Only show settings/info items on Home screen
        MenuItem itemToken = menu.findItem(R.id.menu_current_token);
        MenuItem itemSettings = menu.findItem(R.id.menu_settings);
        MenuItem itemAbout = menu.findItem(R.id.menu_about);
        MenuItem itemCredits = menu.findItem(R.id.menu_credits);
        
        if (itemToken != null) itemToken.setVisible(isHome);
        if (itemSettings != null) itemSettings.setVisible(isHome);
        if (itemAbout != null) itemAbout.setVisible(isHome);
        if (itemCredits != null) itemCredits.setVisible(isHome);
        
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

        } else if (id == R.id.menu_settings) {
            viewModel.selectFragment("settings");
            return true;

        } else if (id == R.id.menu_about) {
            viewModel.selectFragment("about");
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
