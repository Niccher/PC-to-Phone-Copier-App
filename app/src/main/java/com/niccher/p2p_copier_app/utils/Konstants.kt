package com.niccher.p2p_copier_app.utils

class Konstants {
    @JvmField var str_base_url: String = "https://p2p.chegecache.co.ke"
    @JvmField var str_device_action: String = "$str_base_url/device/"
    @JvmField var str_auth_action: String = "$str_base_url/auth/"
    @JvmField var str_file_list_uploaded: String = "$str_base_url/home/phone/"
    @JvmField var str_file_upload_action: String = "$str_base_url/home/phone/"

    @JvmField var TAGGED: String = "P2P_Copier"

    @JvmField var Splash_Time: Int = 1500

    @JvmField var shared_pref_auth: String = "s_p_auth"
    @JvmField var shared_pref_device: String = "s_p_device"
}
