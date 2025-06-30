package com.example.vovbacsi.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager; // Import GridLayoutManager
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.vovbacsi.R;
import com.example.vovbacsi.item.HospitalItem;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HospitalFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<HospitalItem, HospitalViewHolder> adapter;

    public HospitalFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospital, container, false);
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewHospitals);

        // Sử dụng GridLayoutManager với 2 cột
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 cột

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("hospital_items");
        FirebaseRecyclerOptions<HospitalItem> options =
                new FirebaseRecyclerOptions.Builder<HospitalItem>()
                        .setQuery(databaseReference, HospitalItem.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<HospitalItem, HospitalViewHolder>(options) {
            @NonNull
            @Override
            public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_hospital, parent, false);
                return new HospitalViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull HospitalViewHolder holder, int position, @NonNull HospitalItem model) {
                holder.hospitalName.setText(model.getName());
                Glide.with(holder.hospitalImage.getContext()).load(model.getImageUrl()).into(holder.hospitalImage);
            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    public static class HospitalViewHolder extends RecyclerView.ViewHolder {
        TextView hospitalName;
        ImageView hospitalImage;

        public HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.hospital_name);
            hospitalImage = itemView.findViewById(R.id.hospital_image);
        }
    }
}
