package com.niccher.pctophonecopier.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.niccher.pctophonecopier.R;

public class Regista extends AppCompatActivity {

    Button btn_get_started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regista);

        btn_get_started = findViewById(R.id.reg_get_started);

        btn_get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent auth = new Intent(Regista.this, AuthSession.class);
                auth.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(auth);
            }
        });
    }
}