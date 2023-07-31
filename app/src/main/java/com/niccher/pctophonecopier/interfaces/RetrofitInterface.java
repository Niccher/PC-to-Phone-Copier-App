package com.niccher.pctophonecopier.interfaces;

import com.niccher.pctophonecopier.model.Mod_Auth;
import com.niccher.pctophonecopier.model.Mod_Device;
import com.niccher.pctophonecopier.model.Mod_Device_Id;
import com.niccher.pctophonecopier.model.Mod_File_Uploaded;

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
    /*@Multipart
    @POST("upload")
    Call<ResponseBody> filesUpload(
            @Part("varDevId") RequestBody print_id,
            @Part MultipartBody.Part file
    );*/

}
