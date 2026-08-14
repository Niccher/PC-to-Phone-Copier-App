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
import com.niccher.p2p_copier_app.model.api.ApiResponse;
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

    private String formatItemsCount(int count) {
        return count + " item" + (count == 1 ? "" : "s");
    }

    private void loadActivityStats() {
        Context context = requireContext();

        int textCount = prefs.getInt("stat_count_texts", 0);
        int qrCount = prefs.getInt("stat_count_qr", 0);
        int ocrCount = prefs.getInt("stat_count_ocr", 0);
        int fileCount = prefs.getInt("stat_count_files", 0);

        txtCountFiles.setText(formatItemsCount(fileCount));
        txtCountTexts.setText(formatItemsCount(textCount));
        txtCountQr.setText(formatItemsCount(qrCount));
        txtCountOcr.setText(formatItemsCount(ocrCount));

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

            api.getAnalyticsSummary(parameters).enqueue(new Callback<ApiResponse<com.niccher.p2p_copier_app.model.Mod_Analytics_Summary>>() {
                @Override
                public void onResponse(Call<ApiResponse<com.niccher.p2p_copier_app.model.Mod_Analytics_Summary>> call, Response<ApiResponse<com.niccher.p2p_copier_app.model.Mod_Analytics_Summary>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        com.niccher.p2p_copier_app.model.Mod_Analytics_Summary data = response.body().getData();

                        txtCountFiles.setText(formatItemsCount(data.getTotalFiles()));
                        txtCountTexts.setText(formatItemsCount(data.getTotalTexts()));
                        txtCountQr.setText(formatItemsCount(data.getTotalQrScans()));
                        txtCountOcr.setText(formatItemsCount(data.getTotalOcrExtractions()));

                        txtTimeLastSync.setText(data.getLastSync());
                        txtTimeLastUpload.setText(data.getLastUpload());
                        txtTimeLastDownload.setText(data.getLastDownload());

                        prefs.saveInt("stat_count_files", data.getTotalFiles());
                        prefs.saveInt("stat_count_texts", data.getTotalTexts());
                        prefs.saveInt("stat_count_qr", data.getTotalQrScans());
                        prefs.saveInt("stat_count_ocr", data.getTotalOcrExtractions());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<com.niccher.p2p_copier_app.model.Mod_Analytics_Summary>> call, Throwable t) {
                    // Retain cached count
                }
            });
        } catch (Exception ignored) {}
    }
}
