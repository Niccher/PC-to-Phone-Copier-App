package com.niccher.p2p_copier_app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.niccher.p2p_copier_app.R;
import com.niccher.p2p_copier_app.interfaces.RetrofitInterface;
import com.niccher.p2p_copier_app.utils.Helpers;
import com.niccher.p2p_copier_app.utils.ResponseSummarizer;
import com.niccher.p2p_copier_app.utils.ServiceGenerator;
import com.niccher.p2p_copier_app.utils.SharedPrefs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_History_Overview extends Fragment {

    private TextView txtCountFiles, txtCountTexts, txtCountQr, txtCountOcr;
    private TextView txtTimeLastSync, txtTimeLastUpload, txtTimeLastDownload;

    private SharedPrefs prefs;

    public Fragment_History_Overview() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_history_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = new SharedPrefs(requireContext());

        txtCountFiles = view.findViewById(R.id.txt_count_files);
        txtCountTexts = view.findViewById(R.id.txt_count_texts);
        txtCountQr = view.findViewById(R.id.txt_count_qr);
        txtCountOcr = view.findViewById(R.id.txt_count_ocr);

        txtTimeLastSync = view.findViewById(R.id.txt_time_last_sync);
        txtTimeLastUpload = view.findViewById(R.id.txt_time_last_upload);
        txtTimeLastDownload = view.findViewById(R.id.txt_time_last_download);

        loadActivityStats();
        fetchFileCountFromBackend();
    }

    private void loadActivityStats() {
        Context context = requireContext();

        int textCount = prefs.getInt("stat_count_texts", 12);
        int qrCount = prefs.getInt("stat_count_qr", 6);
        int ocrCount = prefs.getInt("stat_count_ocr", 4);
        int fileCount = prefs.getInt("stat_count_files", 3);

        txtCountFiles.setText(String.valueOf(fileCount));
        txtCountTexts.setText(String.valueOf(textCount));
        txtCountQr.setText(String.valueOf(qrCount));
        txtCountOcr.setText(String.valueOf(ocrCount));

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String lastSync = prefs.getString("stat_last_sync", now);
        String lastUpload = prefs.getString("stat_last_upload", Helpers.get_prefs_sess("auth_time", context));
        String lastDownload = prefs.getString("stat_last_download", "Not downloaded yet");

        if (TextUtils.isEmpty(lastUpload)) lastUpload = now;

        txtTimeLastSync.setText(lastSync);
        txtTimeLastUpload.setText(lastUpload);
        txtTimeLastDownload.setText(lastDownload);
    }

    private void fetchFileCountFromBackend() {
        try {
            RetrofitInterface api = ServiceGenerator.createService(RetrofitInterface.class, requireContext());
            Map<String, String> parameters = new HashMap<>();
            parameters.put("var_dev_uuid", Helpers.get_prefs_dev("dev_uuid", requireContext()));
            parameters.put("var_auth_code_id", Helpers.get_prefs_sess("auth_auth_code_id", requireContext()));

            api.getFilesUploadedbySessDevid(parameters).enqueue(new Callback<ResponseSummarizer>() {
                @Override
                public void onResponse(Call<ResponseSummarizer> call, Response<ResponseSummarizer> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getSummarizer() != null) {
                        int count = response.body().getSummarizer().length;
                        txtCountFiles.setText(String.valueOf(count));
                        prefs.saveInt("stat_count_files", count);
                    }
                }

                @Override
                public void onFailure(Call<ResponseSummarizer> call, Throwable t) {
                    // Retain cached count
                }
            });
        } catch (Exception ignored) {}
    }
}
