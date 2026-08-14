package com.niccher.p2p_copier_app.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.niccher.p2p_copier_app.interfaces.RetrofitInterface;
import com.niccher.p2p_copier_app.utils.Helpers;
import com.niccher.p2p_copier_app.utils.ServiceGenerator;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public void uploadText() {
        uploadTextWithDetails("Android Text", null);
    }

    public void uploadTextWithDetails(String source, String title) {
        String text = textContent.getValue();
        if (text == null || text.trim().isEmpty()) {
            errorMessage.setValue("Please enter some text to upload");
            return;
        }

        isLoading.setValue(true);

        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class, getApplication());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("var_dev_uuid", Helpers.get_prefs_dev("dev_uuid", getApplication()));
        parameters.put("var_auth_code_id", Helpers.get_prefs_sess("auth_auth_code_id", getApplication()));
        parameters.put("var_text_sess_id", Helpers.get_prefs_sess("auth_auth_code_id", getApplication()));
        parameters.put("session_id", Helpers.get_prefs_sess("auth_auth_code_id", getApplication()));
        parameters.put("var_text_content", text.trim());
        parameters.put("text_content", text.trim());
        parameters.put("var_text_source", source != null ? source : "Android Text");
        parameters.put("text_source", source != null ? source : "Android Text");
        if (title != null && !title.trim().isEmpty()) {
            parameters.put("var_text_title", title.trim());
            parameters.put("text_title", title.trim());
        }

        Call<ResponseBody> call = retrofitInterface.setTextToUpload(parameters);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    uploadSuccess.setValue(true);
                } else {
                    errorMessage.setValue("Failed to upload text. Server response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Upload error: " + t.getMessage());
            }
        });
    }

    public void clearText() {
        textContent.setValue("");
    }

    public void setError(String message) {
        errorMessage.setValue(message);
    }
}
