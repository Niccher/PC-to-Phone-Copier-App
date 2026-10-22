package com.niccher.p2p_copier_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.niccher.p2p_copier_app.R;
import com.niccher.p2p_copier_app.viewmodels.HomeViewModel;

public class Fragment_About extends Fragment {

    private HomeViewModel viewModel;

    public Fragment_About() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        android.widget.TextView tvVersion = view.findViewById(R.id.tv_about_version);
        if (tvVersion != null) {
            String versionText = "Version " + com.niccher.p2p_copier_app.BuildConfig.APP_VERSION + 
                    " (Build " + com.niccher.p2p_copier_app.BuildConfig.APP_VERSION_CODE + ")";
            tvVersion.setText(versionText);
        }
    }
}
