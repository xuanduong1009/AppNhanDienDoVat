package com.example.vovbacsi.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.vovbacsi.R;
import com.example.vovbacsi.ChangePasswordActivity;
import com.example.vovbacsi.LoginActivity;

public class PersonalFragment extends Fragment {

    private LinearLayout dangXuatLayout, doiMatKhauLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout cho fragment
        return inflater.inflate(R.layout.fragment_personal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo các LinearLayout cho Đăng xuất và Đổi mật khẩu
        dangXuatLayout = view.findViewById(R.id.dang_xuat);
        doiMatKhauLayout = view.findViewById(R.id.doi_mat_khau);

        // Xử lý sự kiện Đăng xuất
        dangXuatLayout.setOnClickListener(v -> {
            // Đăng xuất và chuyển đến màn hình đăng nhập
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        // Xử lý sự kiện Đổi mật khẩu
        doiMatKhauLayout.setOnClickListener(v -> {
            // Chuyển đến màn hình Đổi mật khẩu
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });
    }
}
