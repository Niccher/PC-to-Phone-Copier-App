package com.niccher.pctophonecopier.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.adapters.Adapter_Sel_Files;
import com.niccher.pctophonecopier.model.Mod_File_info;
import com.niccher.pctophonecopier.utils.Konstants;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;

public class Handle_Files extends AppCompatActivity {

    TextView btn_files, btn_upload;
    int get_files_code = 102;
    Konstants kon;

    ArrayList<Mod_File_info> my_got_file;
    Adapter_Sel_Files list_got_files;
    RecyclerView recyclerView_got_files;

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
        btn_upload = findViewById(R.id.btn_upload_files);

        btn_files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                //chooseFile.setType("text/plain");
                chooseFile.setType("*/*");
                startActivityForResult(Intent.createChooser(chooseFile, "Select  a file to upload"), get_files_code);
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Handle_Files.this, "Upload", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView_got_files = findViewById(R.id.desc_file_info_RecyclerView);
        recyclerView_got_files.setHasFixedSize(true);
        recyclerView_got_files.setLayoutManager(new LinearLayoutManager(getApplication()));

        my_got_file = new ArrayList<Mod_File_info>(1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == get_files_code && resultCode == Activity.RESULT_OK) {
            Uri selected_file = data.getData();
            my_got_file.add(new Mod_File_info(getFileName(selected_file)[0], getFileName(selected_file)[1], getFileName(selected_file)[2]));
            showAddedFile(my_got_file);
        } else {
        }
    }

    public String[] getFileName(Uri uri) {
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        //String[] f_info_data = {f_name, f_size, f_mime};
        Long f_size = Long.valueOf(returnCursor.getString(sizeIndex));
        String[] f_info_data = {returnCursor.getString(nameIndex), getContentResolver().getType(uri), humanReadableByteCountBin(f_size)};
        returnCursor.close();

        return f_info_data;
    }

    public static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }

    public void showAddedFile(ArrayList<Mod_File_info> my_got_file_passed) {
        list_got_files = new Adapter_Sel_Files(my_got_file_passed, getApplication());
        recyclerView_got_files.setAdapter(list_got_files);
        list_got_files.notifyDataSetChanged();
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