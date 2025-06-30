package com.example.vovbacsi.doctor;

import android.os.Bundle;
import android.util.SparseArray;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.vovbacsi.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DoctorActivity extends AppCompatActivity {

    private final SparseArray<Fragment> fragmentMap = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        // Add doctor-related fragments to SparseArray

        fragmentMap.put(R.id.nav_notification_doctor, new NotificationFragment());
        fragmentMap.put(R.id.nav_profile_doctor, new UpdateInfoFragment());
        fragmentMap.put(R.id.nav_setting_doctor, new SettingFragment());

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up bottom navigation view
        BottomNavigationView bottomNavigationView = findViewById(R.id.doctor_bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener());


    }

    private NavigationBarView.OnItemSelectedListener onItemSelectedListener() {
        return item -> {
            Fragment selectedFragment = fragmentMap.get(item.getItemId());
            if (selectedFragment != null) {
                setFragment(selectedFragment);
            }
            return true;
        };
    }

    private void setFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.commit();
    }
}
