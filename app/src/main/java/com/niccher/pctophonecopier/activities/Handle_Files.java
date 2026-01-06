package com.niccher.pctophonecopier.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.adapters.Adapter_Sel_Files;
import com.niccher.pctophonecopier.model.Mod_File_info;
import com.niccher.pctophonecopier.utils.Helpers;
import com.niccher.pctophonecopier.utils.Konstants;

import java.util.ArrayList;

public class Handle_Files extends AppCompatActivity {

    FloatingActionButton fab_files;
    int get_files_code = 102;
    public static int code_read_files = 123;
    Konstants kon;
    Helpers helpers = null;

    Adapter_Sel_Files list_got_files;
    RecyclerView recyclerView_got_files;

    // ViewModel
    private com.niccher.pctophonecopier.viewmodels.FileViewModel fileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_files);

        // Initialize ViewModel
        fileViewModel = new ViewModelProvider(this).get(com.niccher.pctophonecopier.viewmodels.FileViewModel.class);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Handle Files");
        actionBar.setBackgroundDrawable(getDrawable(R.color.col_bg_dark_gray));
        actionBar.show();

        kon = new Konstants();
        helpers = new Helpers();

        fab_files = findViewById(R.id.fab_open_files);

        fab_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Floating Button", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                //chooseFile.setType("text/plain");
                chooseFile.setType("*/*");
                startActivityForResult(Intent.createChooser(chooseFile, "Select  a file to upload"), get_files_code);
            }
        });

        selPermissions();

        recyclerView_got_files = findViewById(R.id.desc_file_info_RecyclerView);
        recyclerView_got_files.setHasFixedSize(true);
        recyclerView_got_files.setLayoutManager(new LinearLayoutManager(getApplication()));

        // Observe ViewModel LiveData
        fileViewModel.getSelectedFiles().observe(this, files -> {
            showAddedFile(new ArrayList<>(files));
        });

        fileViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                android.widget.Toast.makeText(this, error, android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        fileViewModel.getUploadSuccess().observe(this, success -> {
            if (success) {
                android.widget.Toast.makeText(this, "Files uploaded successfully!", android.widget.Toast.LENGTH_SHORT).show();
                fileViewModel.clearFiles();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == get_files_code && resultCode == Activity.RESULT_OK) {
            Uri uri_selected_file = data.getData();
            if (uri_selected_file != null) {
                fileViewModel.addFile(uri_selected_file);
            }
        }
    }

    public void showAddedFile(ArrayList<Mod_File_info> my_got_file_passed) {
        list_got_files = new Adapter_Sel_Files(my_got_file_passed, getApplication());
        recyclerView_got_files.setAdapter(list_got_files);
        list_got_files.notifyDataSetChanged();
    }

    private void selPermissions() {
        // For Android 13+ (API 33+), we don't need storage permissions for ACTION_GET_CONTENT
        // The system handles this automatically
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            // For older versions, check and request legacy storage permissions if needed
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, code_read_files);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}