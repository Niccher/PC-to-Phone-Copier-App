package com.niccher.pctophonecopier.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.adapters.Adapter_Uploaded_Files;
import com.niccher.pctophonecopier.interfaces.RetrofitInterface;
import com.niccher.pctophonecopier.model.Mod_List_File_Uploaded;
import com.niccher.pctophonecopier.utils.Helpers;
import com.niccher.pctophonecopier.utils.Konstants;
import com.niccher.pctophonecopier.utils.ResponseSummarizer;
import com.niccher.pctophonecopier.utils.ServiceGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Fragment_History_Files extends Fragment {

    Konstants kon;
    Gson gson = null;
    Helpers helpers;

    RecyclerView rcy_files;

    RetrofitInterface retrofitInterface;

    ResponseSummarizer responseSummarizer;
    ArrayList<Mod_List_File_Uploaded> summaryFileList;

    Adapter_Uploaded_Files adapterUploadedFiles;

    int perm_storage_write = 102;
    int perm_storage_read = 104;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_history_files, container, false);

        rcy_files = view.findViewById(R.id.recycler_uploaded_files);

        rcy_files.setHasFixedSize(true);
        rcy_files.setLayoutManager(new LinearLayoutManager(getActivity()));

        helpers = new Helpers();
        kon = new Konstants();
        gson = new GsonBuilder()
                .setLenient()
                .create();

        filesListing();

        return view;
    }

    private void filesListing() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(kon.str_file_list_uploaded)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(ServiceGenerator.getUnsafeOkHttpClient())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("var_dev_uuid", Helpers.get_prefs_dev("dev_uuid", getActivity()));
        parameters.put("var_auth_code_id", Helpers.get_prefs_sess("auth_auth_code_id", getActivity()));

        Call<ResponseSummarizer> call = retrofitInterface.getFilesUploadedbySessDevid(parameters);

        call.enqueue(new Callback<ResponseSummarizer>() {
            @Override
            public void onResponse(Call<ResponseSummarizer> call, Response<ResponseSummarizer> response) {
                ResponseSummarizer postResponse = response.body();
                if (response.isSuccessful() && response.body() != null) {
                    ResponseSummarizer summaryResponse = response.body();
                    responseSummarizer = summaryResponse;
                    parseFiles(responseSummarizer);
                }
            }

            @Override
            public void onFailure(Call<ResponseSummarizer> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage() + "\nUnknown error occurred, please try again", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void parseFiles(ResponseSummarizer responseSummarizer) {
        checkPermissions();
        Fragment_History_Files.this.summaryFileList = new ArrayList<Mod_List_File_Uploaded>(Arrays.asList(responseSummarizer.getSummarizer()));
        for (Mod_List_File_Uploaded filelist : Fragment_History_Files.this.summaryFileList) {
            adapterUploadedFiles = new Adapter_Uploaded_Files(summaryFileList, getActivity());
            rcy_files.setAdapter(adapterUploadedFiles);
            adapterUploadedFiles.notifyDataSetChanged();
        }
    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, perm_storage_write);
        }
    }
}
