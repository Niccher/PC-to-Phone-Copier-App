package com.niccher.pctophonecopier.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.interfaces.RetrofitInterface;
import com.niccher.pctophonecopier.model.Mod_File_Uploaded;
import com.niccher.pctophonecopier.model.Mod_File_info;
import com.niccher.pctophonecopier.utils.FileUtils;
import com.niccher.pctophonecopier.utils.Helpers;
import com.niccher.pctophonecopier.utils.Konstants;
import com.niccher.pctophonecopier.utils.ServiceGenerator;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Adapter_Sel_Files extends RecyclerView.Adapter<Adapter_Sel_Files.ViewHolder> {

    ArrayList<Mod_File_info> list_file_infos;
    Context context;
    Uri file_uri;

    Konstants kon;
    Gson gson = null;
    Helpers helpers = null;

    Retrofit retrofit_upload = null;
    RetrofitInterface interface_upload = null;

    public Adapter_Sel_Files(ArrayList<Mod_File_info> list_file_infos, Context context) {
        this.list_file_infos = list_file_infos;
        this.context = context;

        kon = new Konstants();
        helpers = new Helpers();
        gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit_upload = new Retrofit.Builder()
                .baseUrl(kon.str_file_upload_action)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(ServiceGenerator.getUnsafeOkHttpClient())
                .build();

        interface_upload = retrofit_upload.create(RetrofitInterface.class);
    }

    @NonNull
    @Override
    public Adapter_Sel_Files.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_info, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Sel_Files.ViewHolder holder, int position) {
        holder.part_name.setText(list_file_infos.get(position).getF_name());
        holder.part_type.setText(list_file_infos.get(position).getF_type());
        holder.part_size.setText(list_file_infos.get(position).getF_size());

        holder.part_mini_remove.setVisibility(View.VISIBLE);
        holder.part_mini_delete.setVisibility(View.GONE);

        holder.part_mini_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Uploading -> " + list_file_infos.get(position).getF_name(), Toast.LENGTH_SHORT).show();
                Uri file_uri = list_file_infos.get(position).getF_uri();

                File file_to_upload = FileUtils.getFile(context, file_uri);

                RequestBody requestFile = RequestBody.create(MediaType.parse( helpers.getFileName(file_uri, context)[1]), file_to_upload );
                MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file_to_upload.getName(), requestFile);

                // add another part within the multipart request
                String part_dev_id = helpers.get_prefs_dev("dev_uuid", context);

                RequestBody requestBody0 = RequestBody.create( okhttp3.MultipartBody.FORM, part_dev_id);

                // finally, execute the request
                Call<Mod_File_Uploaded> call = interface_upload.filesUpload(requestBody0, body);
                call.enqueue(new Callback<Mod_File_Uploaded>() {
                    @Override
                    public void onResponse(Call<Mod_File_Uploaded> call, Response<Mod_File_Uploaded> response) {
                        Mod_File_Uploaded postResponse = response.body();

                        if (postResponse.getStatus() == 0  || postResponse.getStatus() == 2 ) {
                            Toast.makeText(context, postResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }else if (postResponse.getStatus() == 1 ) {
                            Toast.makeText(context, postResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            holder.part_mini_upload.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_file_upload, 0, 0, 0);
                            holder.part_mini_upload.setEnabled(false);
                            holder.part_mini_remove.setVisibility(View.GONE);
                            holder.part_mini_delete.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<Mod_File_Uploaded> call, Throwable t) {
                        //Log.e("Upload error:", t.getMessage());
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        holder.part_mini_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Removing " + list_file_infos.get(position).getF_name(), Toast.LENGTH_SHORT).show();
                list_file_infos.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();
                notifyItemRangeChanged(position, list_file_infos.size());
            }
        });
        holder.part_mini_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Delete -> " + list_file_infos.get(position).getF_name(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_file_infos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView part_name, part_type, part_size;
        TextView part_mini_upload, part_mini_remove, part_mini_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            part_name = itemView.findViewById(R.id.f_info_name);
            part_type = itemView.findViewById(R.id.f_info_ext);
            part_size = itemView.findViewById(R.id.f_info_size);

            part_mini_upload = itemView.findViewById(R.id.f_info_upload);
            part_mini_remove = itemView.findViewById(R.id.f_info_remove);
            part_mini_delete = itemView.findViewById(R.id.f_info_delete);
        }
    }
}
