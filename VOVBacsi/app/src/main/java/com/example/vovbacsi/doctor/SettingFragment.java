package com.example.vovbacsi.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vovbacsi.R;
import com.example.vovbacsi.ChangePasswordActivity;
import com.example.vovbacsi.LoginActivity;

public class SettingFragment extends Fragment {

    private LinearLayout doiMatKhauLayout, dangXuatLayout;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các LinearLayout
        doiMatKhauLayout = view.findViewById(R.id.doi_mat_khau);
        dangXuatLayout = view.findViewById(R.id.dang_xuat);

        // Xử lý sự kiện Đổi mật khẩu
        doiMatKhauLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện Đăng xuất
        dangXuatLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
}
