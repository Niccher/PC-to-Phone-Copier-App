package com.niccher.pctophonecopier.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.activities.Handle_Files;
import com.niccher.pctophonecopier.activities.Handle_Text_2_Image;
import com.niccher.pctophonecopier.activities.Handle_Texts;
import com.niccher.pctophonecopier.utils.SharedPrefs;

public class Fragment_Home extends Fragment {

    // UI Components
    private CardView cardTypeText, cardPasteClipboard, cardImageScan, cardQRScan, cardFileUpload;
    private MaterialButton btnTutorial, btnConnect;
    private TextView txtTransfersCount, txtFilesCount, txtTextsCount;

    // Preferences
    private SharedPrefs prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_home, container, false);

        // Initialize SharedPreferences
        prefs = new SharedPrefs(requireContext());

        // Initialize views
        initViews(view);
        setupClickListeners();
        updateStats();

        return view;
    }

    private void initViews(View view) {
        // Card views
        cardTypeText = view.findViewById(R.id.option_1);
        cardPasteClipboard = view.findViewById(R.id.option_2);
        cardImageScan = view.findViewById(R.id.option_3);
        cardQRScan = view.findViewById(R.id.option_4);
        cardFileUpload = view.findViewById(R.id.option_5);

        // Buttons
        btnTutorial = view.findViewById(R.id.btn_tutorial);
        btnConnect = view.findViewById(R.id.btn_connect);

        // Stats
        txtTransfersCount = view.findViewById(R.id.txt_transfers_count);
        txtFilesCount = view.findViewById(R.id.txt_files_count);
        txtTextsCount = view.findViewById(R.id.txt_texts_count);
    }

    private void setupClickListeners() {
        // Type Text
        cardTypeText.setOnClickListener(v -> {
            navigateWithAnimation(Handle_Texts.class);
        });

        // Paste Clipboard
        cardPasteClipboard.setOnClickListener(v -> {
            navigateWithAnimation(Handle_Texts.class);
        });

        // Image to Text
        cardImageScan.setOnClickListener(v -> {
            navigateWithAnimation(Handle_Text_2_Image.class);
        });

        // QR Scan
        cardQRScan.setOnClickListener(v -> {
            initQRScanner();
        });

        // File Upload
        cardFileUpload.setOnClickListener(v -> {
            navigateWithAnimation(Handle_Files.class);
        });

        // Tutorial Button
        btnTutorial.setOnClickListener(v -> {
            showTutorialDialog();
        });

        // Connect Button
        btnConnect.setOnClickListener(v -> {
            showConnectDialog();
        });
    }

    private void navigateWithAnimation(Class<?> activityClass) {
        Intent intent = new Intent(getActivity(), activityClass);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private void initQRScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan QR code to copy text");
        integrator.setOrientationLocked(false);
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();

        // Update stats
        incrementTransferCount();
    }

    private void showTutorialDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("How to Use P2P Copier")
                .setMessage("1. Connect to the website using QR or code\n\n" +
                        "2. Choose a transfer method:\n" +
                        "   • Type text manually\n" +
                        "   • Paste from clipboard\n" +
                        "   • Extract text from images\n" +
                        "   • Scan QR codes\n" +
                        "   • Upload files\n\n" +
                        "3. Your data will sync across devices instantly!")
                .setPositiveButton("Got it", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Watch Video", (dialog, which) -> {
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Opening tutorial video...", Toast.LENGTH_SHORT).show();
                })
                .setIcon(R.mipmap.app_logo)
                .show();
    }

    private void showConnectDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Connect to Website")
                .setMessage("To start transferring data between devices:\n\n" +
                        "1. Open p2pcopier.com on your computer\n" +
                        "2. Generate a QR code or connection code\n" +
                        "3. Scan or enter the code here\n\n" +
                        "Once connected, you can seamlessly transfer text, images, and files!")
                .setPositiveButton("Scan QR", (dialog, which) -> {
                    dialog.dismiss();
                    initQRScanner();
                })
                .setNegativeButton("Enter Code", (dialog, which) -> {
                    dialog.dismiss();
                    showCodeInputDialog();
                })
                .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setIcon(R.mipmap.img_qr)
                .show();
    }

    private void showCodeInputDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_code_input, null);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Enter Connection Code")
                .setView(dialogView)
                .setPositiveButton("Connect", (dialog, which) -> {
                    // Handle code validation
                    Toast.makeText(getContext(), "Connecting...", Toast.LENGTH_SHORT).show();
                    incrementTransferCount();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void updateStats() {
        int transfers = prefs.getInt("transfer_count", 0);
        int files = prefs.getInt("file_count", 0);
        int texts = prefs.getInt("text_count", 0);

        txtTransfersCount.setText(String.valueOf(transfers));
        txtFilesCount.setText(String.valueOf(files));
        txtTextsCount.setText(String.valueOf(texts));
    }

    private void incrementTransferCount() {
        int current = prefs.getInt("transfer_count", 0);
        prefs.saveInt("transfer_count", current + 1);
        updateStats();
    }

    private void incrementFileCount() {
        int current = prefs.getInt("file_count", 0);
        prefs.saveInt("file_count", current + 1);
        updateStats();
    }

    private void incrementTextCount() {
        int current = prefs.getInt("text_count", 0);
        prefs.saveInt("text_count", current + 1);
        updateStats();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Scan cancelled", Toast.LENGTH_SHORT).show();
            } else {
                showQRResultDialog(result.getContents());
                incrementTextCount();
            }
        }
    }

    private void showQRResultDialog(String qrContent) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("QR Code Content")
                .setMessage(qrContent)
                .setPositiveButton("Copy", (dialog, which) -> {
                    copyToClipboard(qrContent);
                    Toast.makeText(getContext(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton("Share", (dialog, which) -> {
                    shareContent(qrContent);
                    dialog.dismiss();
                })
                .setNeutralButton("Close", (dialog, which) -> dialog.dismiss())
                .setIcon(R.mipmap.img_qr)
                .show();
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("P2P Copier", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
    }

    private void shareContent(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStats();
    }
}