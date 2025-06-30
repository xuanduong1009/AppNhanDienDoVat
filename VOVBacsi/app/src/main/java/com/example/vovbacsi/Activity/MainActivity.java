package com.example.vovbacsi.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.vovbacsi.LoginActivity;
import com.example.vovbacsi.R;
import com.example.vovbacsi.fragment.ActivityFragment;
import com.example.vovbacsi.fragment.GroupFragment;
import com.example.vovbacsi.fragment.HospitalFragment;
import com.example.vovbacsi.fragment.PersonalFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private final SparseArray<Fragment> fragmentMap = new SparseArray<>();
    private FirebaseAuth mAuth;

    private NavigationBarView.OnItemSelectedListener onItemSelectedListener() {
        return new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_activity) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser == null) {
                        // Hiển thị hộp thoại yêu cầu đăng nhập
                        showLoginDialog();
                        return false;
                    }
                }

                Fragment selectedFragment = fragmentMap.get(itemId);
                if (selectedFragment != null) {
                    setFragment(selectedFragment);
                }
                return true;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        fragmentMap.put(R.id.nav_group, new GroupFragment());
        fragmentMap.put(R.id.nav_hospital, new HospitalFragment());
        fragmentMap.put(R.id.nav_activity, new ActivityFragment());
        fragmentMap.put(R.id.nav_personal, new PersonalFragment());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupBottomMenu();
    }

    private void showLoginDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Đăng nhập để thực hiện chức năng này")
                .setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Chuyển đến màn hình đăng nhập
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_group) {
            Toast.makeText(this, "Chọn Nhóm bệnh", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBottomMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener());
        setFragment(fragmentMap.get(R.id.nav_group));
    }

    private void setFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
