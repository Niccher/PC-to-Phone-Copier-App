package com.niccher.pctophonecopier.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.model.Mod_File_info;
import com.niccher.pctophonecopier.model.Mod_File_rando;
import com.niccher.pctophonecopier.utils.Helpers;
import com.niccher.pctophonecopier.utils.Konstants;

import java.util.ArrayList;

public class Adapter_Rando extends RecyclerView.Adapter<Adapter_Rando.ViewHolder> {

    ArrayList<Mod_File_rando> list_file_infos;
    Context context;

    Konstants kon;
    Helpers helpers = null;


    public Adapter_Rando(ArrayList<Mod_File_rando> list_file_infos, Context context) {
        this.list_file_infos = list_file_infos;
        this.context = context;

        kon = new Konstants();
        helpers = new Helpers();
    }

    @NonNull
    @Override
    public Adapter_Rando.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_info, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Rando.ViewHolder holder, int position) {
        holder.part_name.setText(list_file_infos.get(position).getF_name());
        holder.part_type.setText(list_file_infos.get(position).getF_type());
        holder.part_size.setText(list_file_infos.get(position).getF_size());

        holder.part_mini_remove.setVisibility(View.VISIBLE);
        holder.part_mini_delete.setVisibility(View.GONE);
        holder.part_mini_progress.setVisibility(View.INVISIBLE);

        holder.part_mini_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Uploading -> " + list_file_infos.get(position).getF_name(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.part_mini_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Removing " + list_file_infos.get(position).getF_name(), Toast.LENGTH_SHORT).show();
                list_file_infos.remove(position);
                notifyDataSetChanged();
                notifyItemChanged(position);
                //notifyItemRangeChanged(position, list_file_infos.size());
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
        ProgressBar part_mini_progress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            part_name = itemView.findViewById(R.id.f_info_name);
            part_type = itemView.findViewById(R.id.f_info_ext);
            part_size = itemView.findViewById(R.id.f_info_size);

            part_mini_upload = itemView.findViewById(R.id.f_info_upload);
            part_mini_remove = itemView.findViewById(R.id.f_info_remove);
            part_mini_delete = itemView.findViewById(R.id.f_info_delete);

            part_mini_progress = itemView.findViewById(R.id.f_info_progress);
        }
    }
}
