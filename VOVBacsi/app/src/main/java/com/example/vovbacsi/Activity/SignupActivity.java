package com.example.vovbacsi.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vovbacsi.LoginActivity;
import com.example.vovbacsi.R;
import com.example.vovbacsi.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    private EditText phoneNumberEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private CheckBox termsCheckbox;
    private Button signupButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Ánh xạ các view
        phoneNumberEditText = findViewById(R.id.editTextPhone);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        termsCheckbox = findViewById(R.id.checkBox);
        signupButton = findViewById(R.id.buttonRegister);

        // Khởi tạo Firebase Auth và Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Xử lý sự kiện cho nút đăng ký
        signupButton.setOnClickListener(v -> registerUser());

        // Tìm login_link và thiết lập sự kiện OnClickListener
        TextView loginLink = findViewById(R.id.login_link);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển qua LoginActivity khi bấm vào
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Kết thúc SignupActivity để không quay lại màn hình này khi nhấn back
            }
        });

        // Tìm terms_link và thiết lập sự kiện OnClickListener
        TextView termsLink = findViewById(R.id.terms_link);
        termsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển qua TermsActivity khi bấm vào
                Intent intent = new Intent(SignupActivity.this, TermsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Kiểm tra thông tin đăng ký
        if (phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Vui lòng nhập số điện thoại và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignupActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!termsCheckbox.isChecked()) {
            Toast.makeText(SignupActivity.this, "Bạn cần đồng ý với điều khoản sử dụng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo tài khoản với số điện thoại và mật khẩu
        mAuth.createUserWithEmailAndPassword(phoneNumber + "@gmail.com", password)
                .addOnCompleteListener(SignupActivity.this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();

                        // Tạo một đối tượng User để lưu vào Firestore
                        User userInfo = new User(uid, phoneNumber);

                        // Lưu vào Firestore
                        db.collection("users").document(uid)
                                .set(userInfo.toMap()) // Sử dụng phương thức toMap()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(SignupActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SignupActivity.this, "Lỗi lưu thông tin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(SignupActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
