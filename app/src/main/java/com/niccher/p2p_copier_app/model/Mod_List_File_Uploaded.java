package com.niccher.p2p_copier_app.model;

public class Mod_List_File_Uploaded {
    int status;
    String up_file_uuid, up_file_session_id, up_file_dev_id, up_file_Name, up_file_Type, up_file_Extension, up_file_Size, up_file_Created_at;

    public Mod_List_File_Uploaded(int status, String up_file_uuid, String up_file_session_id, String up_file_dev_id, String up_file_Name, String up_file_Type, String up_file_Extension, String up_file_Size, String up_file_Created_at) {
        this.status = status;
        this.up_file_uuid = up_file_uuid;
        this.up_file_session_id = up_file_session_id;
        this.up_file_dev_id = up_file_dev_id;
        this.up_file_Name = up_file_Name;
        this.up_file_Type = up_file_Type;
        this.up_file_Extension = up_file_Extension;
        this.up_file_Size = up_file_Size;
        this.up_file_Created_at = up_file_Created_at;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUp_file_uuid() {
        return up_file_uuid;
    }

    public void setUp_file_uuid(String up_file_uuid) {
        this.up_file_uuid = up_file_uuid;
    }

    public String getUp_file_session_id() {
        return up_file_session_id;
    }

    public void setUp_file_session_id(String up_file_session_id) {
        this.up_file_session_id = up_file_session_id;
    }

    public String getUp_file_dev_id() {
        return up_file_dev_id;
    }

    public void setUp_file_dev_id(String up_file_dev_id) {
        this.up_file_dev_id = up_file_dev_id;
    }

    public String getUp_file_Name() {
        return up_file_Name;
    }

    public void setUp_file_Name(String up_file_Name) {
        this.up_file_Name = up_file_Name;
    }

    public String getUp_file_Type() {
        return up_file_Type;
    }

    public void setUp_file_Type(String up_file_Type) {
        this.up_file_Type = up_file_Type;
    }

    public String getUp_file_Extension() {
        return up_file_Extension;
    }

    public void setUp_file_Extension(String up_file_Extension) {
        this.up_file_Extension = up_file_Extension;
    }

    public String getUp_file_Size() {
        return up_file_Size;
    }

    public void setUp_file_Size(String up_file_Size) {
        this.up_file_Size = up_file_Size;
    }

    public String getUp_file_Created_at() {
        return up_file_Created_at;
    }

    public void setUp_file_Created_at(String up_file_Created_at) {
        this.up_file_Created_at = up_file_Created_at;
    }
}
