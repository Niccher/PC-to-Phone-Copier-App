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
        isLoading.setValue(true);
        // TODO: Implement actual file upload logic
        // For now, simulate upload success
        new android.os.Handler().postDelayed(() -> {
            isLoading.setValue(false);
            uploadSuccess.setValue(true);
        }, 2000);
    }

    public void setError(String message) {
        errorMessage.setValue(message);
    }
}
