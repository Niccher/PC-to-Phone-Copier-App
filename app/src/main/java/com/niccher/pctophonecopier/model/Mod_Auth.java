package com.niccher.pctophonecopier.model;

public class Mod_Auth {
    String auth_status, auth_type, auth_auth_code, auth_message, auth_auth_code_id, auth_time;

    public Mod_Auth(String auth_status, String auth_type, String auth_auth_code, String auth_message, String auth_auth_code_id, String auth_time) {
        this.auth_status = auth_status;
        this.auth_type = auth_type;
        this.auth_auth_code = auth_auth_code;
        this.auth_message = auth_message;
        this.auth_auth_code_id = auth_auth_code_id;
        this.auth_time = auth_time;
    }

    public String getAuth_status() {
        return auth_status;
    }

    public void setAuth_status(String auth_status) {
        this.auth_status = auth_status;
    }

    public String getAuth_type() {
        return auth_type;
    }

    public void setAuth_type(String auth_type) {
        this.auth_type = auth_type;
    }

    public String getAuth_auth_code() {
        return auth_auth_code;
    }

    public void setAuth_auth_code(String auth_auth_code) {
        this.auth_auth_code = auth_auth_code;
    }

    public String getAuth_message() {
        return auth_message;
    }

    public void setAuth_message(String auth_message) {
        this.auth_message = auth_message;
    }

    public String getAuth_auth_code_id() {
        return auth_auth_code_id;
    }

    public void setAuth_auth_code_id(String auth_auth_code_id) {
        this.auth_auth_code_id = auth_auth_code_id;
    }

    public String getAuth_time() {
        return auth_time;
    }

    public void setAuth_time(String auth_time) {
        this.auth_time = auth_time;
    }
}
