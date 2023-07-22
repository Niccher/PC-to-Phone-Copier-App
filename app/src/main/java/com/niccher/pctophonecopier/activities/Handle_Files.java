package com.niccher.pctophonecopier.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.niccher.pctophonecopier.HomePage;
import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.adapters.Adapter_Sel_Files;
import com.niccher.pctophonecopier.interfaces.AuthUser;
import com.niccher.pctophonecopier.model.Mod_Auth;
import com.niccher.pctophonecopier.model.Mod_File_Uploaded;
import com.niccher.pctophonecopier.model.Mod_File_info;
import com.niccher.pctophonecopier.utils.FileUtils;
import com.niccher.pctophonecopier.utils.Helpers;
import com.niccher.pctophonecopier.utils.Konstants;
import com.niccher.pctophonecopier.utils.ServiceGenerator;

import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Handle_Files extends AppCompatActivity {

    TextView btn_files, btn_upload;
    int get_files_code = 102;
    public static int code_read_files = 123;
    Konstants kon;
    Gson gson = null;
    Helpers helpers = null;

    ArrayList<Uri> sel_files;
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
        helpers = new Helpers();
        gson = new GsonBuilder()
                .setLenient()
                .create();

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
                try {
                    Uri uri_file_sample = sel_files.get(0);
                    filesUpload(uri_file_sample);
                }catch (Exception es){
                    Toast.makeText(Handle_Files.this, es.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        selPermissions();

        recyclerView_got_files = findViewById(R.id.desc_file_info_RecyclerView);
        recyclerView_got_files.setHasFixedSize(true);
        recyclerView_got_files.setLayoutManager(new LinearLayoutManager(getApplication()));

        my_got_file = new ArrayList<Mod_File_info>(1);
        sel_files = new ArrayList<Uri>(1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == get_files_code && resultCode == Activity.RESULT_OK) {
            Uri uri_selected_file = data.getData();
            sel_files.add(uri_selected_file);
            my_got_file.add(new Mod_File_info(getFileName(uri_selected_file)[0], getFileName(uri_selected_file)[1], getFileName(uri_selected_file)[2]));
            showAddedFile(my_got_file);
            //filesUpload(uri_selected_file);
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

    private void selPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, code_read_files);
            }
        }
    }

    public void filesUpload(Uri file_uri){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(kon.str_file_upload_action)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(ServiceGenerator.getUnsafeOkHttpClient())
                .build();

        AuthUser int_upload = retrofit.create(AuthUser.class);

        File file_to_upload = FileUtils.getFile(this, file_uri);

        RequestBody requestFile = RequestBody.create(MediaType.parse(getFileName(file_uri)[1]), file_to_upload );
        MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file_to_upload.getName(), requestFile);

        // add another part within the multipart request
        String part_dev_id = helpers.get_prefs_dev("dev_uuid", getApplication());

        RequestBody requestBody0 = RequestBody.create( okhttp3.MultipartBody.FORM, part_dev_id);

        // finally, execute the request
        Call<Mod_File_Uploaded> call = int_upload.filesUpload(requestBody0, body);
        call.enqueue(new Callback<Mod_File_Uploaded>() {
            @Override
            public void onResponse(Call<Mod_File_Uploaded> call, Response<Mod_File_Uploaded> response) {
                Mod_File_Uploaded postResponse = response.body();

                if (postResponse.getStatus() == 0  || postResponse.getStatus() == 2 ) {
                    Toast.makeText(Handle_Files.this, postResponse.getMessage(), Toast.LENGTH_LONG).show();
                }else if (postResponse.getStatus() == 1 ) {
                    Toast.makeText(Handle_Files.this, postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Mod_File_Uploaded> call, Throwable t) {
                //Log.e("Upload error:", t.getMessage());
                Toast.makeText(Handle_Files.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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