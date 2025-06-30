package com.example.vovbacsi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vovbacsi.item.GroupItem;
import com.example.vovbacsi.R;
import com.example.vovbacsi.DoctorListItemLayout; // Đảm bảo rằng import này đã có
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class GroupFragment extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<GroupItem, GroupViewHolder> adapter;
    private EditText searchBar;
    private DatabaseReference databaseReference;

    public GroupFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        // Khởi tạo các thành phần giao diện
        searchBar = view.findViewById(R.id.search_bar);
        recyclerView = view.findViewById(R.id.recycler_view);

        // Cấu hình RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        databaseReference = FirebaseDatabase.getInstance().getReference("group_items");

        // Thiết lập tính năng tìm kiếm
        setupSearchFunctionality();

        // Lấy dữ liệu từ Firebase và hiển thị
        setupRecyclerView();

        return view;
    }

    private void setupSearchFunctionality() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                searchDisease(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchDisease(String query) {
        Query searchQuery;
        if (query.isEmpty()) {
            // Nếu không có từ khóa tìm kiếm, hiển thị tất cả dữ liệu
            searchQuery = databaseReference;
        } else {
            // Tìm kiếm theo tên bệnh (sử dụng startAt và endAt cho tìm kiếm theo prefix)
            searchQuery = databaseReference.orderByChild("diseaseName").startAt(query).endAt(query + "\uf8ff");
        }

        setupRecyclerView(searchQuery);
    }

    private void setupRecyclerView() {
        setupRecyclerView(databaseReference); // Hiển thị tất cả dữ liệu ban đầu
    }

    private void setupRecyclerView(Query query) {
        FirebaseRecyclerOptions<GroupItem> options =
                new FirebaseRecyclerOptions.Builder<GroupItem>()
                        .setQuery(query, GroupItem.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<GroupItem, GroupViewHolder>(options) {
            @NonNull
            @Override
            public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_group2, parent, false);
                return new GroupViewHolder(itemView);
            }

            @Override
            protected void onBindViewHolder(@NonNull GroupViewHolder holder, int position, @NonNull GroupItem model) {
                holder.title.setText(model.getDiseaseName());
                Glide.with(holder.imageView.getContext()).load(model.getImageUrl()).into(holder.imageView);

                // Xử lý sự kiện click để chuyển đến activity DoctorListItemLayout
                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), DoctorListItemLayout.class);
                    intent.putExtra("disease_name", model.getDiseaseName()); // Truyền tên bệnh sang activity tiếp theo
                    startActivity(intent);
                });
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

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imageView;
        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.group_title);
            imageView = itemView.findViewById(R.id.group_image);
        }
    }
}
