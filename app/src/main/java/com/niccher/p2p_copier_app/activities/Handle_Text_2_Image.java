package com.niccher.p2p_copier_app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.niccher.p2p_copier_app.R;
import com.niccher.p2p_copier_app.interfaces.RetrofitInterface;
import com.niccher.p2p_copier_app.utils.Helpers;
import com.niccher.p2p_copier_app.utils.ServiceGenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Handle_Text_2_Image extends AppCompatActivity {

    public static final int CODE_CAMERA = 123;
    public static final int CODE_GALLERY = 124;

    private ImageView img_view;
    private MaterialButton btn_sel_img, btn_gallery, btn_upload_ocr;
    private ProgressBar p_bar_progress;
    private MaterialCardView progress_card;
    private TextView txt_extracted, text_char_count;
    private TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_text2_image);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_image);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Image to Text (OCR)");
        }

        img_view = findViewById(R.id.img_box);
        btn_sel_img = findViewById(R.id.btn_get_image);
        btn_gallery = findViewById(R.id.btn_gallery);
        btn_upload_ocr = findViewById(R.id.btn_upload_ocr);
        p_bar_progress = findViewById(R.id.prg_state);
        progress_card = findViewById(R.id.progress_card);
        txt_extracted = findViewById(R.id.txt_box);
        text_char_count = findViewById(R.id.text_char_count);

        if (progress_card != null) progress_card.setVisibility(View.GONE);
        if (p_bar_progress != null) p_bar_progress.setVisibility(View.GONE);

        // Initialize Google ML Kit on-device Latin text recognizer
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        btn_sel_img.setOnClickListener(v -> selImageCamera());
        if (btn_gallery != null) {
            btn_gallery.setOnClickListener(v -> selImageGallery());
        }
        img_view.setOnClickListener(v -> selImageCamera());

        if (btn_upload_ocr != null) {
            btn_upload_ocr.setOnClickListener(v -> uploadExtractedText());
        }
    }

    private void selImageCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CODE_CAMERA);
                return;
            }
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CODE_CAMERA);
    }

    private void selImageGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image for OCR"), CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        try {
            Bitmap bitmap = null;
            if (requestCode == CODE_CAMERA) {
                Bundle bundle = data.getExtras();
                if (bundle != null && bundle.get("data") != null) {
                    bitmap = (Bitmap) bundle.get("data");
                }
            } else if (requestCode == CODE_GALLERY) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (IOException e) {
                        Toast.makeText(this, "Failed to load selected image", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            if (bitmap != null) {
                img_view.setImageBitmap(bitmap);
                processImageWithMLKit(bitmap);
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Error processing image: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void processImageWithMLKit(Bitmap bitmap) {
        if (progress_card != null) progress_card.setVisibility(View.VISIBLE);
        if (p_bar_progress != null) p_bar_progress.setVisibility(View.VISIBLE);

        InputImage image = InputImage.fromBitmap(bitmap, 0);

        textRecognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    if (progress_card != null) progress_card.setVisibility(View.GONE);
                    if (p_bar_progress != null) p_bar_progress.setVisibility(View.GONE);

                    String text = visionText.getText();
                    if (txt_extracted != null) {
                        txt_extracted.setText(text);
                    }
                    if (text_char_count != null) {
                        text_char_count.setText(text.length() + " chars");
                    }
                    if (text.trim().isEmpty()) {
                        Toast.makeText(Handle_Text_2_Image.this, "No text detected in this image", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Handle_Text_2_Image.this, "Text extracted successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    if (progress_card != null) progress_card.setVisibility(View.GONE);
                    if (p_bar_progress != null) p_bar_progress.setVisibility(View.GONE);
                    Toast.makeText(Handle_Text_2_Image.this, "OCR Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadExtractedText() {
        if (txt_extracted == null) return;
        String content = txt_extracted.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "Please extract text from an image before uploading", Toast.LENGTH_SHORT).show();
            return;
        }

        if (btn_upload_ocr != null) btn_upload_ocr.setEnabled(false);
        if (progress_card != null) progress_card.setVisibility(View.VISIBLE);

        RetrofitInterface api = ServiceGenerator.createService(RetrofitInterface.class, this);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("var_dev_uuid", Helpers.get_prefs_dev("dev_uuid", this));
        parameters.put("var_auth_code_id", Helpers.get_prefs_sess("auth_auth_code_id", this));
        parameters.put("var_text_sess_id", Helpers.get_prefs_sess("auth_auth_code_id", this));
        parameters.put("session_id", Helpers.get_prefs_sess("auth_auth_code_id", this));
        parameters.put("var_text_content", content);
        parameters.put("text_content", content);
        parameters.put("var_text_source", "OCR Text");
        parameters.put("text_source", "OCR Text");
        parameters.put("var_text_title", "Camera OCR Extraction");
        parameters.put("text_title", "Camera OCR Extraction");

        api.setTextToUpload(parameters).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (btn_upload_ocr != null) btn_upload_ocr.setEnabled(true);
                if (progress_card != null) progress_card.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Toast.makeText(Handle_Text_2_Image.this, "Extracted text uploaded successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Handle_Text_2_Image.this, "Upload failed (Server HTTP " + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (btn_upload_ocr != null) btn_upload_ocr.setEnabled(true);
                if (progress_card != null) progress_card.setVisibility(View.GONE);
                Toast.makeText(Handle_Text_2_Image.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}