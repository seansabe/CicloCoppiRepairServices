package com.example.repairservicesapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.repairservicesapp.R;
import com.example.repairservicesapp.app.AppManager;
import com.example.repairservicesapp.database.DatabaseHelper;
import com.example.repairservicesapp.model.User;
import com.example.repairservicesapp.util.StatusBarUtils;

public class RegistrationActivity extends AppCompatActivity {
    User user;
    DatabaseHelper dbHelper;
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
        btnRegister.setOnClickListener(v -> {
            if (checkTextUtils()) {
                String fName = editFirstName.getText().toString();
                String lName = editLastName.getText().toString();
                String address = editAddress.getText().toString();
                String phone = editPhone.getText().toString();
                String email = editRegEmail.getText().toString();
                String password = editRegPass.getText().toString();

                dbHelper = new DatabaseHelper(getApplicationContext());
                try {
                    user = dbHelper.getUserByEmail(email);
                    // Check if the user already exists
                    if (user != null) {
                        txtErrorAccountExists.setVisibility(TextView.VISIBLE);
                    } else {
                        txtErrorAccountExists.setVisibility(TextView.GONE);
                        // Adding a new user
                        User newUser = new User(fName,lName,address,phone,email,password, User.UserType.CUSTOMER);
                        dbHelper = new DatabaseHelper(getApplicationContext());
                        dbHelper.addUser(newUser);

                        startActivity(new Intent(RegistrationActivity.this, NavigationActivity.class));
                        AppManager.instance.setUser(newUser);
                        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("email", newUser.email);
                        editor.putString("password", newUser.password);
                        editor.apply();
                        finish();
                    }
                }
                catch(Exception e) { e.printStackTrace(); }
            }
        });
        btnCancel.setOnClickListener(v -> finish());
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