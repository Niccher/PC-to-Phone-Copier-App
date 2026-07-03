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

public interface RetrofitInterface {

    /*@FormUrlEncoded
    @POST("register")
    Call<Mod_User_Auth> createRegister(
            @Field("varUsername") String varUsername,
            @Field("varEmail") String varEmail,
            @Field("varPassword") String varPassword
    );;*/

    @FormUrlEncoded
    @POST("register")
    Call<Mod_Auth> createRegister(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("register")
    Call<Mod_Device_Id> createDevice(@FieldMap Map<String, String> fields);

    @Multipart
    @POST("upload")
    Call<Mod_File_Uploaded> filesUpload(
            @Part("varDevId") RequestBody device_id,
            @Part("varSessId") RequestBody sess_id,
            @Part MultipartBody.Part file
    );

    @FormUrlEncoded
    @POST("get_files_uploaded_by_session")
    Call<Mod_List_File_Uploaded> getFilesUploadedbySessDevid0(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("get_files_uploaded_by_session")
    Call<ResponseSummarizer> getFilesUploadedbySessDevid(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("get_files_uploaded_by_session_download")
    Call<ResponseBody> getFilesUploadedbySessDevidDownloaded(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("set_files_to_delete")
    Call<Mod_File_Delete> getFilesUploadedbySessDevidDelete(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("set_files_to_delete")
    Call<Mod_File_Delete> setTextToUpload(@FieldMap Map<String, String> fields);

}
