package com.example.repairservicesapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.repairservicesapp.R;
import com.example.repairservicesapp.app.AppManager;
import com.example.repairservicesapp.database.FirebaseUtils;
import com.example.repairservicesapp.model.User;
import com.example.repairservicesapp.util.MapUtils;
import com.example.repairservicesapp.util.StatusBarUtils;

public class RegistrationActivity extends AppCompatActivity {
    User user;
    EditText editFirstName, editLastName, editAddress, editPhone, editRegEmail, editRegPass, editRegPassConf;
    Button btnRegister, btnCancel;
    TextView txtErrorAccountExists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Custom status and nav bar
        StatusBarUtils.setStatusBarColor(getWindow(), ContextCompat.getColor(this, R.color.white), ContextCompat.getColor(this, R.color.white));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        loadUI();
        loadEvents();
    }

    private void loadUI() {
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editAddress = findViewById(R.id.editAddress);
        editPhone = findViewById(R.id.editPhone);
        editRegEmail = findViewById(R.id.editRegEmail);
        editRegPass = findViewById(R.id.editRegPass);
        editRegPassConf = findViewById(R.id.editRegPassConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        btnCancel = findViewById(R.id.btnCancel);
        txtErrorAccountExists = findViewById(R.id.txtErrorAccountExists);
    }

    private void loadEvents() {
        btnRegister.setOnClickListener(v -> register());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void register() {
        String fName = editFirstName.getText().toString();
        String lName = editLastName.getText().toString();
        String address = editAddress.getText().toString();
        String phone = editPhone.getText().toString();
        String email = editRegEmail.getText().toString();
        String password = editRegPass.getText().toString();

        if (checkTextUtils()) {
            // Check if user doesn't already exist
            FirebaseUtils.INSTANCE.getFirestore().collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                // Adding a new user
                                txtErrorAccountExists.setVisibility(TextView.GONE);
                                user = new User(fName, lName, address, phone, email, password, User.UserType.CUSTOMER, null, 100);
                                FirebaseUtils.INSTANCE.getFirestore().collection("users")
                                        .add(MapUtils.INSTANCE.userToMap(user))
                                        .addOnSuccessListener(documentReference -> {
                                            user.setUserId(documentReference.getId());
                                            AppManager.instance.setUser(user);
                                            Log.d("RegistrationActivity", "documentReference: " + AppManager.instance.user.getUserId());
                                            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("email", user.email);
                                            editor.putString("password", user.password);
                                            editor.apply();
                                            startActivity(new Intent(RegistrationActivity.this, NavigationActivity.class));
                                            finish();
                                            // Set userId inside Firebase document based on AppManager userId
                                            FirebaseUtils.INSTANCE.setUserId(AppManager.instance.user.getUserId());
                                        })
                                        .addOnFailureListener(e -> Log.d("RegistrationActivity", "register error: " + e.getMessage()));
                            } else {
                                txtErrorAccountExists.setVisibility(TextView.VISIBLE);
                            }
                        }
                    });
        }
    }

    private boolean checkTextUtils() {
        boolean isValid = true;
        String password, passwordConf;
        if (TextUtils.isEmpty(editFirstName.getText().toString())) {
            editFirstName.setError(getString(R.string.txtErrorEmptyFirstName));
            isValid = false;
        }
        if (TextUtils.isEmpty(editLastName.getText().toString())) {
            editLastName.setError(getString(R.string.txtErrorEmptyLastName));
            isValid = false;
        }
        if (TextUtils.isEmpty(editAddress.getText().toString())) {
            editAddress.setError(getString(R.string.txtErrorEmptyAddress));
            isValid = false;
        }
        if (TextUtils.isEmpty(editPhone.getText().toString())) {
            editPhone.setError(getString(R.string.txtErrorEmptyPhone));
            isValid = false;
        }
        if (TextUtils.isEmpty(editRegEmail.getText().toString())) {
            editRegEmail.setError(getString(R.string.txtErrorEmptyEmail));
            isValid = false;
        }
        if (TextUtils.isEmpty(editRegPass.getText().toString())) {
            editRegPass.setError(getString(R.string.txtErrorEmptyPassword));
            isValid = false;
        }
        if (TextUtils.isEmpty(editRegPassConf.getText().toString())) {
            editRegPassConf.setError(getString(R.string.txtErrorEmptyConfirmPassword));
            isValid = false;
        }
        if (isValid) {
            password = editRegPass.getText().toString();
            passwordConf = editRegPassConf.getText().toString();
            if (!password.equals(passwordConf)) {
                editRegPassConf.setError(getString(R.string.txtErrorPassMismatch));
                isValid = false;
                editRegPass.setText(null);
                editRegPass.clearFocus();
                editRegPassConf.setText(null);
                editRegPassConf.clearFocus();
            }
        }
        return isValid;
    }
}