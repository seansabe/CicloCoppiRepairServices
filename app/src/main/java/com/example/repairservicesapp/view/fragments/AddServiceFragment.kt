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
import com.example.repairservicesapp.database.DatabaseHelper
import com.example.repairservicesapp.database.FirebaseUtils
import com.example.repairservicesapp.model.Service
import com.example.repairservicesapp.model.User
import com.example.repairservicesapp.util.MapUtils

class AddServiceFragment : Fragment() {
    private lateinit var btnAdd: Button
    private lateinit var btnCancel: Button
    private lateinit var edTxtServiceName: EditText
    private lateinit var edTxtServiceDescription: EditText
    private lateinit var edTxtServicePrice: EditText
    private lateinit var edTxtServiceDuration: EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_service, container, false)
        loadUI(view)
        loadEvents()
        return view
    }

    private fun loadUI(view: View) {
        btnAdd = view.findViewById(R.id.btnAdd)
        btnCancel = view.findViewById(R.id.btnCancel)
        edTxtServiceName = view.findViewById(R.id.edTxtServiceName)
        edTxtServiceDescription = view.findViewById(R.id.edTxtServiceDescription)
        edTxtServicePrice = view.findViewById(R.id.edTxtServicePrice)
        edTxtServiceDuration = view.findViewById(R.id.edTxtServiceDuration)
    }

    private fun loadEvents() {
        btnAdd.setOnClickListener {
            if (checkTextUtils()) {
                val serviceName = edTxtServiceName.text.toString()
                val serviceDescription = edTxtServiceDescription.text.toString()
                val servicePrice = edTxtServicePrice.text.toString().toDouble()
                val serviceDuration = edTxtServiceDuration.text.toString().toInt()
                addService(Service(serviceName, serviceDescription, servicePrice, serviceDuration))
                val fragment = AdminFragment()
                val fragmentManager = requireActivity().supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment_container_view, fragment)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
                Toast.makeText(requireContext(), R.string.txtServiceAdded, Toast.LENGTH_SHORT).show()
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
        if (TextUtils.isEmpty(edTxtServiceName.text.toString())) {
            edTxtServiceName.error = getString(R.string.txtErrorEmptyFirstName)
            isValid = false
        }
        if (TextUtils.isEmpty(edTxtServiceDescription.text.toString())) {
            edTxtServiceDescription.error = getString(R.string.txtErrorEmptyLastName)
            isValid = false
        }
        if (TextUtils.isEmpty(edTxtServicePrice.text.toString())) {
            edTxtServicePrice.error = getString(R.string.txtErrorEmptyAddress)
            isValid = false
        }
        if (TextUtils.isEmpty(edTxtServiceDuration.text.toString())) {
            edTxtServiceDuration.error = getString(R.string.txtErrorEmptyPhone)
            isValid = false
        }
        return isValid
    }

    private fun addService(service: Service) {
        FirebaseUtils.firestore.collection("services")
            .add(MapUtils.serviceToMap(service))
            .addOnSuccessListener {
                Log.d("AddServiceFragment", "Service added successfully")
                // Set serviceId inside Firebase document based on document id
                FirebaseUtils.setServiceId(it.id)
            }
            .addOnFailureListener { e ->
                Log.d("AddServiceFragment", "Error adding service: ${e.message}")
            }
    }
}