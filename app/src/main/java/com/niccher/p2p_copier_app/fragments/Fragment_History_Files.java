package com.niccher.p2p_copier_app.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.niccher.p2p_copier_app.R;
import com.niccher.p2p_copier_app.adapters.Adapter_Uploaded_Files;
import com.niccher.p2p_copier_app.interfaces.RetrofitInterface;
import com.niccher.p2p_copier_app.model.Mod_File_Delete;
import com.niccher.p2p_copier_app.model.Mod_List_File_Uploaded;
import com.niccher.p2p_copier_app.utils.Helpers;
import com.niccher.p2p_copier_app.utils.Konstants;
import com.niccher.p2p_copier_app.utils.ResponseSummarizer;
import com.niccher.p2p_copier_app.utils.ServiceGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Fragment_History_Files extends Fragment implements Adapter_Uploaded_Files.OnSelectionChangedListener {

    Konstants kon;
    Gson gson;
    Helpers helpers;

    RecyclerView rcy_files_current;
    CircularProgressIndicator progressSpinner;
    TextView txtEmptyState;
    View layoutEmptyState;
    LinearLayout layoutBatchActions;
    ExtendedFloatingActionButton fabBatchDownload, fabBatchDelete;

    RetrofitInterface retrofitInterface;
    ResponseSummarizer responseSummarizer;
    ArrayList<Mod_List_File_Uploaded> summaryFileList;
    Adapter_Uploaded_Files adapterUploadedFiles;

    int perm_storage_write = 102;
    private int batchDownloadIndex = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_history_files, container, false);

        rcy_files_current    = view.findViewById(R.id.recycler_uploaded_files);
        progressSpinner      = view.findViewById(R.id.spinner_loading);
        txtEmptyState        = view.findViewById(R.id.txt_empty_state);
        layoutEmptyState     = view.findViewById(R.id.layout_empty_state);
        layoutBatchActions   = view.findViewById(R.id.layout_batch_actions);
        fabBatchDownload     = view.findViewById(R.id.fab_batch_download);
        fabBatchDelete       = view.findViewById(R.id.fab_batch_delete);

        rcy_files_current.setHasFixedSize(false);
        rcy_files_current.setLayoutManager(new LinearLayoutManager(getActivity()));

        helpers = new Helpers();
        kon     = new Konstants();
        gson    = new GsonBuilder().setLenient().create();

        layoutBatchActions.setVisibility(View.GONE);

        fabBatchDownload.setOnClickListener(v -> startBatchDownload());
        fabBatchDelete.setOnClickListener(v -> startBatchDelete());

        filesListing();
        return view;
    }

    private void filesListing() {
        if (getActivity() == null) return;
        progressSpinner.setVisibility(View.VISIBLE);
        rcy_files_current.setVisibility(View.GONE);
        txtEmptyState.setVisibility(View.GONE);

        retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class, getActivity());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("var_dev_uuid", Helpers.get_prefs_dev("dev_uuid", getActivity()));
        parameters.put("var_auth_code_id", Helpers.get_prefs_sess("auth_auth_code_id", getActivity()));

        summaryFileList = new ArrayList<>();

        retrofitInterface.getUploadedItems(parameters).enqueue(new Callback<com.niccher.p2p_copier_app.model.api.ApiResponse<com.niccher.p2p_copier_app.model.api.UploadedEnvelope>>() {
            @Override
            public void onResponse(Call<com.niccher.p2p_copier_app.model.api.ApiResponse<com.niccher.p2p_copier_app.model.api.UploadedEnvelope>> call, Response<com.niccher.p2p_copier_app.model.api.ApiResponse<com.niccher.p2p_copier_app.model.api.UploadedEnvelope>> response) {
                progressSpinner.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    com.niccher.p2p_copier_app.model.api.UploadedEnvelope envelope = response.body().getData();
                    if (envelope.getItems() != null) {
                        summaryFileList.addAll(envelope.getItems());
                    }
                }
                renderUnifiedList();
            }

            @Override
            public void onFailure(Call<com.niccher.p2p_copier_app.model.api.ApiResponse<com.niccher.p2p_copier_app.model.api.UploadedEnvelope>> call, Throwable t) {
                progressSpinner.setVisibility(View.GONE);
                renderUnifiedList();
            }
        });
    }

    private void renderUnifiedList() {
        if (summaryFileList.isEmpty()) {
            showEmpty("No files or text items uploaded yet for this session.");
            return;
        }

        java.util.Collections.sort(summaryFileList, (a, b) -> {
            String dateA = a.getUp_file_Created_at() != null ? a.getUp_file_Created_at() : "";
            String dateB = b.getUp_file_Created_at() != null ? b.getUp_file_Created_at() : "";
            return dateB.compareTo(dateA);
        });

        if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
        rcy_files_current.setVisibility(View.VISIBLE);

        adapterUploadedFiles = new Adapter_Uploaded_Files(summaryFileList, getActivity(), this);
        rcy_files_current.setAdapter(adapterUploadedFiles);
    }

    private void showEmpty(String msg) {
        rcy_files_current.setVisibility(View.GONE);
        if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.VISIBLE);
        txtEmptyState.setText(msg);
        if (getActivity() != null) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    // --- Batch Actions ---

    @Override
    public void onSelectionChanged(int count) {
        if (count > 0) {
            fabBatchDownload.setText("Download (" + count + ")");
            fabBatchDelete.setText("Delete (" + count + ")");
            layoutBatchActions.setVisibility(View.VISIBLE);
        } else {
            layoutBatchActions.setVisibility(View.GONE);
        }
    }

    private void startBatchDownload() {
        if (adapterUploadedFiles == null) return;
        List<Mod_List_File_Uploaded> selected = adapterUploadedFiles.getSelectedItems();
        if (selected.isEmpty()) return;

        checkPermissions();
        batchDownloadIndex = 0;
        fabBatchDownload.setEnabled(false);
        fabBatchDelete.setEnabled(false);
        fabBatchDownload.setText("Downloading…");
        downloadNext(selected);
    }

    private void downloadNext(List<Mod_List_File_Uploaded> selected) {
        if (batchDownloadIndex >= selected.size()) {
            Toast.makeText(getActivity(), "All downloads complete!", Toast.LENGTH_SHORT).show();
            fabBatchDownload.setEnabled(true);
            fabBatchDelete.setEnabled(true);
            fabBatchDownload.setText("Download (" + selected.size() + ")");
            adapterUploadedFiles.clearSelection();
            return;
        }

        Mod_List_File_Uploaded item = selected.get(batchDownloadIndex);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(kon.str_file_upload_action)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(ServiceGenerator.getUnsafeOkHttpClient())
                .build();

        RetrofitInterface iface = retrofit.create(RetrofitInterface.class);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("var_file_id", item.getUp_file_uuid());
        parameters.put("var_dev_id", Helpers.get_prefs_dev("dev_uuid", getActivity()));
        parameters.put("var_sess_id", Helpers.get_prefs_sess("auth_auth_code_id", getActivity()));

        iface.getFilesUploadedbySessDevidDownloaded(parameters).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        File dest = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), item.getUp_file_Name());
                        InputStream in = response.body().byteStream();
                        OutputStream out = new FileOutputStream(dest);
                        byte[] buf = new byte[4096];
                        int read;
                        while ((read = in.read(buf)) != -1) out.write(buf, 0, read);
                        out.flush();
                        in.close();
                        out.close();
                        Toast.makeText(getActivity(), "✓ " + item.getUp_file_Name(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), "✗ Failed: " + item.getUp_file_Name(), Toast.LENGTH_SHORT).show();
                    }
                }
                batchDownloadIndex++;
                downloadNext(selected);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "✗ Error: " + item.getUp_file_Name(), Toast.LENGTH_SHORT).show();
                batchDownloadIndex++;
                downloadNext(selected);
            }
        });
    }

    private void startBatchDelete() {
        if (adapterUploadedFiles == null) return;
        List<Mod_List_File_Uploaded> selected = adapterUploadedFiles.getSelectedItems();
        if (selected.isEmpty()) return;

        new AlertDialog.Builder(getActivity())
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete " + selected.size() + " files? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    fabBatchDownload.setEnabled(false);
                    fabBatchDelete.setEnabled(false);
                    fabBatchDelete.setText("Deleting…");

                    StringBuilder ids = new StringBuilder();
                    for (int i = 0; i < selected.size(); i++) {
                        if (i > 0) ids.append(",");
                        ids.append(selected.get(i).getUp_file_uuid());
                    }

                    try {
                        RetrofitInterface iface = ServiceGenerator.createService(RetrofitInterface.class, getActivity());
                        Map<String, String> parameters = new HashMap<>();
                        parameters.put("var_file_ids", ids.toString());
                        parameters.put("var_dev_uuid", Helpers.get_prefs_dev("dev_uuid", getActivity()));
                        parameters.put("var_auth_code_id", Helpers.get_prefs_sess("auth_auth_code_id", getActivity()));

                        iface.batchDeleteFiles(parameters).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), "✓ Batch deletion complete!", Toast.LENGTH_SHORT).show();
                                    fabBatchDownload.setEnabled(true);
                                    fabBatchDelete.setEnabled(true);
                                    adapterUploadedFiles.clearSelection();
                                    filesListing();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                if (getActivity() != null) {
                                    Toast.makeText(getActivity(), "✗ Delete error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    fabBatchDownload.setEnabled(true);
                                    fabBatchDelete.setEnabled(true);
                                }
                            }
                        });
                    } catch (Exception e) {
                        fabBatchDownload.setEnabled(true);
                        fabBatchDelete.setEnabled(true);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, perm_storage_write);
        }
    }
}
