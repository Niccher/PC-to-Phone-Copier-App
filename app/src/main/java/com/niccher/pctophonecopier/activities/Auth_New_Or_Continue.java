package com.niccher.pctophonecopier.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.niccher.pctophonecopier.Dope;
import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.splash.Splasher;

public class Auth_New_Or_Continue extends AppCompatActivity {

    ConstraintLayout sess_continue, sess_new;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_new_or_continue);

        sess_continue = findViewById(R.id.option_previous);
        sess_new = findViewById(R.id.option_new);

        Intent to_validate = new Intent(Auth_New_Or_Continue.this, Regista.class);
        Intent to_home = new Intent(Auth_New_Or_Continue.this, Dope.class);

        sess_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                to_home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(to_home);
                Auth_New_Or_Continue.this.finish();
            }
        });

        sess_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                to_validate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(to_validate);
                Auth_New_Or_Continue.this.finish();
            }
        });
    }
}