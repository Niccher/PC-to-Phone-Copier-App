package com.niccher.p2p_copier_app.viewmodels;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.niccher.p2p_copier_app.model.Mod_File_info;
import com.niccher.p2p_copier_app.utils.Helpers;

import java.util.ArrayList;
import java.util.List;

public class FileViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Mod_File_info>> selectedFiles = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> uploadSuccess = new MutableLiveData<>();

    public FileViewModel(@NonNull Application application) {
        super(application);
        selectedFiles.setValue(new ArrayList<>());
        isLoading.setValue(false);
    }

    public LiveData<List<Mod_File_info>> getSelectedFiles() {
        return selectedFiles;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getUploadSuccess() {
        return uploadSuccess;
    }

    public void addFile(Uri fileUri) {
        List<Mod_File_info> currentFiles = selectedFiles.getValue();
        if (currentFiles != null) {
            try {
                String[] fileInfo = Helpers.getFileName(fileUri, getApplication());
                if (fileInfo != null && fileInfo.length >= 3) {
                    Mod_File_info newFile = new Mod_File_info(
                        fileInfo[0], // file name
                        fileInfo[1], // file size
                        fileInfo[2], // file type
                        fileUri
                    );
                    currentFiles.add(newFile);
                    selectedFiles.setValue(currentFiles);
                }
            } catch (Exception e) {
                errorMessage.setValue("Failed to process selected file: " + e.getMessage());
            }
        }
    }

    public void removeFile(int position) {
        List<Mod_File_info> currentFiles = selectedFiles.getValue();
        if (currentFiles != null && position >= 0 && position < currentFiles.size()) {
            currentFiles.remove(position);
            selectedFiles.setValue(currentFiles);
        }
    }

    public void removeFile(Mod_File_info targetFile) {
        List<Mod_File_info> currentFiles = selectedFiles.getValue();
        if (currentFiles != null && targetFile != null) {
            currentFiles.remove(targetFile);
            selectedFiles.setValue(currentFiles);
        }
    }

    public void clearFiles() {
        selectedFiles.setValue(new ArrayList<>());
    }

    public void uploadFiles() {
        List<Mod_File_info> files = selectedFiles.getValue();
        if (files == null || files.isEmpty()) {
            errorMessage.setValue("No files selected for upload");
            return;
        }

        isLoading.setValue(true);
        Context context = getApplication().getApplicationContext();
        String devId = Helpers.get_prefs_dev("dev_uuid", context);
        String sessId = Helpers.get_prefs_sess("auth_session_uuid", context);
        if (sessId == null || sessId.trim().isEmpty()) {
            sessId = Helpers.get_prefs_sess("auth_auth_code_id", context);
        }

        com.niccher.p2p_copier_app.interfaces.RetrofitInterface api =
            com.niccher.p2p_copier_app.utils.ServiceGenerator.createService(
                com.niccher.p2p_copier_app.interfaces.RetrofitInterface.class, context
            );

        java.util.concurrent.atomic.AtomicInteger pending = new java.util.concurrent.atomic.AtomicInteger(files.size());
        java.util.concurrent.atomic.AtomicBoolean anyFailure = new java.util.concurrent.atomic.AtomicBoolean(false);

        for (Mod_File_info info : files) {
            try {
                Uri uri = info.getF_uri();
                java.io.File fileToUpload = com.niccher.p2p_copier_app.utils.FileUtils.getFile(context, uri);
                if (fileToUpload == null) {
                    String[] fileInfoData = Helpers.getFileName(uri, context);
                    String fileName = (fileInfoData != null && fileInfoData.length > 0) ? fileInfoData[0] : "upload_" + System.currentTimeMillis();
                    fileToUpload = new java.io.File(context.getCacheDir(), fileName);
                    try (java.io.InputStream is = context.getContentResolver().openInputStream(uri);
                         java.io.FileOutputStream fos = new java.io.FileOutputStream(fileToUpload)) {
                        byte[] buffer = new byte[4096];
                        int read;
                        while ((read = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, read);
                        }
                    }
                }
                String mimeType = info.getF_type();
                if (mimeType == null || mimeType.isEmpty()) {
                    mimeType = "application/octet-stream";
                }

                okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(
                    okhttp3.MediaType.parse(mimeType), fileToUpload
                );
                okhttp3.MultipartBody.Part body = okhttp3.MultipartBody.Part.createFormData(
                    "uploaded_file", fileToUpload.getName(), requestFile
                );
                okhttp3.RequestBody devBody = okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, devId != null ? devId : "");
                okhttp3.RequestBody sessBody = okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, sessId != null ? sessId : "general");

                api.filesUpload(devBody, sessBody, body).enqueue(new retrofit2.Callback<com.niccher.p2p_copier_app.model.Mod_File_Uploaded>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.niccher.p2p_copier_app.model.Mod_File_Uploaded> call, retrofit2.Response<com.niccher.p2p_copier_app.model.Mod_File_Uploaded> response) {
                        if (!response.isSuccessful()) {
                            anyFailure.set(true);
                        }
                        if (pending.decrementAndGet() == 0) {
                            isLoading.postValue(false);
                            if (anyFailure.get()) {
                                errorMessage.postValue("One or more files failed to upload");
                            } else {
                                uploadSuccess.postValue(true);
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.niccher.p2p_copier_app.model.Mod_File_Uploaded> call, Throwable t) {
                        anyFailure.set(true);
                        if (pending.decrementAndGet() == 0) {
                            isLoading.postValue(false);
                            errorMessage.postValue("Upload network error: " + t.getMessage());
                        }
                    }
                });
            } catch (Exception e) {
                anyFailure.set(true);
                if (pending.decrementAndGet() == 0) {
                    isLoading.postValue(false);
                    errorMessage.postValue("File preparation error: " + e.getMessage());
                }
            }
        }
    }

    public void setError(String message) {
        errorMessage.setValue(message);
    }
}
