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
        gson = new GsonBuilder().setLenient().create();

        retrofit_download = new Retrofit.Builder()
                .baseUrl(kon.str_file_upload_action)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(ServiceGenerator.getUnsafeOkHttpClient())
                .build();
        interface_download = retrofit_download.create(RetrofitInterface.class);

        retrofit_delete = new Retrofit.Builder()
                .baseUrl(kon.str_file_upload_action)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(ServiceGenerator.getUnsafeOkHttpClient())
                .build();
        interface_delete = retrofit_delete.create(RetrofitInterface.class);
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

    @NonNull
    @Override
    public Adapter_Uploaded_Files.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_uploaded_file_info, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Uploaded_Files.ViewHolder holder, int position) {
        holder.part_name.setText(list_file_infos.get(position).getUp_file_Name());
        holder.part_type.setText(list_file_infos.get(position).getUp_file_Type());
        holder.part_size.setText(humanReadableByteCountBin(Long.parseLong(list_file_infos.get(position).getUp_file_Size())));
        holder.part_ext.setText(list_file_infos.get(position).getUp_file_Extension());
        holder.part_date.setText(list_file_infos.get(position).getUp_file_Created_at());
        holder.part_mini_progress.setVisibility(View.GONE);

        // Checkbox selection
        holder.checkbox_select.setOnCheckedChangeListener(null); // prevent recursive triggers
        holder.checkbox_select.setChecked(selectedPositions.contains(position));
        holder.checkbox_select.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) selectedPositions.add(position);
            else selectedPositions.remove(position);
            if (selectionListener != null) selectionListener.onSelectionChanged(selectedPositions.size());
        });
        holder.itemView.setOnClickListener(v -> holder.checkbox_select.toggle());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            File file_path = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + list_file_infos.get(position).getUp_file_Name());
            Drawable icon_present = holder.part_mini_download.getContext().getResources().getDrawable( R.drawable.ic_download_blue );
            Drawable icon_absent  = holder.part_mini_download.getContext().getResources().getDrawable( R.drawable.ic_download );
            if(file_path.exists() && ! file_path.isDirectory()) {
                holder.part_mini_download.setCompoundDrawablesWithIntrinsicBounds( icon_present, null, null, null);
            }else {
                holder.part_mini_download.setCompoundDrawablesWithIntrinsicBounds( icon_absent, null, null, null);
            }
        }

        holder.part_mini_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String part_file_id = list_file_infos.get(position).getUp_file_uuid();
                String part_dev_id = Helpers.get_prefs_dev("dev_uuid", context);
                String part_sess_id = Helpers.get_prefs_sess("auth_auth_code_id", context);

                Map<String, String> parameters = new HashMap<>();
                parameters.put("var_file_id", part_file_id);
                parameters.put("var_dev_id", part_dev_id);
                parameters.put("var_sess_id", part_sess_id);

                holder.part_mini_progress.setVisibility(View.VISIBLE);

                Call<ResponseBody> call = interface_download.getFilesUploadedbySessDevidDownloaded(parameters);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            checkPermissions();
                            Toast.makeText(context, "Downloading -> " + list_file_infos.get(position).getUp_file_Name() , Toast.LENGTH_SHORT).show();
                            //boolean writtenToDisk = writeResponseBodyToDisk(response.body(), list_file_infos.get(position).getUp_file_Name());
                            try {
                                File new_loaded_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + list_file_infos.get(position).getUp_file_Name());
                                InputStream inputStream = null;
                                OutputStream outputStream = null;
                                try {
                                    byte[] fileReader = new byte[4096];
                                    long fileSize = response.body().contentLength();
                                    long fileSizeDownloaded = 0;
                                    float size_downloaded = 0;

                                    inputStream = response.body().byteStream();
                                    outputStream = new FileOutputStream(new_loaded_file);

                                    while (true) {
                                        int read = inputStream.read(fileReader);
                                        if (read == -1) {
                                            break;
                                        }
                                        outputStream.write(fileReader, 0, read);
                                        fileSizeDownloaded += read;
                                        //Double downloaded = Double.parseDouble(String.valueOf(fileSizeDownloaded))/ Double.parseDouble(String.valueOf(fileSize));
                                        //holder.part_mini_progress.setProgress((int) (downloaded * 100));
                                        //updateProgressUI(String.valueOf(fileSizeDownloaded), String.valueOf(fileSize), holder, position);
                                    }
                                    outputStream.flush();
                                    Snackbar snackbar = Snackbar.make(view, "File "+list_file_infos.get(position).getUp_file_Name()+" has been successfully downloaded",Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                } catch (IOException e) {
                                    Toast.makeText(context, "Unable to save file: " + list_file_infos.get(position).getUp_file_Name(), Toast.LENGTH_SHORT).show();
                                } finally {
                                    if (inputStream != null) {
                                        inputStream.close();
                                    }
                                    if (outputStream != null) {
                                        outputStream.close();
                                    }
                                }
                            } catch (IOException e) {
                                Toast.makeText(context, "Unknown error and unable to save file: " + list_file_infos.get(position).getUp_file_Name(), Toast.LENGTH_SHORT).show();
                            }
                            //Toast.makeText(context, "Download status -> " + writtenToDisk , Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Un expected response, please try again " , Toast.LENGTH_LONG).show();
                        }
                        holder.part_mini_progress.setVisibility(View.GONE);
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, "Failed to initiate download, please try again " , Toast.LENGTH_LONG).show();
                        holder.part_mini_progress.setVisibility(View.GONE);
                    }
                });
            }
        });

        holder.part_mini_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String part_file_id = list_file_infos.get(position).getUp_file_uuid();
                String part_file_name = list_file_infos.get(position).getUp_file_Name();
                String part_dev_id = Helpers.get_prefs_dev("dev_uuid", context);
                String part_sess_id = Helpers.get_prefs_sess("auth_auth_code_id", context);

                Map<String, String> parameters = new HashMap<>();
                parameters.put("var_file_uuid", part_file_id);
                parameters.put("var_file_name", part_file_name);
                //parameters.put("var_dev_id", part_dev_id);
                //parameters.put("var_sess_id", part_sess_id);

                holder.part_mini_progress.setVisibility(View.VISIBLE);

                Call<Mod_File_Delete> call = interface_delete.getFilesUploadedbySessDevidDelete(parameters);
                call.enqueue(new Callback<Mod_File_Delete>() {
                    @Override
                    public void onResponse(Call<Mod_File_Delete> call, Response<Mod_File_Delete> response) {
                        if (response.isSuccessful()) {
                            Mod_File_Delete mod_file_delete = response.body();
                            if (mod_file_delete.getStatus().equals("1")){
                                Toast.makeText(context, "File "+ part_file_name +" deleted " , Toast.LENGTH_SHORT).show();
                                list_file_infos.remove(position);
                                notifyItemChanged(position);
                                notifyDataSetChanged();
                                notifyItemRangeChanged(position, list_file_infos.size());
                            }else {
                                Toast.makeText(context, "Deleting "+ part_file_name +" has encountered an error " , Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Un-expected error when deleting response, please try again " , Toast.LENGTH_LONG).show();
                        }
                        holder.part_mini_progress.setVisibility(View.GONE);
                    }
                    @Override
                    public void onFailure(Call<Mod_File_Delete> call, Throwable t) {
                        Toast.makeText(context, "Failed to delete "+part_file_name+", please try again " , Toast.LENGTH_LONG).show();
                        holder.part_mini_progress.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String file_name) {
        try {
            File new_loaded_file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + file_name);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(new_loaded_file);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    //Log.d(kon.TAGGED, "file download size: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                Snackbar snackbar = Snackbar.make(view, "File "+file_name+" has been successfully downloaded",Snackbar.LENGTH_LONG);
                snackbar.show();

                return true;
            } catch (IOException e) {
                Toast.makeText(context, "Unable to save file: " + file_name, Toast.LENGTH_SHORT).show();
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Toast.makeText(context, "Unknown error and unable to save file: " + file_name, Toast.LENGTH_SHORT).show();
            return false;
        }
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
        TextView part_mini_download, part_mini_delete;
        ProgressBar part_mini_progress;
        CheckBox checkbox_select;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            part_name = itemView.findViewById(R.id.f_info_name);
            part_type = itemView.findViewById(R.id.f_info_type);
            part_ext = itemView.findViewById(R.id.f_info_ext);
            part_size = itemView.findViewById(R.id.f_info_size);
            part_date = itemView.findViewById(R.id.f_info_date);

            part_mini_download = itemView.findViewById(R.id.f_info_download);
            part_mini_delete = itemView.findViewById(R.id.f_info_delete);
            part_mini_progress = itemView.findViewById(R.id.f_info_progress);
            checkbox_select = itemView.findViewById(R.id.f_info_checkbox);
        }
    }
}
