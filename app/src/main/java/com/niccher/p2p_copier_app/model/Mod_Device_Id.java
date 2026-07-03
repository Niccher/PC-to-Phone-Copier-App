package com.niccher.p2p_copier_app.model;

public class Mod_Device_Id {
    public String dev_uuid, dev_status, dev_time, dev_message;

    public Mod_Device_Id(String dev_uuid, String dev_status, String dev_time, String dev_message) {
        this.dev_uuid = dev_uuid;
        this.dev_status = dev_status;
        this.dev_time = dev_time;
        this.dev_message = dev_message;
    }

    public String getDev_uuid() {
        return dev_uuid;
    }

    public void setDev_uuid(String dev_uuid) {
        this.dev_uuid = dev_uuid;
    }

    public String getDev_status() {
        return dev_status;
    }

    public void setDev_status(String dev_status) {
        this.dev_status = dev_status;
    }

    public String getDev_time() {
        return dev_time;
    }

    public void setDev_time(String dev_time) {
        this.dev_time = dev_time;
    }

    public String getDev_message() {
        return dev_message;
    }

    public void setDev_message(String dev_message) {
        this.dev_message = dev_message;
    }
}
