package com.example.vovbacsi;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userId; // UID của người dùng trong Firebase
    private String phone;   // Số điện thoại của người dùng
    private String role;    // Vai trò của người dùng, ví dụ: "user" hoặc "admin"

    // Constructor
    public User(String userId, String phone) {
        this.userId = userId;
        this.phone = phone;
        this.role = "user"; // Mặc định là "user", có thể thay đổi nếu cần
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setRole(String role) {
        this.role = role;
    }

    // Chuyển đổi đối tượng thành HashMap để lưu vào Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("phone", phone);
        map.put("role", role);
        return map;
    }
}
