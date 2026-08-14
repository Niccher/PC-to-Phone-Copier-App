package com.niccher.p2p_copier_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.niccher.p2p_copier_app.R;

public class Regista extends AppCompatActivity {

    Button btn_get_started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regista);

        btn_get_started = findViewById(R.id.reg_get_started);
        Button btn_backend_config = findViewById(R.id.btn_reg_backend_config);

        btn_get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent auth = new Intent(Regista.this, AuthSession.class);
                auth.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(auth);
            }
        });

        if (btn_backend_config != null) {
            btn_backend_config.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent config = new Intent(Regista.this, BackendConfigActivity.class);
                    startActivity(config);
                }
            });
        }
    }
}