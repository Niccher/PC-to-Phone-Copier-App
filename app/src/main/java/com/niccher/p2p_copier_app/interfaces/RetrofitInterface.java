package com.niccher.p2p_copier_app.interfaces;

import com.niccher.p2p_copier_app.model.Mod_Auth;
import com.niccher.p2p_copier_app.model.Mod_Device_Id;
import com.niccher.p2p_copier_app.model.Mod_File_Uploaded;
import com.niccher.p2p_copier_app.model.Mod_File_Delete;
import com.niccher.p2p_copier_app.model.Mod_List_File_Uploaded;
import com.niccher.p2p_copier_app.utils.ResponseSummarizer;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import com.niccher.p2p_copier_app.model.api.ApiResponse;

public interface RetrofitInterface {

    @FormUrlEncoded
    @POST("api/v1/auth/pair")
    Call<ApiResponse<Mod_Auth>> createRegister(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("api/v1/device/register")
    Call<Mod_Device_Id> createDevice(@FieldMap Map<String, String> fields);

    @Multipart
    @POST("api/v1/files")
    Call<Mod_File_Uploaded> filesUpload(
            @Part("varDevId") RequestBody device_id,
            @Part("varSessId") RequestBody sess_id,
            @Part MultipartBody.Part file
    );

    @FormUrlEncoded
    @POST("api/v1/files/list")
    Call<Mod_List_File_Uploaded> getFilesUploadedbySessDevid0(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("api/v1/files/list")
    Call<ResponseSummarizer> getFilesUploadedbySessDevid(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("api/v1/files/download")
    Call<ResponseBody> getFilesUploadedbySessDevidDownloaded(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("api/v1/files/delete")
    Call<Mod_File_Delete> getFilesUploadedbySessDevidDelete(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("api/v1/texts")
    Call<ResponseBody> setTextToUpload(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("api/v1/texts")
    Call<ResponseBody> getTextsUploadedbySessDevid(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("api/v1/device/metrics")
    Call<ResponseBody> logDeviceMetrics(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("api/v1/analytics/summary")
    Call<ApiResponse<com.niccher.p2p_copier_app.model.Mod_Analytics_Summary>> getAnalyticsSummary(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("api/v1/auth/session-status")
    Call<ApiResponse<com.google.gson.JsonObject>> checkSessionStatus(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("api/v1/telemetry/event")
    Call<ResponseBody> sendTelemetryEvent(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("api/v1/files/batch-delete")
    Call<ResponseBody> batchDeleteFiles(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("api/v1/texts/list")
    Call<ApiResponse<com.niccher.p2p_copier_app.model.api.TextDataEnvelope>> getTextsUploaded(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("api/v1/uploaded")
    Call<ApiResponse<com.niccher.p2p_copier_app.model.api.UploadedEnvelope>> getUploadedItems(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("api/v1/texts/delete")
    Call<ResponseBody> deleteTextUploaded(@FieldMap Map<String, String> fields);

}
