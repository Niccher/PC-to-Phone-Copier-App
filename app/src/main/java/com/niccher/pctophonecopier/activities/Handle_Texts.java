package com.niccher.pctophonecopier.activities;

import static android.content.ClipDescription.MIMETYPE_TEXT_HTML;
import static android.content.ClipDescription.MIMETYPE_TEXT_INTENT;
import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.content.ClipDescription.MIMETYPE_TEXT_URILIST;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.niccher.pctophonecopier.R;

public class Handle_Texts extends AppCompatActivity {

    EditText txt_text_area;
    Button btn_paste, btn_upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_texts);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Text");
        actionBar.setBackgroundDrawable(getDrawable(R.color.col_bg_dark_gray));
        actionBar.show();

        txt_text_area = findViewById(R.id.txt_box);
        btn_paste = findViewById(R.id.txt_paste_from_clip);
        btn_upload = findViewById(R.id.txt_upload);

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Handle_Texts.this, "btn_upload", Toast.LENGTH_SHORT).show();
            }
        });

        btn_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPasteText();
            }
        });
    }
    private void initPasteText() {
        // Gets a handle to the Clipboard Manager.
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        // Gets a content resolver instance.
        ContentResolver cr = getContentResolver();

        ClipData clipData = clipboard.getPrimaryClip();

        try {
            ClipData.Item c_item = clipData.getItemAt(0);
            String pastedData = "";
            if (clipData != null) {
                if (clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)) {
                    pastedData = c_item.getText().toString();
                    txt_text_area.setText(pastedData);
                }
                else if (clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_INTENT)) {
                    Uri pastedUri = c_item.getUri();
                    if (pastedUri != null) {
                        // Calls a routine to resolve the URI and get data from it.
                        String uriMimeType = cr.getType(pastedUri);
                        // Something is wrong. The MIME type is plain text, but the
                        // clipboard doesn't contain text or a Uri. Report an error.
                        Toast.makeText(Handle_Texts.this, "Clipboard contains an invalid data type", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(Handle_Texts.this, "MIMETYPE_TEXT_INTENT not supported", Toast.LENGTH_SHORT).show();
                }
                else if (clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_URILIST)) {
                    Toast.makeText(Handle_Texts.this, "MIMETYPE_TEXT_URILIST not supported", Toast.LENGTH_SHORT).show();
                }
                else if (clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_HTML)) {
                    Toast.makeText(Handle_Texts.this, "MIMETYPE_TEXT_HTML not supported", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(Handle_Texts.this, "MIMETYPE_TEXT_PLAIN  not supported", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception ex){
            Toast.makeText(Handle_Texts.this, "Seems the Clipboard is empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}