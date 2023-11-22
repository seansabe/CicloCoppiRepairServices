package com.example.repairservicesapp.view.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.repairservicesapp.R;
import com.example.repairservicesapp.app.AppManager;
import com.example.repairservicesapp.database.DatabaseHelper;
import com.example.repairservicesapp.model.User;
import com.example.repairservicesapp.util.KeyboardUtils;
import com.example.repairservicesapp.view.LoginActivity;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    View view;
    Button btnLogOut, btnSave;
    EditText edTxtFirstName, edTxtLastName, edTxtEmail, edTxtPhone, edTxtAddress, edTxtCurrentPass, edTxtNewPass, edTxtConfirmPass;
    User user;
    DatabaseHelper dbHelper;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        loadUserData(view);
        loadEvents();
        return view;
    }

    private void loadUserData(View view) {
        edTxtFirstName = view.findViewById(R.id.edTxtProfileFirstName);
        edTxtLastName = view.findViewById(R.id.edTxtProfileLastName);
        edTxtEmail = view.findViewById(R.id.edTxtProfileEmail);
        edTxtPhone = view.findViewById(R.id.edTxtProfilePhone);
        edTxtAddress = view.findViewById(R.id.edTxtProfileAddress);
        edTxtCurrentPass = view.findViewById(R.id.edTxtProfileCurrentPass);
        edTxtNewPass = view.findViewById(R.id.edTxtProfileNewPass);
        edTxtConfirmPass = view.findViewById(R.id.edTxtProfileConfirmPass);
        btnLogOut = view.findViewById(R.id.btnProfileLogOut);
        btnSave = view.findViewById(R.id.btnProfileSave);
        user = AppManager.instance.user;
        edTxtFirstName.setText(user.firstName);
        edTxtLastName.setText(user.lastName);
        edTxtEmail.setText(user.email);
        edTxtPhone.setText(user.phoneNumber);
        edTxtAddress.setText(user.address);
    }

    private void loadEvents() {
        btnLogOut.setOnClickListener(v -> logout());
        btnSave.setOnClickListener(v -> {
            updateUserData();
            Toast.makeText(getActivity(), getString(R.string.txtProfileUpdated), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateUserData() {
        SharedPreferences preferences = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (checkTextUtils()) {
            String fName = edTxtFirstName.getText().toString();
            String lName = edTxtLastName.getText().toString();
            String address = edTxtAddress.getText().toString();
            String phone = edTxtPhone.getText().toString();
            String email = edTxtEmail.getText().toString();
            dbHelper = new DatabaseHelper(requireContext());
            user.firstName = fName;
            user.lastName = lName;
            user.address = address;
            user.phoneNumber = phone;
            user.email = email;
            dbHelper.updateUserData(user);
            editor.putString("email", user.email);
        }

        if (edTxtCurrentPass.getText().toString().equals(user.password)) {
            String newPassword = edTxtNewPass.getText().toString();
            String passwordConf = edTxtConfirmPass.getText().toString();
            if (newPassword.equals(passwordConf)) {
                user.password = newPassword;
                dbHelper.updateUserPassword(user);
                editor.putString("password", user.password);
                edTxtCurrentPass.setText(null);
                edTxtCurrentPass.clearFocus();
            } else {
                edTxtConfirmPass.setError(getString(R.string.txtErrorPassMismatch));
            }
            edTxtNewPass.setText(null);
            edTxtNewPass.clearFocus();
            edTxtConfirmPass.setText(null);
            edTxtConfirmPass.clearFocus();
        }
        editor.apply();
        KeyboardUtils.hideKeyboard(view);
    }

    private void logout() {
        deleteToken();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        AppManager.instance.user = null;
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private boolean checkTextUtils() {
        boolean isValid = true;
        if (TextUtils.isEmpty(edTxtFirstName.getText().toString())) {
            edTxtFirstName.setError(getString(R.string.txtErrorEmptyFirstName));
            isValid = false;
        }
        if (TextUtils.isEmpty(edTxtLastName.getText().toString())) {
            edTxtLastName.setError(getString(R.string.txtErrorEmptyLastName));
            isValid = false;
        }
        if (TextUtils.isEmpty(edTxtAddress.getText().toString())) {
            edTxtAddress.setError(getString(R.string.txtErrorEmptyAddress));
            isValid = false;
        }
        if (TextUtils.isEmpty(edTxtPhone.getText().toString())) {
            edTxtPhone.setError(getString(R.string.txtErrorEmptyPhone));
            isValid = false;
        }
        if (TextUtils.isEmpty(edTxtEmail.getText().toString())) {
            edTxtEmail.setError(getString(R.string.txtErrorEmptyEmail));
            isValid = false;
        }
        return isValid;
    }

    private void deleteToken() {
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("TOKEN", "Token not deleted");
                return;
            }
            Log.d("TOKEN", "Token deleted");
        });
    }
}