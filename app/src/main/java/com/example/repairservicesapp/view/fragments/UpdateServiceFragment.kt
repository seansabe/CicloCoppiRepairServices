package com.example.repairservicesapp.view.fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.repairservicesapp.R
import com.example.repairservicesapp.database.DatabaseHelper
import com.example.repairservicesapp.model.Service
import com.example.repairservicesapp.util.KeyboardUtils

class UpdateServiceFragment : Fragment() {
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var edTxtServiceName: EditText
    private lateinit var edTxtServiceDescription: EditText
    private lateinit var edTxtServicePrice: EditText
    private lateinit var edTxtServiceDuration: EditText
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var view: View
    private var serviceId = 0
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
        arguments?.getString("selectedService")?.let {selectedService ->
            dbHelper = DatabaseHelper(requireContext())
            val service = dbHelper.getServiceByName(selectedService)
            serviceId = service?.getServiceId()!!
            edTxtServiceName.setText(service.serviceName)
            edTxtServiceDescription.setText(service.serviceDescription)
            edTxtServicePrice.setText(service.servicePrice.toString())
            edTxtServiceDuration.setText(service.serviceDuration.toString())
        }
    }

    private fun loadEvents() {
        btnSave.setOnClickListener {
            updateService()
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
            dbHelper = DatabaseHelper(requireContext())
            dbHelper.updateService(Service(serviceId, serviceName, serviceDescription, servicePrice, serviceDuration))
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
}