package com.niccher.pctophonecopier.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.niccher.pctophonecopier.Dope;
import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.activities.Regista;
import com.niccher.pctophonecopier.utils.Konstants;

public class Splasher extends AppCompatActivity {
    private ProgressBar mProgressBar;
    private int progressStatus = 0;
    Konstants kon;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splasher);

        mProgressBar = (ProgressBar) findViewById (R.id.progress_bar);

        kon = new Konstants();

    }

    @Override
    protected void onStart() {
        super.onStart();
        startloading();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startloading();
    }

    private void startloading() {
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 4;
                    handler.post(new Runnable() {
                        public void run() {
                            mProgressBar.setProgress(progressStatus);
                            if (progressStatus == 100){
                                Intent to_validate = new Intent(Splasher.this, Regista.class);
                                Intent to_home = new Intent(Splasher.this, Dope.class);
                                String state = checkValidity();
                                if (Integer.parseInt(state) == 1 ){
                                    to_home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(to_home);
                                    Splasher.this.finish();
                                }else {
                                    to_validate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(to_validate);
                                    Splasher.this.finish();
                                }
                            }
                        }
                    });
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public String checkValidity(){
        SharedPreferences sharedPreferences = getSharedPreferences(kon.shared_pref_auth, Context.MODE_PRIVATE);
        String status = sharedPreferences.getString("status","1");
        return status;
    }
}