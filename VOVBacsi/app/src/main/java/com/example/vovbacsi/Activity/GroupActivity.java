package com.example.vovbacsi.Activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vovbacsi.item.GroupItem;
import com.example.vovbacsi.databinding.ActivityGroupBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {
    private ActivityGroupBinding binding;
    private List<GroupItem> groupItemList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupItemList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("group_items");
        loadGroupItems();
    }

    private void loadGroupItems() {
    }


}
