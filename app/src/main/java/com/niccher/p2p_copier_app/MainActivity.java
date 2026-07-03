package com.niccher.p2p_copier_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.skyhope.showmoretextview.ShowMoreTextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button lg,camvw;
    EditText bas;
    ShowMoreTextView text_more_less;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camvw= (Button) findViewById(R.id.btn_camvw);
        //bas=findViewById(R.id.pasted);

        text_more_less = findViewById(R.id.text_view_show_more);
        text_more_less.setShowingLine(2);

        text_more_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_more_less.addShowMoreText("Continue");
                text_more_less.addShowLessText("Less");
                text_more_less.setShowMoreColor(Color.RED); // or other color
                text_more_less.setShowLessTextColor(Color.RED); // or other color
            }
        });
    }
}
