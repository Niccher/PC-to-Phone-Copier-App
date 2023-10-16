package com.niccher.pctophonecopier.utils;

import com.niccher.pctophonecopier.model.Mod_List_File_Uploaded;

public class ResponseSummarizer {

    private Mod_List_File_Uploaded[] file_info;
    private Mod_List_File_Uploaded[] file_info_all;

    public Mod_List_File_Uploaded[] getSummarizer(){
        return file_info;
    }

    public Mod_List_File_Uploaded[] getSummarizerAll(){
        return file_info_all;
    }
}