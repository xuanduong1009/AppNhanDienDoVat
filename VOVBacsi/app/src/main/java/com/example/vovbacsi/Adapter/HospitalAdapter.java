package com.example.vovbacsi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vovbacsi.item.HospitalItem;
import com.example.vovbacsi.R;

import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {

    private final Context context;
    private final List<HospitalItem> hospitalItems;

    public HospitalAdapter(Context context, List<HospitalItem> hospitalItems) {
        this.context = context;
        this.hospitalItems = hospitalItems;
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hospital, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        HospitalItem hospitalItem = hospitalItems.get(position);
        holder.hospitalName.setText(hospitalItem.getName());
        Glide.with(context).load(hospitalItem.getImageUrl()).into(holder.hospitalImage);
    }

    @Override
    public int getItemCount() {
        return hospitalItems.size();
    }

    static class HospitalViewHolder extends RecyclerView.ViewHolder {
        TextView hospitalName;
        ImageView hospitalImage;

        HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.hospital_name);
            hospitalImage = itemView.findViewById(R.id.hospital_image);
        }
    }
}
