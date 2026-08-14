package com.niccher.p2p_copier_app.model.api;

import java.util.List;

public class TextDataEnvelope {
    private List<com.niccher.p2p_copier_app.model.Mod_Text_Uploaded> texts;
    private int count;

    public List<com.niccher.p2p_copier_app.model.Mod_Text_Uploaded> getTexts() {
        return texts;
    }

    public int getCount() {
        return count;
    }
}
