package com.niccher.pctophonecopier.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.utils.Konstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Handle_Files extends AppCompatActivity {

    Button btn_files;
    int get_files_code = 102;
    Konstants kon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_files);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Handle Files");
        actionBar.setBackgroundDrawable(getDrawable(R.color.col_bg_dark_gray));
        actionBar.show();

        kon = new Konstants();

        btn_files = findViewById(R.id.btn_open_files);

        btn_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                //chooseFile.setType("text/plain");
                chooseFile.setType("*/*");
                startActivityForResult(Intent.createChooser(chooseFile,"Select  a file to upload"), get_files_code);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == get_files_code && resultCode == Activity.RESULT_OK){
            Uri selected_file = data.getData();
            String file_name = getFileName(selected_file);
            String file_path = "";

            //File file = new File(selected_file.getPath());
            //file_path = file.getAbsolutePath();

            //Log.e(kon.TAGGED, "getFileName(selected_file): " + file_path);
            Log.e(kon.TAGGED, "filePath(selected_file): " + file_name);
        }else{}
    }

    public String getFileName(Uri uri) {
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        int pathIndex = returnCursor.getColumnIndex("_data");
        returnCursor.moveToFirst();

        String f_name = returnCursor.getString(nameIndex);
        String f_size = returnCursor.getString(sizeIndex);
        String f_mime = getContentResolver().getType(uri);
        //String f_path = returnCursor.getString(pathIndex);
        //Log.e(kon.TAGGED, "getFileName(selected_file): " + f_path);
        returnCursor.close();
        return f_name;
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