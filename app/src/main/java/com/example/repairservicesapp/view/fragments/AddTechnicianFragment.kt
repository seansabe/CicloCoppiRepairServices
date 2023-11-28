package com.example.repairservicesapp.view.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.repairservicesapp.R
import com.example.repairservicesapp.database.FirebaseUtils
import com.example.repairservicesapp.database.FirebaseUtils.setUserId
import com.example.repairservicesapp.model.User
import com.example.repairservicesapp.util.MapUtils

class AddTechnicianFragment : Fragment() {
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button
    private lateinit var edTxtFirstName: EditText
    private lateinit var edTxtLastName: EditText
    private lateinit var edTxtEmail: EditText
    private lateinit var edTxtPhone: EditText
    private lateinit var edTxtAddress: EditText
    private lateinit var edTxtPassword: EditText
    private lateinit var edTxtConfirmPassword: EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_technician, container, false)
        loadUI(view)
        loadEvents()
        return view
    }

    private fun loadUI(view: View) {
        btnAdd = view.findViewById(R.id.btnAdd)
        btnCancel = view.findViewById(R.id.btnCancel)
        edTxtFirstName = view.findViewById(R.id.edTxtTechnicianFirstName)
        edTxtLastName = view.findViewById(R.id.edTxtTechnicianLastName)
        edTxtEmail = view.findViewById(R.id.edTxtTechnicianEmail)
        edTxtPhone = view.findViewById(R.id.edTxtTechnicianPhone)
        edTxtAddress = view.findViewById(R.id.edTxtTechnicianAddress)
        edTxtPassword = view.findViewById(R.id.edTxtTechnicianPass)
        edTxtConfirmPassword = view.findViewById(R.id.edTxtTechnicianPassConfirm)
    }

    private fun loadEvents() {
        btnAdd.setOnClickListener {
            if (checkTextUtils()) {
                val firstName = edTxtFirstName.text.toString()
                val lastName = edTxtLastName.text.toString()
                val email = edTxtEmail.text.toString()
                val phone = edTxtPhone.text.toString()
                val address = edTxtAddress.text.toString()
                val password = edTxtPassword.text.toString()
                addTechnician(User(firstName, lastName, address, phone, email, password, userType = User.UserType.TECHNICIAN, null, 100))
                val fragment = AdminFragment()
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment_container_view, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                Toast.makeText(context, "Technician added successfully", Toast.LENGTH_SHORT).show()
            }
        }
        btnCancel.setOnClickListener {
            val fragment = AdminFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container_view, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    private fun checkTextUtils(): Boolean {
        var isValid = true
        val password: String
        val passwordConf: String
        if (TextUtils.isEmpty(edTxtFirstName.text.toString())) {
            edTxtFirstName.error = getString(R.string.txtErrorEmptyFirstName)
            isValid = false
        }
        if (TextUtils.isEmpty(edTxtLastName.text.toString())) {
            edTxtLastName.error = getString(R.string.txtErrorEmptyLastName)
            isValid = false
        }
        if (TextUtils.isEmpty(edTxtAddress.text.toString())) {
            edTxtAddress.error = getString(R.string.txtErrorEmptyAddress)
            isValid = false
        }
        if (TextUtils.isEmpty(edTxtPhone.text.toString())) {
            edTxtPhone.error = getString(R.string.txtErrorEmptyPhone)
            isValid = false
        }
        if (TextUtils.isEmpty(edTxtEmail.text.toString())) {
            edTxtEmail.error = getString(R.string.txtErrorEmptyEmail)
            isValid = false
        }
        if (TextUtils.isEmpty(edTxtPassword.text.toString())) {
            edTxtPassword.error = getString(R.string.txtErrorEmptyPassword)
            isValid = false
        }
        if (TextUtils.isEmpty(edTxtConfirmPassword.text.toString())) {
            edTxtConfirmPassword.error = getString(R.string.txtErrorEmptyConfirmPassword)
            isValid = false
        }
        if (isValid) {
            password = edTxtPassword.text.toString()
            passwordConf = edTxtConfirmPassword.text.toString()
            if (password != passwordConf) {
                edTxtConfirmPassword.error = getString(R.string.txtErrorPassMismatch)
                isValid = false
                edTxtPassword.text = null
                edTxtPassword.clearFocus()
                edTxtConfirmPassword.text = null
                edTxtConfirmPassword.clearFocus()
            }
        }
        return isValid
    }

    private fun addTechnician(user: User) {
        FirebaseUtils.firestore.collection("users")
            .add(MapUtils.userToMap(user))
            .addOnSuccessListener {
                Log.d("AddTechnicianFragment", "Technician added successfully")
                // Set userId inside Firebase document based on document id
                setUserId(it.id)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error adding technician!", Toast.LENGTH_SHORT).show()
            }
    }
}