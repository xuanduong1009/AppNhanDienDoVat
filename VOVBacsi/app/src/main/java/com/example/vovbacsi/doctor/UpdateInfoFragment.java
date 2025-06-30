package com.example.vovbacsi.doctor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.vovbacsi.Doctor;
import com.example.vovbacsi.R;
import com.example.vovbacsi.item.GroupItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UpdateInfoFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView doctorProfileImage;
    private Button selectImageButton, updateButton;
    private EditText nameInput, specializationInput, degreeInput, birthYearInput, unitInput, specialtyInput, treatableDiseasesInput, priceInput;
    private AutoCompleteTextView departmentInput, specializationCategoryInput;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri imageUri;

    private ArrayList<String> departments, specializationCategories;
    private ArrayAdapter<String> departmentAdapter, specializationCategoryAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_info, container, false);

        // Gắn View
        doctorProfileImage = view.findViewById(R.id.doctor_profileImage);
        selectImageButton = view.findViewById(R.id.doctor_selectImageButton);
        updateButton = view.findViewById(R.id.doctor_updateButton);
        nameInput = view.findViewById(R.id.doctor_nameInput);
        specializationInput = view.findViewById(R.id.doctor_specializationInput);
        degreeInput = view.findViewById(R.id.doctor_degreeInput);
        birthYearInput = view.findViewById(R.id.doctor_birthYearInput);
        departmentInput = view.findViewById(R.id.doctor_departmentInput);
        specializationCategoryInput = view.findViewById(R.id.doctor_specializationCategoryInput);
        unitInput = view.findViewById(R.id.doctor_unitInput);
        specialtyInput = view.findViewById(R.id.doctor_specialtyInput);
        treatableDiseasesInput = view.findViewById(R.id.doctor_treatableDiseasesInput);
        priceInput = view.findViewById(R.id.doctor_priceInput);

        databaseReference = FirebaseDatabase.getInstance().getReference("doctors");
        storageReference = FirebaseStorage.getInstance().getReference("doctor_images");

        // Khởi tạo danh sách
        specializationCategories = new ArrayList<>();
        specializationCategories.add("Nội khoa");
        specializationCategories.add("Ngoại khoa");
        specializationCategoryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, specializationCategories);
        specializationCategoryInput.setAdapter(specializationCategoryAdapter);

        departments = new ArrayList<>();
        departmentAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, departments);
        departmentInput.setAdapter(departmentAdapter);
        loadDepartmentsFromFirebase();

        selectImageButton.setOnClickListener(v -> openImageChooser());
        updateButton.setOnClickListener(v -> updateDoctorInfo());

        return view;
    }

    private void loadDepartmentsFromFirebase() {
        DatabaseReference departmentRef = FirebaseDatabase.getInstance().getReference("group_items");
        departmentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                departments.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    GroupItem item = dataSnapshot.getValue(GroupItem.class);
                    if (item != null) {
                        departments.add(item.getDiseaseName());
                    }
                }
                departmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getActivity(), "Lỗi khi tải danh sách khoa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                doctorProfileImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateDoctorInfo() {
        String name = nameInput.getText().toString();
        String specialization = specializationInput.getText().toString();
        String category = specializationCategoryInput.getText().toString();
        String degree = degreeInput.getText().toString();
        String birthYear = birthYearInput.getText().toString();
        String department = departmentInput.getText().toString();
        String unit = unitInput.getText().toString();
        String specialty = specialtyInput.getText().toString();
        String treatableDiseases = treatableDiseasesInput.getText().toString();
        String priceText = priceInput.getText().toString();
        double price = priceText.isEmpty() ? 0.0 : Double.parseDouble(priceText);

        if (name.isEmpty() || specialization.isEmpty() || degree.isEmpty() || birthYear.isEmpty()) {
            Toast.makeText(getActivity(), "Hãy điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : null;

        if (userId != null) {
            Doctor doctor = new Doctor(name, specialization, degree, birthYear, department,
                    unit, specialty, treatableDiseases, price, "");

            databaseReference.child(userId).setValue(doctor).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                }
            });

            if (imageUri != null) {
                uploadImage(userId); // Upload ảnh vào vị trí tương ứng với userId
            }
        }
    }

    private void uploadImage(String userId) {
        StorageReference fileRef = storageReference.child(userId + ".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri ->
                        databaseReference.child(userId).child("imageUrl").setValue(uri.toString())
                ));
    }

    private void loadDoctorInfo() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : null;

        if (userId != null) {
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Doctor doctor = dataSnapshot.getValue(Doctor.class);
                    if (doctor != null) {
                        nameInput.setText(doctor.getName());
                        specializationInput.setText(doctor.getSpecialization());
                        degreeInput.setText(doctor.getDegree());
                        birthYearInput.setText(doctor.getBirthYear());
                        departmentInput.setText(doctor.getDepartment());
                        unitInput.setText(doctor.getUnit());
                        specialtyInput.setText(doctor.getSpecialty());
                        treatableDiseasesInput.setText(doctor.getTreatableDiseases());
                        priceInput.setText(String.valueOf(doctor.getPrice()));

                        // Load image URL if available
                        if (doctor.getImageUrl() != null && !doctor.getImageUrl().isEmpty()) {
                            Glide.with(getActivity()).load(doctor.getImageUrl()).into(doctorProfileImage);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Lỗi khi tải thông tin bác sĩ", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDoctorInfo(); // Tải thông tin bác sĩ khi màn hình được mở lại
    }
}
