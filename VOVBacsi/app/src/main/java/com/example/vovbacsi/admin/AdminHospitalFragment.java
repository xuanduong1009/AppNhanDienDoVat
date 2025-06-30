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

import com.example.vovbacsi.item.HospitalItem;
import com.example.vovbacsi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.UUID;

public class AdminHospitalFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Button addButton;
    private EditText hospitalNameEditText;
    private Uri imageUri;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    public AdminHospitalFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_hospital, container, false);

        if (!isAdminUser()) {
            Toast.makeText(getActivity(), "Bạn không có quyền truy cập!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return view;
        }

        initializeUI(view);
        initializeFirebase();

        imageView.setOnClickListener(v -> openImageChooser());
        addButton.setOnClickListener(v -> addHospitalToFirebase());

        return view;
    }

    private boolean isAdminUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            db = FirebaseFirestore.getInstance();
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String role = documentSnapshot.getString("role");
                        if (!"admin".equals(role)) {
                            Toast.makeText(getActivity(), "Bạn không có quyền truy cập!", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Lỗi khi kiểm tra quyền!", Toast.LENGTH_SHORT).show());
        }
        return true; // Phải dễ dàng để điều kiện có khả năng sai
    }

    private void initializeUI(View view) {
        imageView = view.findViewById(R.id.image_view);
        addButton = view.findViewById(R.id.save_button);
        hospitalNameEditText = view.findViewById(R.id.hospital_name_edit_text);
    }

    private void initializeFirebase() {
        storageReference = FirebaseStorage.getInstance().getReference("hospital_images");
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            setImageView();
        }
    }

    private void setImageView() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Không thể lấy ảnh!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addHospitalToFirebase() {
        String hospitalName = hospitalNameEditText.getText().toString().trim();
        if (imageUri != null && !hospitalName.isEmpty()) {
            uploadImageToFirebase(hospitalName);
        } else {
            Toast.makeText(getActivity(), "Vui lòng nhập tên và chọn ảnh!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebase(String hospitalName) {
        String imageId = UUID.randomUUID().toString();
        StorageReference fileReference = storageReference.child(imageId);
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            saveHospitalToDatabase(hospitalName, uri.toString());
                        }))
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Không thể tải ảnh!", Toast.LENGTH_SHORT).show());
    }

    private void saveHospitalToDatabase(String hospitalName, String imageUrl) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("hospital_items");
        String itemId = databaseReference.push().getKey();
        HospitalItem hospitalItem = new HospitalItem(itemId, hospitalName, imageUrl);
        databaseReference.child(itemId).setValue(hospitalItem)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Đã thêm bệnh viện thành công!", Toast.LENGTH_SHORT).show();
                        resetInputFields();
                    } else {
                        Toast.makeText(getActivity(), "Không thể lưu vào cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resetInputFields() {
        hospitalNameEditText.setText("");
        imageView.setImageResource(android.R.color.darker_gray);
        imageUri = null;
    }
}
