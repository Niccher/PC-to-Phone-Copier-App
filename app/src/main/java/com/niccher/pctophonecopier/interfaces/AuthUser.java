package com.niccher.pctophonecopier.interfaces;

import com.niccher.pctophonecopier.model.Mod_Auth;
import com.niccher.pctophonecopier.model.Mod_Device;
import com.niccher.pctophonecopier.model.Mod_Device_Id;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthUser {

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

}
