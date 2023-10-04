package com.niccher.pctophonecopier.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.niccher.pctophonecopier.HomePage;
import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.activities.AuthSession;
import com.niccher.pctophonecopier.interfaces.RetrofitInterface;
import com.niccher.pctophonecopier.model.Mod_Auth;
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

    TextView tv_files;

    RetrofitInterface retrofitInterface;

    ResponseSummarizer responseSummarizer;
    ArrayList<Mod_List_File_Uploaded> summaryFileList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_history_files,container,false);

        tv_files = view.findViewById(R.id.file_listing);

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

    private void parseFiles(ResponseSummarizer responseSummarizer){
        Fragment_History_Files.this.summaryFileList = new ArrayList<Mod_List_File_Uploaded>(Arrays.asList(responseSummarizer.getSummarizer()));
        String flist = "";

        for (Mod_List_File_Uploaded filelist : Fragment_History_Files.this.summaryFileList) {
            flist = filelist.getUp_file_Name() +
                    "\nSize " + helpers.humanReadableByteCountBin(Long.parseLong(filelist.getUp_file_Size())) +
                    "\nType "+ filelist.getUp_file_Type() +
                    "\nExtension "+ filelist.getUp_file_Extension() +
                    "\nCreated "+ filelist.getUp_file_Created_at() + "\n\n\n";
            tv_files.append(flist);
//            Log.e(kon.TAGGED, "parseFiles: " + flist);
        }
    }
}
