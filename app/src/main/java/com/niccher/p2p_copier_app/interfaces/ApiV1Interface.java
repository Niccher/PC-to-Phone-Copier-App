package com.niccher.p2p_copier_app.interfaces;

import com.niccher.p2p_copier_app.model.api.ApiResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiV1Interface {

    @POST("api/v1/device/register")
    Call<ApiResponse<Map<String, Object>>> registerDevice(@Body Map<String, String> payload);

    @POST("api/v1/device/metrics")
    Call<ApiResponse<Map<String, Object>>> logMetrics(@Body Map<String, String> payload);

    @POST("api/v1/auth/pair")
    Call<ApiResponse<Map<String, String>>> pairAuthCode(@Body Map<String, String> payload);

    @GET("api/v1/device/sessions")
    Call<ApiResponse<Object>> getDeviceSessions();

    @POST("api/v1/auth/reactivate")
    Call<ApiResponse<Map<String, String>>> reactivateSession(@Body Map<String, String> payload);

    @GET("api/v1/files")
    Call<ApiResponse<Object>> getFiles(@Query("session_id") String sessionId);

    @Multipart
    @POST("api/v1/files")
    Call<ApiResponse<Object>> uploadFile(
            @Part("session_id") RequestBody sessionId,
            @Part MultipartBody.Part file
    );

    @DELETE("api/v1/files/{id}")
    Call<ApiResponse<Object>> deleteFile(@Path("id") String fileUuid);

    @GET("api/v1/texts")
    Call<ApiResponse<Object>> getTexts(@Query("session_id") String sessionId);

    @POST("api/v1/texts")
    Call<ApiResponse<Object>> createText(@Body Map<String, String> payload);
}
