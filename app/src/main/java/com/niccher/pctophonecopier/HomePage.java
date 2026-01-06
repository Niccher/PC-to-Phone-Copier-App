package com.niccher.pctophonecopier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.niccher.pctophonecopier.fragments.Fragment_History_Files;
import com.niccher.pctophonecopier.fragments.Fragment_Home;
import com.niccher.pctophonecopier.fragments.Fragment_History_Text;
import com.niccher.pctophonecopier.viewmodels.HomeViewModel;

public class HomePage extends AppCompatActivity {

    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;
    HomeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

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
