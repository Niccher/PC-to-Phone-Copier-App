package com.niccher.pctophonecopier.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
                Toast.makeText(Handle_Texts.this, "btn_paste", Toast.LENGTH_SHORT).show();
            }
        });
    }
}