package com.example.repairservicesapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.repairservicesapp.R;
import com.example.repairservicesapp.app.AppManager;
import com.example.repairservicesapp.database.FirebaseUtils;
import com.example.repairservicesapp.model.User;
import com.example.repairservicesapp.util.StatusBarUtils;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    EditText edTxtEmail, edTxtPassword;
    TextView txtRegLink, txtErrorMessage;
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

        // Using shared preferences to auto login
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String email = preferences.getString("email", "");
        String password = preferences.getString("password", "");

        auth(email, password, true);
    }

    public void loadEvents() {
        // Login event
        btnLogin.setOnClickListener(v -> {
            String email = this.edTxtEmail.getText().toString().trim();
            String password = this.edTxtPassword.getText().toString().trim();

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

            // Manual login
            auth(email, password, false);
        });

        // Registration event
        txtRegLink.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
    }

    private void auth(String email, String password, boolean isAutoLogin) {
        FirebaseUtils.INSTANCE.getFirestore().collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .limit(1)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.d("LoginActivity", "Error getting documents: ", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        for (User user : value.toObjects(User.class)) {
                            if (isAutoLogin) {
                                Log.d("LoginActivity", "auto login success with user: " + user.getUserId() + ", " + user.email + ", " + user.password);
                            } else {
                                Log.d("LoginActivity", "login success with user: " + user.getUserId() + ", " + user.email + ", " + user.password);
                                SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("email", user.email);
                                editor.putString("password", user.password);
                                editor.apply();
                            }

                            //Singleton class to hold logged user for whole app life cycle
                            AppManager.instance.setUser(user);
                            startActivity(new Intent(LoginActivity.this, NavigationActivity.class));
                            txtErrorMessage.setVisibility(TextView.GONE);
                            finish();
                        }
                    } else {
                        if (!isAutoLogin) {
                            txtErrorMessage.setVisibility(TextView.VISIBLE);
                            Log.d("LoginActivity", "login error with user: " + AppManager.instance.user);
                        }
                    }
                });
    }
}