package com.example.repairservicesapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
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

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    EditText edTxtEmail, edTxtPassword;
    TextView txtRegLink, txtErrorMessage;
    DatabaseHelper dbHelper;
    String email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Custom status and nav bar
        StatusBarUtils.setStatusBarColor(getWindow(), ContextCompat.getColor(this, R.color.white), ContextCompat.getColor(this, R.color.white));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadUI();
        loadEvents();
    }

    public void loadUI() {
        btnLogin = findViewById(R.id.btnLogin);
        edTxtEmail = findViewById(R.id.editEmail);
        edTxtPassword = findViewById(R.id.editPassword);
        txtRegLink = findViewById(R.id.txtRegisterLink);
        txtErrorMessage = findViewById(R.id.txtErrorMessage);

        // AUTO LOGIN USING SHARED PREFERENCES
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        email = preferences.getString("email", "");
        password = preferences.getString("password", "");

        if (!email.equals("") && !password.equals("")) {
            dbHelper = new DatabaseHelper(getApplicationContext());
            User user = dbHelper.getUserByEmail(email);
            AppManager.instance.setUser(user);
            startActivity(new Intent(LoginActivity.this, NavigationActivity.class));
            finish();
        }
    }

    public void loadEvents() {
        // Login event
        btnLogin.setOnClickListener(v -> {
            String email = this.edTxtEmail.getText().toString().trim();
            String password = this.edTxtPassword.getText().toString().trim();
            dbHelper = new DatabaseHelper(getApplicationContext());

            if (email.isEmpty()) {
                this.edTxtEmail.setError(getString(R.string.txtErrorEmptyEmail));
                this.edTxtEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                this.edTxtPassword.setError(getString(R.string.txtErrorEmptyPassword));
                this.edTxtPassword.requestFocus();
                return;
            }

            // Check if the email field is a valid email format
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                this.edTxtEmail.setError(getString(R.string.txtErrorValidEmail));
                this.edTxtEmail.requestFocus();
                return;
            }

            // Check if the email and password match the registered ones
            if(dbHelper.checkUserCredentials(email, password)) {
                User user = dbHelper.getUserByEmail(email);
                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("email", user.email);
                editor.putString("password", user.password);
                editor.apply();
                //Singleton class to hold logged user for whole app life cycle
                AppManager.instance.setUser(user);
                startActivity(new Intent(LoginActivity.this, NavigationActivity.class));
                txtErrorMessage.setVisibility(TextView.GONE);
                finish();
            } else {
                txtErrorMessage.setVisibility(TextView.VISIBLE);
            }
        });

        // Registration event
        txtRegLink.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
    }
}