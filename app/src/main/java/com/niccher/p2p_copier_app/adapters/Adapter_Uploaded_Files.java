package com.niccher.p2p_copier_app.adapters;

import static com.niccher.p2p_copier_app.utils.Helpers.humanReadableByteCountBin;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.niccher.p2p_copier_app.R;
import com.niccher.p2p_copier_app.interfaces.RetrofitInterface;
import com.niccher.p2p_copier_app.model.Mod_File_Delete;
import com.niccher.p2p_copier_app.model.Mod_List_File_Uploaded;
import com.niccher.p2p_copier_app.utils.Helpers;
import com.niccher.p2p_copier_app.utils.Konstants;
import com.niccher.p2p_copier_app.utils.ServiceGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Adapter_Uploaded_Files extends RecyclerView.Adapter<Adapter_Uploaded_Files.ViewHolder> {

    ArrayList<Mod_List_File_Uploaded> list_file_infos;
    Context context;

    Retrofit retrofit_download, retrofit_delete = null;
    RetrofitInterface interface_download, interface_delete = null;

    Konstants kon;
    Gson gson = null;
    Helpers helpers = null;

    View view;
    int perm_storage_write = 102;

    private final HashSet<Integer> selectedPositions = new HashSet<>();
    private OnSelectionChangedListener selectionListener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int count);
    }

    public Adapter_Uploaded_Files(ArrayList<Mod_List_File_Uploaded> list_file_infos, Context context, OnSelectionChangedListener listener) {
        this.list_file_infos = list_file_infos;
        this.context = context;
        this.selectionListener = listener;

        kon = new Konstants();
        helpers = new Helpers();
        interface_download = ServiceGenerator.createService(RetrofitInterface.class, context);
        interface_delete = interface_download;
    }

    public void selectAll() {
        for (int i = 0; i < list_file_infos.size(); i++) selectedPositions.add(i);
        notifyDataSetChanged();
        if (selectionListener != null) selectionListener.onSelectionChanged(selectedPositions.size());
    }

    public void clearSelection() {
        selectedPositions.clear();
        notifyDataSetChanged();
        if (selectionListener != null) selectionListener.onSelectionChanged(0);
    }

    public List<Mod_List_File_Uploaded> getSelectedItems() {
        List<Mod_List_File_Uploaded> result = new ArrayList<>();
        for (int pos : selectedPositions) {
            if (pos < list_file_infos.size()) result.add(list_file_infos.get(pos));
        }
        return result;
    }

    private boolean isMultiSelectMode = false;

    public void setMultiSelectMode(boolean enabled) {
        this.isMultiSelectMode = enabled;
        if (!enabled) {
            selectedPositions.clear();
            if (selectionListener != null) selectionListener.onSelectionChanged(0);
        }
        notifyDataSetChanged();
    }

    public boolean isMultiSelectMode() {
        return isMultiSelectMode;
    }

    @NonNull
    @Override
    public Adapter_Uploaded_Files.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_uploaded_file_info, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Uploaded_Files.ViewHolder holder, int position) {
        Mod_List_File_Uploaded fileItem = list_file_infos.get(position);
        holder.part_name.setText(fileItem.getDisplayName());
        holder.part_type.setText(fileItem.getUp_file_Type());

        try {
            holder.part_size.setText(humanReadableByteCountBin(Long.parseLong(fileItem.getUp_file_Size())));
        } catch (Exception e) {
            holder.part_size.setText(fileItem.getUp_file_Size());
        }

        holder.part_date.setText(fileItem.getUp_file_Created_at());
        holder.part_mini_progress.setVisibility(View.GONE);

        // Bind Filetype Icon & Color Badge
        bindFileTypeIconAndBadge(holder, fileItem.getUp_file_Extension(), fileItem.getUp_file_Type(), fileItem.getDisplayName());

        // Checkbox & Multi-select handling
        holder.checkbox_select.setVisibility(isMultiSelectMode ? View.VISIBLE : View.GONE);
        holder.checkbox_select.setOnCheckedChangeListener(null);
        holder.checkbox_select.setChecked(selectedPositions.contains(position));
        holder.checkbox_select.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) selectedPositions.add(position);
            else selectedPositions.remove(position);
            if (selectionListener != null) selectionListener.onSelectionChanged(selectedPositions.size());
        });

        // Item click behavior
        holder.itemView.setOnClickListener(v -> {
            if (isMultiSelectMode) {
                holder.checkbox_select.toggle();
            } else if (fileItem.isTextItem()) {
                showTextDetailsModal(fileItem, position, holder);
            } else {
                showContextMenuForItem(v, position, holder);
            }
        });

        // Long press listener to show context menu
        holder.itemView.setOnLongClickListener(v -> {
            showContextMenuForItem(v, position, holder);
            return true;
        });
    }

    private void bindFileTypeIconAndBadge(ViewHolder holder, String ext, String type, String fileName) {
        String cleanExt = ext != null && !ext.trim().isEmpty() ? ext.trim().toLowerCase() : "";
        if (cleanExt.isEmpty() && fileName != null && fileName.contains(".")) {
            cleanExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }

        if (holder.part_ext != null) {
            holder.part_ext.setText(cleanExt.isEmpty() ? "?" : cleanExt.toUpperCase());
        }

        int iconRes = R.drawable.ic_file_upload;
        int bgColor = 0xFF1565C0; // default primary blue

        if (cleanExt.equals("ocr")) {
            iconRes = R.drawable.ic_menu_paste;
            bgColor = 0xFF7B1FA2; // Purple
        } else if (cleanExt.equals("qr") || cleanExt.equals("qr_code")) {
            iconRes = R.drawable.ic_qr;
            bgColor = 0xFF00796B; // Teal
        } else if (cleanExt.equals("text") || cleanExt.equals("txt")) {
            iconRes = R.drawable.ic_file_attach;
            bgColor = 0xFF388E3C; // Green
        } else if (cleanExt.equals("pdf")) {
            iconRes = R.drawable.ic_menu_save;
            bgColor = 0xFFD32F2F; // Red
        } else if (cleanExt.equals("jpg") || cleanExt.equals("jpeg") || cleanExt.equals("png") || cleanExt.equals("gif") || cleanExt.equals("webp") || cleanExt.equals("bmp") || cleanExt.equals("svg") || (type != null && type.contains("image"))) {
            iconRes = R.drawable.ic_menu_gallery;
            bgColor = 0xFF0288D1; // Cyan / Light Blue
        } else if (cleanExt.equals("mp4") || cleanExt.equals("avi") || cleanExt.equals("mov") || cleanExt.equals("mkv") || cleanExt.equals("webm") || (type != null && type.contains("video"))) {
            iconRes = R.drawable.ic_menu_camera;
            bgColor = 0xFFC2185B; // Pink / Magenta
        } else if (cleanExt.equals("mp3") || cleanExt.equals("wav") || cleanExt.equals("aac") || cleanExt.equals("flac") || cleanExt.equals("m4a") || (type != null && type.contains("audio"))) {
            iconRes = R.drawable.ic_menu_play_clip;
            bgColor = 0xFFE65100; // Orange / Amber
        } else if (cleanExt.equals("zip") || cleanExt.equals("rar") || cleanExt.equals("7z") || cleanExt.equals("tar") || cleanExt.equals("gz") || cleanExt.equals("bz2")) {
            iconRes = R.drawable.ic_file_attach;
            bgColor = 0xFF455A64; // Slate / Gray
        } else if (cleanExt.equals("txt") || cleanExt.equals("doc") || cleanExt.equals("docx") || cleanExt.equals("xls") || cleanExt.equals("xlsx") || cleanExt.equals("json") || cleanExt.equals("xml") || cleanExt.equals("csv")) {
            iconRes = R.drawable.ic_menu_save;
            bgColor = 0xFF2E7D32; // Green
        }

        if (holder.part_icon != null) {
            holder.part_icon.setImageResource(iconRes);
        }
        if (holder.card_badge != null) {
            holder.card_badge.setCardBackgroundColor(bgColor);
        }
    }

    private void showContextMenuForItem(View anchor, int position, ViewHolder holder) {
        if (position < 0 || position >= list_file_infos.size()) return;
        Mod_List_File_Uploaded fileItem = list_file_infos.get(position);
        androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(context, anchor);

        if (fileItem.isTextItem()) {
            popup.getMenu().add(0, 1, 0, "📋 Copy Text");
            popup.getMenu().add(0, 2, 1, "🗑️ Delete Text");
            popup.getMenu().add(0, 3, 2, "☑️ Select Multiple");
        } else {
            popup.getMenu().add(0, 1, 0, "📥 Download File");
            popup.getMenu().add(0, 2, 1, "🗑️ Delete File");
            popup.getMenu().add(0, 3, 2, "☑️ Select Multiple");
        }

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 1:
                    if (fileItem.isTextItem()) {
                        copyTextToClipboard(fileItem.getTextContent());
                    } else {
                        performDownload(position, holder);
                    }
                    return true;
                case 2:
                    performDelete(position, holder);
                    return true;
                case 3:
                    setMultiSelectMode(true);
                    selectedPositions.add(position);
                    notifyDataSetChanged();
                    if (selectionListener != null) selectionListener.onSelectionChanged(selectedPositions.size());
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private void copyTextToClipboard(String content) {
        if (content == null) return;
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", content);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTextDetailsModal(Mod_List_File_Uploaded fileItem, int position, ViewHolder holder) {
        if (context == null || fileItem == null) return;

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_text_details, null, false);
        builder.setView(dialogView);

        TextView txtTitle = dialogView.findViewById(R.id.dialog_text_title);
        TextView txtTypeBadge = dialogView.findViewById(R.id.dialog_text_badge);
        TextView txtDate = dialogView.findViewById(R.id.dialog_text_date);
        TextView txtContent = dialogView.findViewById(R.id.dialog_text_content);
        com.google.android.material.button.MaterialButton btnCopy = dialogView.findViewById(R.id.btn_dialog_copy);
        com.google.android.material.button.MaterialButton btnDelete = dialogView.findViewById(R.id.btn_dialog_delete);
        com.google.android.material.button.MaterialButton btnClose = dialogView.findViewById(R.id.btn_dialog_close);

        android.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        String source = fileItem.getTextSource() != null ? fileItem.getTextSource() : "Text Item";
        txtTitle.setText(source);
        txtTypeBadge.setText(fileItem.getUp_file_Extension());
        txtDate.setText(fileItem.getUp_file_Created_at());
        txtContent.setText(fileItem.getTextContent() != null ? fileItem.getTextContent() : "");

        btnCopy.setOnClickListener(v -> copyTextToClipboard(fileItem.getTextContent()));

        btnDelete.setOnClickListener(v -> {
            dialog.dismiss();
            performDelete(position, holder);
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void performDownload(int position, ViewHolder holder) {
        if (position < 0 || position >= list_file_infos.size()) return;
        Mod_List_File_Uploaded targetFile = list_file_infos.get(position);
        String part_file_id = targetFile.getUp_file_uuid();
        String part_file_display_name = targetFile.getDisplayName();
        String part_dev_id = Helpers.get_prefs_dev("dev_uuid", context);
        String part_sess_id = Helpers.get_prefs_sess("auth_auth_code_id", context);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("var_file_id", part_file_id);
        parameters.put("var_dev_id", part_dev_id);
        parameters.put("var_sess_id", part_sess_id);

        if (holder != null && holder.part_mini_progress != null) {
            holder.part_mini_progress.setVisibility(View.VISIBLE);
        }

        Call<ResponseBody> call = interface_download.getFilesUploadedbySessDevidDownloaded(parameters);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    checkPermissions();
                    Toast.makeText(context, "Downloading -> " + part_file_display_name, Toast.LENGTH_SHORT).show();
                    try {
                        File new_loaded_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + part_file_display_name);
                        InputStream inputStream = null;
                        OutputStream outputStream = null;
                        try {
                            byte[] fileReader = new byte[4096];
                            inputStream = response.body().byteStream();
                            outputStream = new FileOutputStream(new_loaded_file);

                            while (true) {
                                int read = inputStream.read(fileReader);
                                if (read == -1) break;
                                outputStream.write(fileReader, 0, read);
                            }
                            outputStream.flush();
                            Snackbar.make(anchorViewOrParent(holder), "File " + part_file_display_name + " downloaded successfully", Snackbar.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(context, "Unable to save file: " + part_file_display_name, Toast.LENGTH_SHORT).show();
                        } finally {
                            if (inputStream != null) inputStream.close();
                            if (outputStream != null) outputStream.close();
                        }
                    } catch (IOException e) {
                        Toast.makeText(context, "Error saving file: " + part_file_display_name, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Unexpected response from server", Toast.LENGTH_LONG).show();
                }
                if (holder != null && holder.part_mini_progress != null) {
                    holder.part_mini_progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Failed to initiate download", Toast.LENGTH_LONG).show();
                if (holder != null && holder.part_mini_progress != null) {
                    holder.part_mini_progress.setVisibility(View.GONE);
                }
            }
        });
    }

    private View anchorViewOrParent(ViewHolder holder) {
        return (holder != null && holder.itemView != null) ? holder.itemView : view;
    }

    private void performDelete(int position, ViewHolder holder) {
        if (position < 0 || position >= list_file_infos.size()) return;
        Mod_List_File_Uploaded targetItem = list_file_infos.get(position);

        if (holder != null && holder.part_mini_progress != null) {
            holder.part_mini_progress.setVisibility(View.VISIBLE);
        }

        if (targetItem.isTextItem()) {
            Map<String, String> parameters = new HashMap<>();
            parameters.put("var_text_uuid", targetItem.getUp_file_uuid());
            parameters.put("text_uuid", targetItem.getUp_file_uuid());

            RetrofitInterface retro = ServiceGenerator.createService(RetrofitInterface.class, context);
            retro.deleteTextUploaded(parameters).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Text item deleted", Toast.LENGTH_SHORT).show();
                        if (position < list_file_infos.size()) {
                            list_file_infos.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, list_file_infos.size());
                        }
                    } else {
                        Toast.makeText(context, "Failed to delete text item", Toast.LENGTH_SHORT).show();
                    }
                    if (holder != null && holder.part_mini_progress != null) {
                        holder.part_mini_progress.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, "Network error deleting text", Toast.LENGTH_SHORT).show();
                    if (holder != null && holder.part_mini_progress != null) {
                        holder.part_mini_progress.setVisibility(View.GONE);
                    }
                }
            });
            return;
        }

        String part_file_id = targetItem.getUp_file_uuid();
        String part_file_name = targetItem.getUp_file_Name();

        Map<String, String> parameters = new HashMap<>();
        parameters.put("var_file_uuid", part_file_id);
        parameters.put("var_file_name", part_file_name);

        Call<Mod_File_Delete> call = interface_delete.getFilesUploadedbySessDevidDelete(parameters);
        call.enqueue(new Callback<Mod_File_Delete>() {
            @Override
            public void onResponse(Call<Mod_File_Delete> call, Response<Mod_File_Delete> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Mod_File_Delete mod_file_delete = response.body();
                    if ("1".equals(mod_file_delete.getStatus())) {
                        Toast.makeText(context, "File " + targetItem.getDisplayName() + " deleted", Toast.LENGTH_SHORT).show();
                        if (position < list_file_infos.size()) {
                            list_file_infos.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, list_file_infos.size());
                        }
                    } else {
                        Toast.makeText(context, "Deleting " + targetItem.getDisplayName() + " failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Unexpected error deleting file", Toast.LENGTH_LONG).show();
                }
                if (holder != null && holder.part_mini_progress != null) {
                    holder.part_mini_progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Mod_File_Delete> call, Throwable t) {
                Toast.makeText(context, "Failed to delete file", Toast.LENGTH_LONG).show();
                if (holder != null && holder.part_mini_progress != null) {
                    holder.part_mini_progress.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String file_name) {
        return false;
    }

    private void setFitlToDelete(ResponseBody body, String file_name) {
    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, perm_storage_write);
        }
    }

    @Override
    public int getItemCount() {
        return list_file_infos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView part_name, part_type, part_ext, part_size, part_date;
        ProgressBar part_mini_progress;
        CheckBox checkbox_select;
        android.widget.ImageView part_icon;
        com.google.android.material.card.MaterialCardView card_badge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            part_name = itemView.findViewById(R.id.f_info_name);
            part_type = itemView.findViewById(R.id.f_info_type);
            part_ext = itemView.findViewById(R.id.f_info_ext);
            part_size = itemView.findViewById(R.id.f_info_size);
            part_date = itemView.findViewById(R.id.f_info_date);

            part_mini_progress = itemView.findViewById(R.id.f_info_progress);
            checkbox_select = itemView.findViewById(R.id.f_info_checkbox);
            part_icon = itemView.findViewById(R.id.f_info_icon);
            card_badge = itemView.findViewById(R.id.card_ext_badge);
        }
    }
}
