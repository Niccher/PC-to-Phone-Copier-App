package com.niccher.p2p_copier_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class TextViewModel extends AndroidViewModel {

    private final MutableLiveData<String> textContent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> uploadSuccess = new MutableLiveData<>();

    public TextViewModel(@NonNull Application application) {
        super(application);
        textContent.setValue("");
        isLoading.setValue(false);
    }

    public LiveData<String> getTextContent() {
        return textContent;
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

    public void setTextContent(String text) {
        textContent.setValue(text);
    }

    public void pasteFromClipboard() {
        // TODO: Implement clipboard access
        // This would require Activity context, so we'll handle it in the activity
    }

    public void uploadText() {
        String text = textContent.getValue();
        if (text == null || text.trim().isEmpty()) {
            errorMessage.setValue("Please enter some text to upload");
            return;
        }

        isLoading.setValue(true);
        // TODO: Implement actual text upload logic
        // For now, simulate upload success
        new android.os.Handler().postDelayed(() -> {
            isLoading.setValue(false);
            uploadSuccess.setValue(true);
        }, 1500);
    }

    public void clearText() {
        textContent.setValue("");
    }

    public void setError(String message) {
        errorMessage.setValue(message);
    }
}
