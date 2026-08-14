package com.niccher.p2p_copier_app.model.api;

import com.niccher.p2p_copier_app.model.Mod_List_File_Uploaded;
import java.util.List;

public class UploadedEnvelope {
    private List<Mod_List_File_Uploaded> items;
    private int count;

    public List<Mod_List_File_Uploaded> getItems() {
        return items;
    }

    public int getCount() {
        return count;
    }
}
