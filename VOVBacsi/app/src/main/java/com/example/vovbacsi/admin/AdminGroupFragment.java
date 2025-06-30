package com.example.vovbacsi.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vovbacsi.R;
import com.example.vovbacsi.item.GroupItem;
import com.example.vovbacsi.Adapter.GroupItemAdapter;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class AdminGroupFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Button addButton;
    private EditText diseaseNameEditText;
    private Uri imageUri;
    private StorageReference storageReference;
    private RecyclerView recyclerView;
    private GroupItemAdapter adapter;
    private ArrayList<GroupItem> groupItemList;
    private DatabaseReference databaseReference;

    public AdminGroupFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_group, container, false);

        imageView = view.findViewById(R.id.image_view);
        addButton = view.findViewById(R.id.add_button);
        diseaseNameEditText = view.findViewById(R.id.disease_name_edit_text);
        recyclerView = view.findViewById(R.id.recycler_view);

        initializeFirebase();
        initializeRecyclerView();
        loadItemsFromFirebase();

        imageView.setOnClickListener(v -> openImageChooser());
        addButton.setOnClickListener(v -> addItemToFirebase());

        return view;
    }

    private void initializeFirebase() {
        storageReference = FirebaseStorage.getInstance().getReference("group_images");
        databaseReference = FirebaseDatabase.getInstance().getReference("group_items");
    }

    private void initializeRecyclerView() {
        groupItemList = new ArrayList<>();
        adapter = new GroupItemAdapter(groupItemList, getContext(), new GroupItemAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(GroupItem groupItem) {
                editItemInFirebase(groupItem);
            }

            @Override
            public void onDeleteClick(int position) {
                deleteItemInFirebase(position);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadItemsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupItemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GroupItem item = dataSnapshot.getValue(GroupItem.class);
                    if (item != null) {
                        groupItemList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void addItemToFirebase() {
        String diseaseName = diseaseNameEditText.getText().toString();
        if (imageUri != null && !diseaseName.isEmpty()) {
            String imageName = UUID.randomUUID().toString();
            StorageReference imageRef = storageReference.child(imageName);

            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String itemId = databaseReference.push().getKey();
                        if (itemId != null) {
                            GroupItem item = new GroupItem(itemId, diseaseName, uri.toString());
                            databaseReference.child(itemId).setValue(item).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
            ).addOnFailureListener(e -> Toast.makeText(getContext(), "Thêm thất bại", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "Vui lòng chọn ảnh và nhập tên bệnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void editItemInFirebase(GroupItem groupItem) {
        diseaseNameEditText.setText(groupItem.getDiseaseName());
        imageUri = Uri.parse(groupItem.getImageUrl());
        imageView.setImageURI(imageUri);

        addButton.setOnClickListener(v -> {
            String updatedName = diseaseNameEditText.getText().toString();
            if (!updatedName.isEmpty()) {
                groupItem.setDiseaseName(updatedName);
                databaseReference.child(groupItem.getItemId()).setValue(groupItem).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Sửa thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Sửa thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void deleteItemInFirebase(int position) {
        GroupItem item = groupItemList.get(position);
        // Lấy tham chiếu đến hình ảnh đã lưu trên Firebase Storage
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(item.getImageUrl());

        // Xóa hình ảnh khỏi Firebase Storage
        imageRef.delete().addOnSuccessListener(aVoid -> {
            // Xóa dữ liệu nhóm khỏi Firebase Realtime Database
            databaseReference.child(item.getItemId()).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Cập nhật lại danh sách trong RecyclerView sau khi xóa
                    groupItemList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Lỗi khi xóa dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Xóa thất bại do lỗi hình ảnh", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
