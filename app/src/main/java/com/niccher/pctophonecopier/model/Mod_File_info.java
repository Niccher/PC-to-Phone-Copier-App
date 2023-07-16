package com.niccher.pctophonecopier.model;

public class Mod_File_info {
    String f_name, f_type, f_size;

    public Mod_File_info(String f_name, String f_type, String f_size) {
        this.f_name = f_name;
        this.f_type = f_type;
        this.f_size = f_size;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getF_type() {
        return f_type;
    }

    public void setF_type(String f_type) {
        this.f_type = f_type;
    }

    public String getF_size() {
        return f_size;
    }

    public void setF_size(String f_size) {
        this.f_size = f_size;
    }
}
