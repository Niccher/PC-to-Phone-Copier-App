package com.niccher.p2p_copier_app.model;

import com.google.gson.annotations.SerializedName;

public class Mod_Analytics_Summary {

    @SerializedName("total_files")
    private int totalFiles;

    @SerializedName("total_texts")
    private int totalTexts;

    @SerializedName("total_qr_scans")
    private int totalQrScans;

    @SerializedName("total_ocr_extractions")
    private int totalOcrExtractions;

    @SerializedName("last_sync")
    private String lastSync;

    @SerializedName("last_upload")
    private String lastUpload;

    @SerializedName("last_download")
    private String lastDownload;

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getTotalTexts() {
        return totalTexts;
    }

    public int getTotalQrScans() {
        return totalQrScans;
    }

    public int getTotalOcrExtractions() {
        return totalOcrExtractions;
    }

    public String getLastSync() {
        return lastSync;
    }

    public String getLastUpload() {
        return lastUpload;
    }

    public String getLastDownload() {
        return lastDownload;
    }
}
