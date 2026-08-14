package com.niccher.p2p_copier_app.model;

public class Mod_List_File_Uploaded {
    int status;
    String up_file_uuid, up_file_session_id, up_file_dev_id, up_file_Name, up_file_Orig_Name, up_file_Sys_Name, up_file_Type, up_file_Extension, up_file_Size, up_file_Created_at;

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

    public String getUp_file_Orig_Name() {
        return up_file_Orig_Name;
    }

    public void setUp_file_Orig_Name(String up_file_Orig_Name) {
        this.up_file_Orig_Name = up_file_Orig_Name;
    }

    public String getUp_file_Sys_Name() {
        return up_file_Sys_Name;
    }

    public void setUp_file_Sys_Name(String up_file_Sys_Name) {
        this.up_file_Sys_Name = up_file_Sys_Name;
    }

    @com.google.gson.annotations.SerializedName(value = "is_text", alternate = {"isTextItem"})
    int is_text = 0;

    @com.google.gson.annotations.SerializedName(value = "text_content", alternate = {"textContent"})
    String textContent;

    @com.google.gson.annotations.SerializedName(value = "text_source", alternate = {"textSource"})
    String textSource;

    public boolean isTextItem() {
        if (is_text == 1) return true;
        if (up_file_Extension != null) {
            String ext = up_file_Extension.trim().toUpperCase();
            if (ext.equals("TEXT") || ext.equals("OCR") || ext.equals("QR")) {
                return true;
            }
        }
        if (up_file_Type != null && up_file_Type.startsWith("text/")) {
            return true;
        }
        return false;
    }

    public void setTextItem(boolean textItem) {
        this.is_text = textItem ? 1 : 0;
    }

    public String getTextContent() {
        if (textContent != null && !textContent.trim().isEmpty()) {
            return textContent;
        }
        return up_file_Name != null ? up_file_Name : "";
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getTextSource() {
        if (textSource != null && !textSource.trim().isEmpty()) {
            return textSource;
        }
        return "Text Item";
    }

    public void setTextSource(String textSource) {
        this.textSource = textSource;
    }

    public String getDisplayName() {
        if (isTextItem() && getTextContent() != null && !getTextContent().trim().isEmpty()) {
            String clean = getTextContent().replaceAll("\\s+", " ").trim();
            if (clean.length() > 45) {
                return clean.substring(0, 45) + "...";
            }
            return clean;
        }
        if (up_file_Orig_Name != null && !up_file_Orig_Name.trim().isEmpty()) {
            return up_file_Orig_Name;
        }
        return up_file_Name != null ? up_file_Name : "Unknown";
    }

    public String getUp_file_Type() {
        return up_file_Type;
    }

    public void setUp_file_Type(String up_file_Type) {
        this.up_file_Type = up_file_Type;
    }

    public String getUp_file_Extension() {
        if (up_file_Extension != null && !up_file_Extension.trim().isEmpty()) {
            return up_file_Extension;
        }
        String name = getDisplayName();
        if (name.contains(".")) {
            return name.substring(name.lastIndexOf(".") + 1);
        }
        return "";
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
