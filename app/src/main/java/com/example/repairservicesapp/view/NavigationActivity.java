package com.example.repairservicesapp.view;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.repairservicesapp.R;
import com.example.repairservicesapp.app.AppManager;
import com.example.repairservicesapp.database.DatabaseHelper;
import com.example.repairservicesapp.database.FirebaseUtils;
import com.example.repairservicesapp.model.User;
import com.example.repairservicesapp.util.StatusBarUtils;
import com.example.repairservicesapp.view.fragments.AdminFragment;
import com.example.repairservicesapp.view.fragments.BookingFragment;
import com.example.repairservicesapp.view.fragments.ProfileFragment;
import com.example.repairservicesapp.view.fragments.ScheduleFragment;
import com.example.repairservicesapp.view.fragments.ServiceHistoryCustomerFragment;
import com.example.repairservicesapp.view.fragments.ServiceHistoryTechnicianFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.messaging.FirebaseMessaging;

public class NavigationActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Custom status and nav bar
        StatusBarUtils.setStatusBarColor(getWindow(), ContextCompat.getColor(this, R.color.white), ContextCompat.getColor(this, R.color.blue));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        loadUI();
        //getToken();
        User user = AppManager.instance.user;
        if (user.isAdmin()) {
            loadAdminEvents();
        } else if (user.isCustomer()) {
            loadCustomerEvents();
        } else if (user.isTechnician()) {
            loadTechnicianEvents();
        }
    }

    private void loadUI() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().clear();
        if (AppManager.instance.user.isAdmin()) {
            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu_admin);
            loadFragment(new AdminFragment());
        } else if (AppManager.instance.user.isCustomer()) {
            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu_customer);
            loadFragment(new BookingFragment());
        } else if (AppManager.instance.user.isTechnician()) {
            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu_technician);
            loadFragment(new ScheduleFragment());
        }
    }

    // Load events for admin
    private void loadAdminEvents() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.itemAdminHome) {
                loadFragment(new AdminFragment());
            } else if (itemId == R.id.itemAdminHistory) {
                loadFragment(new ServiceHistoryTechnicianFragment());
            } else if (itemId == R.id.itemAdminProfile) {
                loadFragment(new ProfileFragment());
            }
            return true;
        });

        bottomNavigationView.setOnItemReselectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.itemAdminHome) {
                loadFragment(new AdminFragment());
            } else if (itemId == R.id.itemAdminHistory) {
                loadFragment(new ServiceHistoryTechnicianFragment());
            } else if (itemId == R.id.itemAdminProfile) {
                loadFragment(new ProfileFragment());
            }
        });
    }

    // Load events for customer
    private void loadCustomerEvents() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.itemCustomerBooking) {
                loadFragment(new BookingFragment());
            } else if (itemId == R.id.itemCustomerHistory) {
                loadFragment(new ServiceHistoryCustomerFragment());
            } else if (itemId == R.id.itemCustomerProfile) {
                loadFragment(new ProfileFragment());
            }
            return true;
        });

        bottomNavigationView.setOnItemReselectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.itemCustomerBooking) {
                loadFragment(new BookingFragment());
            } else if (itemId == R.id.itemCustomerHistory) {
                loadFragment(new ServiceHistoryCustomerFragment());
            } else if (itemId == R.id.itemCustomerProfile) {
                loadFragment(new ProfileFragment());
            }
        });
    }

    // Load events for technician
    private void loadTechnicianEvents() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.itemTechnicianSchedule) {
                loadFragment(new ScheduleFragment());
            } else if (itemId == R.id.itemTechnicianHistory) {
                loadFragment(new ServiceHistoryTechnicianFragment());
            } else if (itemId == R.id.itemTechnicianProfile) {
                loadFragment(new ProfileFragment());
            }
            return true;
        });

        bottomNavigationView.setOnItemReselectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.itemTechnicianSchedule) {
                loadFragment(new ScheduleFragment());
            } else if (itemId == R.id.itemTechnicianHistory) {
                loadFragment(new ServiceHistoryTechnicianFragment());
            } else if (itemId == R.id.itemTechnicianProfile) {
                loadFragment(new ProfileFragment());
            }
        });
    }

    // Load fragment
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .commit();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                return;
            }
            String token = task.getResult();
            Log.d("TOKEN", token);
            AppManager.instance.user.setToken(token);
            FirebaseUtils.INSTANCE.getFirestore().collection("users")
                    .document(AppManager.instance.user.getUserId())
                    .update("token", token, "timestamp", Timestamp.now());
        });
    }
}