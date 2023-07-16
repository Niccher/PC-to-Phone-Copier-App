package com.niccher.pctophonecopier.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.niccher.pctophonecopier.R;
import com.niccher.pctophonecopier.model.Mod_File_info;

import java.util.ArrayList;

public class Adapter_Sel_Files extends RecyclerView.Adapter<Adapter_Sel_Files.ViewHolder> {

    ArrayList<Mod_File_info> products;
    Context context;

    public Adapter_Sel_Files(ArrayList<Mod_File_info> products, Context context) {
        this.products = products;
        this.context = context;
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
        holder.part_name.setText(products.get(position).getF_name());
        holder.part_type.setText(products.get(position).getF_type());
        holder.part_size.setText(products.get(position).getF_size());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView part_name, part_type, part_size;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            part_name = itemView.findViewById(R.id.f_info_name);
            part_type = itemView.findViewById(R.id.f_info_ext);
            part_size = itemView.findViewById(R.id.f_info_size);
        }
    }
}
