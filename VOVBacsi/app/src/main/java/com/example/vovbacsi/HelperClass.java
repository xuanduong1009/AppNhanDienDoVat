package com.example.vovbacsi;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.vovbacsi.User;


public class HelperClass {
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    public HelperClass() {
        // Khởi tạo FirebaseAuth và FirebaseDatabase
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    }

    // Hàm đăng ký người dùng
    public void registerUser(String phone, String password, Context context) {
        auth.createUserWithEmailAndPassword(phone + "@example.com", password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Thêm người dùng vào database
                            User newUser = new User(user.getUid(), phone);
                            databaseReference.child(user.getUid()).setValue(newUser)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Log.e("Firebase", "Error: " + e.getMessage()));
                        }
                    } else {
                        Toast.makeText(context, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Hàm đăng nhập người dùng
    public void loginUser(String phone, String password, Context context) {
        auth.signInWithEmailAndPassword(phone + "@example.com", password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Hàm kiểm tra trạng thái đăng nhập
    public FirebaseUser checkUserLoggedIn() {
        return auth.getCurrentUser();
    }
}
