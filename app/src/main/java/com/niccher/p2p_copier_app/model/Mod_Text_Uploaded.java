package com.niccher.p2p_copier_app.model;

public class Mod_Text_Uploaded {
    private String text_id;
    private String text_uuid;
    private String text_session_id;
    private String text_dev_id;
    private String text_title;
    private String text_content;
    private String text_source;
    private String text_created_at;

    public String getText_id() { return text_id; }
    public String getText_uuid() { return text_uuid; }
    public String getText_session_id() { return text_session_id; }
    public String getText_dev_id() { return text_dev_id; }
    public String getText_title() { return text_title; }
    public String getText_content() { return text_content; }
    public String getText_source() { return text_source; }
    public String getText_created_at() { return text_created_at; }

    public Mod_List_File_Uploaded toFileUploadedModel() {
        String ext = "TEXT";
        String mime = "text/plain";
        if (text_source != null) {
            String srcLower = text_source.toLowerCase();
            if (srcLower.contains("ocr") || srcLower.contains("image")) {
                ext = "OCR";
                mime = "text/ocr";
            } else if (srcLower.contains("qr") || srcLower.contains("scan")) {
                ext = "QR";
                mime = "text/qr";
            }
        }

        Mod_List_File_Uploaded item = new Mod_List_File_Uploaded(
            1, text_uuid, text_session_id, text_dev_id,
            text_title, mime, ext,
            String.valueOf(text_content != null ? text_content.getBytes().length : 0),
            text_created_at
        );
        item.setTextItem(true);
        item.setTextContent(text_content);
        item.setTextSource(text_source);
        return item;
    }
}
