package com.niccher.pctophonecopier.activities;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.niccher.pctophonecopier.R;

public class Handle_Text_2_Image extends AppCompatActivity {

    public static int code_camera = 123;
    ImageView img_view;
    Button btn_sel_img, btn_extract;
    ProgressBar p_bar_progress;
    TextView txt_extracted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_text2_image);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Copy from Image");
        actionBar.show();

        img_view = findViewById(R.id.img_box);
        btn_sel_img = findViewById(R.id.btn_get_image);
        //btn_extract = findViewById(R.id.btn_extract_text);
        p_bar_progress = findViewById(R.id.prg_state);
        txt_extracted = findViewById(R.id.txt_box);

        p_bar_progress.setVisibility(View.GONE);
        txt_extracted.setVisibility(View.GONE);

        btn_sel_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selImage();
            }
        });

        img_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selImage();
            }
        });
    }

    private void selImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, code_camera);
            }
        }
        //Open the camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,code_camera);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle bundle = data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");
        img_view.setImageBitmap(bitmap);
        p_bar_progress.setVisibility(View.VISIBLE);

        //Create a FirebaseVisionImage object from your image/bitmap.
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVision firebaseVision = FirebaseVision.getInstance();
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();

        //Process the Image
        Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);

        txt_extracted.setVisibility(View.VISIBLE);
        task.addOnSuccessListener(firebaseVisionText -> {
            String text = firebaseVisionText.getText();
            txt_extracted.setText(text);
        });
        task.addOnFailureListener(e -> {
            txt_extracted.setText(e.getMessage());
        });
        p_bar_progress.setVisibility(View.GONE);
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