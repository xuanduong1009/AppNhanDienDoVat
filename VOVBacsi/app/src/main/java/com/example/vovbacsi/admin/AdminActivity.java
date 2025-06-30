package com.example.vovbacsi.admin;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.vovbacsi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class AdminActivity extends AppCompatActivity {

    // SparseArray để ánh xạ các mục của BottomNavigationView với các Fragment tương ứng
    private final SparseArray<Fragment> fragmentMap = new SparseArray<>();
    private GridView categoryGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Ánh xạ các Fragment với các mục trong BottomNavigationView
        fragmentMap.put(R.id.nav_admin_group, new AdminGroupFragment());
        fragmentMap.put(R.id.nav_admin_hospital, new AdminHospitalFragment());
        fragmentMap.put(R.id.nav_admin_activity, new AdminActivityFragment());
        fragmentMap.put(R.id.nav_admin_personal, new AdminPersonalFragment());

        // Khởi tạo GridView cho các danh mục
        categoryGrid = findViewById(R.id.adminCategoryGrid);

        // Cài đặt padding cho layout chính dựa trên kích thước của hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Cài đặt menu dưới cùng và GridView
        setupBottomMenu();
        setupCategoryGrid();
    }

    private NavigationBarView.OnItemSelectedListener onItemSelectedListener() {
        return new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Lấy Fragment từ SparseArray dựa trên id của item
                Fragment selectedFragment = fragmentMap.get(item.getItemId());

                if (selectedFragment != null) {
                    setFragment(selectedFragment);
                }
                return true;
            }
        };
    }

    private void setupCategoryGrid() {
        categoryGrid.setOnItemClickListener((parent, view, position, id) -> {
            Toast.makeText(AdminActivity.this, "Chọn mục " + position, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupBottomMenu() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener());

        // Mặc định hiển thị fragment nhóm
        setFragment(fragmentMap.get(R.id.nav_admin_group));
    }

    private void setFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.admin_fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
