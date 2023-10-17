package com.example.repairservicesapp.view.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.repairservicesapp.R;
import com.example.repairservicesapp.app.AppManager;
import com.example.repairservicesapp.database.DatabaseHelper;
import com.example.repairservicesapp.model.User;
import com.example.repairservicesapp.view.LoginActivity;

public class ProfileFragment extends Fragment {
    View view;
    Button btnLogOut, btnSave;
    EditText edTxtFirstName, edTxtLastName, edTxtEmail, edTxtPhone, edTxtAddress, edTxtCurrentPass, edTxtNewPass, edTxtConfirmPass;
    User user;
    DatabaseHelper dbHelper;
    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        loadUserData(view);
        loadEvents();
        updateUserData();
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
        if (user != null) {
            edTxtFirstName.setText(user.getFirstName());
            edTxtLastName.setText(user.getLastName());
            edTxtEmail.setText(user.getEmail());
            edTxtPhone.setText(user.getPhoneNumber());
            edTxtAddress.setText(user.getAddress());
        }
    }

    private void loadEvents() {
        btnLogOut.setOnClickListener(v -> logout());
        btnSave.setOnClickListener(v -> updateUserData());
    }

    private void updateUserData() {
        if (checkTextUtils()) {
            String fName = edTxtFirstName.getText().toString();
            String lName = edTxtLastName.getText().toString();
            String address = edTxtAddress.getText().toString();
            String phone = edTxtPhone.getText().toString();
            String email = edTxtEmail.getText().toString();
            String password = edTxtNewPass.getText().toString();
            dbHelper = new DatabaseHelper(getActivity().getApplicationContext());
            user.setFirstName(fName);
            user.setLastName(lName);
            user.setAddress(address);
            user.setPhoneNumber(phone);
            user.setEmail(email);
            user.setPassword(password);
            dbHelper.updateUserData(user);
            Toast.makeText(getActivity(), getString(R.string.txtProfileUpdated), Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        AppManager.instance.user = null;
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private boolean checkTextUtils() {
        boolean isValid = true;
        String newPassword, passwordConf;
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
        if (isValid) {
            newPassword = edTxtNewPass.getText().toString();
            passwordConf = edTxtConfirmPass.getText().toString();
            if (edTxtCurrentPass.getText().toString().equals(user.getPassword())) {
                edTxtCurrentPass.setError(getString(R.string.txtErrorEmptyCurrentPassword));
                isValid = false;
            }
            if (!newPassword.equals(passwordConf)) {
                edTxtConfirmPass.setError(getString(R.string.txtErrorPassMismatch));
                isValid = false;
                edTxtNewPass.setText(null);
                edTxtNewPass.clearFocus();
                edTxtConfirmPass.setText(null);
                edTxtConfirmPass.clearFocus();
            }
        }
        return isValid;
    }
}