package com.example.vovbacsi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vovbacsi.Activity.SignupActivity;
import com.example.vovbacsi.admin.AdminActivity;
import com.example.vovbacsi.Activity.MainActivity;
import com.example.vovbacsi.doctor.DoctorActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumberEditText;
    private EditText passwordEditText;
    private Button confirmButton;
    private TextView registerLink;
    private TextView forgotPasswordLink;
    private TextView backLink;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNumberEditText = findViewById(R.id.phone_number);
        passwordEditText = findViewById(R.id.password);
        confirmButton = findViewById(R.id.btn_confirm);
        registerLink = findViewById(R.id.register_link);
        forgotPasswordLink = findViewById(R.id.forgot_password);
        backLink = findViewById(R.id.back_link);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        confirmButton.setOnClickListener(v -> loginUser());
        registerLink.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
        forgotPasswordLink.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this, "Chức năng quên mật khẩu chưa được triển khai", Toast.LENGTH_SHORT).show()
        );
        backLink.setOnClickListener(v -> finish());
    }

    private void loginUser() {
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (phoneNumber.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Vui lòng nhập số điện thoại và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(phoneNumber + "@gmail.com", password)
                .addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Kiểm tra vai trò của người dùng trong Firestore
                            db.collection("users").document(user.getUid()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String role = documentSnapshot.getString("role");
                                            Log.d("LoginActivity", "User role: " + role);

                                            // Chuyển hướng theo vai trò
                                            Intent intent = new Intent(LoginActivity.this, getRoleActivity(role));
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("LoginActivity", "Error getting user document: ", e);
                                        Toast.makeText(LoginActivity.this, "Lỗi khi truy xuất thông tin người dùng", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Log.e("LoginActivity", "Login failed: " + task.getException().getMessage());
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Hàm trả về Activity phù hợp với vai trò
    private Class<?> getRoleActivity(String role) {
        switch (role != null ? role : "") {
            case "admin":
                return AdminActivity.class;
            case "doctor":
                return DoctorActivity.class;  // Bạn cần tạo DoctorActivity
            case "user":
            default:
                return MainActivity.class;
        }
    }
}
