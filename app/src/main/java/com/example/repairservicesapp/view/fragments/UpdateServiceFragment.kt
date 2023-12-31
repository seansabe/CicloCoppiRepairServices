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
import com.example.repairservicesapp.model.Service
import com.example.repairservicesapp.util.KeyboardUtils
import com.google.firebase.firestore.toObject

class UpdateServiceFragment : Fragment() {
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var edTxtServiceName: EditText
    private lateinit var edTxtServiceDescription: EditText
    private lateinit var edTxtServicePrice: EditText
    private lateinit var edTxtServiceDuration: EditText
    private lateinit var view: View
    private var serviceId = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_update_service, container, false)
        loadService(view)
        loadEvents()
        return view
    }

    private fun loadService(view: View) {
        edTxtServiceName = view.findViewById(R.id.edTxtServiceName)
        edTxtServiceDescription = view.findViewById(R.id.edTxtServiceDescription)
        edTxtServicePrice = view.findViewById(R.id.edTxtServicePrice)
        edTxtServiceDuration = view.findViewById(R.id.edTxtServiceDuration)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)

        // Get selected Service
        getService()
    }

    private fun loadEvents() {
        btnSave.setOnClickListener {
            updateService()
            Toast.makeText(requireContext(), R.string.txtServiceUpdated, Toast.LENGTH_SHORT).show()
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

    private fun updateService() {
        if (checkTextUtils()) {
            val serviceName = edTxtServiceName.text.toString()
            val serviceDescription = edTxtServiceDescription.text.toString()
            val servicePrice = edTxtServicePrice.text.toString().toDouble()
            val serviceDuration = edTxtServiceDuration.text.toString().toInt()
            updateService(Service(serviceId, serviceName, serviceDescription, servicePrice, serviceDuration))
            val fragment = AdminFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container_view, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        KeyboardUtils.hideKeyboard(view)
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

    private fun getService() {
        var selectedService = arguments?.getString("selectedService")
        FirebaseUtils.firestore.collection("services")
            .whereEqualTo("serviceName", selectedService)
            .limit(1)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w("UpdateServiceFragment", "Listen failed.", error)
                    return@addSnapshotListener
                }
                for (document in value!!) {
                    val service = document.toObject<Service>()
                    serviceId = service.getServiceId()
                    edTxtServiceName.setText(service.serviceName)
                    edTxtServiceDescription.setText(service.serviceDescription)
                    edTxtServicePrice.setText(service.serviceCost.toString())
                    edTxtServiceDuration.setText(service.serviceDuration.toString())
                }
            }
    }

    private fun updateService(service: Service) {
        FirebaseUtils.firestore.collection("services")
            .document(service.getServiceId())
            .set(service)
            .addOnSuccessListener {
                Log.d("UpdateServiceFragment", "Service successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("UpdateServiceFragment", "Error updating service", e)
            }
    }
}