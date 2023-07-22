package com.niccher.pctophonecopier.model;

public class Mod_File_Uploaded {
    int status;
    String time, message;

    public Mod_File_Uploaded(int status, String time, String message) {
        this.status = status;
        this.time = time;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
