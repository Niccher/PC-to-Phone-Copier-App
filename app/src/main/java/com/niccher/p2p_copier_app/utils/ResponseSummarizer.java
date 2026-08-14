package com.niccher.p2p_copier_app.utils;

import com.niccher.p2p_copier_app.model.Mod_List_File_Uploaded;

public class ResponseSummarizer {

    private DataEnvelope data;
    private Mod_List_File_Uploaded[] file_info;

    public static class DataEnvelope {
        private Mod_List_File_Uploaded[] files;

        public Mod_List_File_Uploaded[] getFiles() {
            return files;
        }
    }

    public Mod_List_File_Uploaded[] getSummarizer() {
        if (data != null && data.getFiles() != null) {
            return data.getFiles();
        }
        return file_info != null ? file_info : new Mod_List_File_Uploaded[0];
    }

    public Mod_List_File_Uploaded[] getSummarizerAll() {
        return getSummarizer();
    }
}