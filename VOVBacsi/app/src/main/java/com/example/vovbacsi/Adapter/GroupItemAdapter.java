package com.example.vovbacsi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.vovbacsi.R;
import com.example.vovbacsi.item.GroupItem;
import java.util.List;

public class GroupItemAdapter extends RecyclerView.Adapter<GroupItemAdapter.GroupItemViewHolder> {

    private List<GroupItem> groupItemList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(GroupItem groupItem);
        void onDeleteClick(int position);
    }

    public GroupItemAdapter(List<GroupItem> groupItemList, Context context, OnItemClickListener listener) {
        this.groupItemList = groupItemList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupItemViewHolder holder, int position) {
        GroupItem groupItem = groupItemList.get(position);
        holder.title.setText(groupItem.getDiseaseName());
        Glide.with(context).load(groupItem.getImageUrl()).into(holder.imageView);

        holder.editButton.setOnClickListener(v -> listener.onEditClick(groupItem));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return groupItemList.size();
    }

    public static class GroupItemViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imageView;
        Button editButton, deleteButton;

        public GroupItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.group_title);
            imageView = itemView.findViewById(R.id.group_image);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
