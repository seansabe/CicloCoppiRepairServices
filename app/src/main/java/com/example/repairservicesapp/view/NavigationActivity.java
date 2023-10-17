package com.example.repairservicesapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.repairservicesapp.R;
import com.example.repairservicesapp.databinding.ActivityMainBinding;
import com.example.repairservicesapp.util.StatusBarUtils;
import com.example.repairservicesapp.view.fragments.ProfileFragment;
import com.example.repairservicesapp.view.fragments.ScheduleFragment;
import com.example.repairservicesapp.view.fragments.ServiceHistoryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Custom status and nav bar
        StatusBarUtils.setStatusBarColor(getWindow(), ContextCompat.getColor(this, R.color.white), ContextCompat.getColor(this, R.color.blue));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        loadUI();
        loadEvents();
    }

    private void loadUI() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        loadFragment(new ScheduleFragment());
    }

    private void loadEvents() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.itemSchedule) {
                loadFragment(new ScheduleFragment());
            } else if (itemId == R.id.itemHistory) {
                loadFragment(new ServiceHistoryFragment());
            } else if (itemId == R.id.itemProfile) {
                loadFragment(new ProfileFragment());
            }
            return true;
        });

        bottomNavigationView.setOnItemReselectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.itemSchedule) {
                loadFragment(new ScheduleFragment());
            } else if (itemId == R.id.itemHistory) {
                loadFragment(new ServiceHistoryFragment());
            } else if (itemId == R.id.itemProfile) {
                loadFragment(new ProfileFragment());
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .commit();
    }
}