package com.niccher.pctophonecopier.adapters;

import static com.niccher.pctophonecopier.utils.Helpers.humanReadableByteCountBin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.model.Mod_List_File_Uploaded;

import java.util.ArrayList;

public class Adapter_Uploaded_Files extends RecyclerView.Adapter<Adapter_Uploaded_Files.ViewHolder> {

    ArrayList<Mod_List_File_Uploaded> list_file_infos;
    Context context;

    public Adapter_Uploaded_Files(ArrayList<Mod_List_File_Uploaded> list_file_infos, Context context) {
        this.list_file_infos = list_file_infos;
        this.context = context;
    }

    @NonNull
    @Override
    public Adapter_Uploaded_Files.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_uploaded_file_info, parent, false);
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

        holder.part_mini_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Downloading -> " + list_file_infos.get(position).getUp_file_Name(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_file_infos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView part_name, part_type, part_ext, part_size, part_date;
        TextView part_mini_download;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            part_name = itemView.findViewById(R.id.f_info_name);
            part_type = itemView.findViewById(R.id.f_info_type);
            part_ext = itemView.findViewById(R.id.f_info_ext);
            part_size = itemView.findViewById(R.id.f_info_size);
            part_date = itemView.findViewById(R.id.f_info_date);

            part_mini_download = itemView.findViewById(R.id.f_info_download);
        }
    }
}
