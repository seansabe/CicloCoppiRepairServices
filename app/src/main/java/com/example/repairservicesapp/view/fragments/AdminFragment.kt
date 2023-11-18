package com.example.repairservicesapp.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.repairservicesapp.R
import com.example.repairservicesapp.database.DatabaseHelper
import com.example.repairservicesapp.model.Service
import com.example.repairservicesapp.model.User
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AdminFragment : Fragment() {
    private lateinit var technicians : ArrayList<User>
    private lateinit var services : ArrayList<Service>
    private lateinit var dbHelper : DatabaseHelper
    private lateinit var btnAddTechnician : Button
    private lateinit var btnAddService : Button
    private lateinit var btnUpdateService : Button
    private lateinit var spinnerServices : Spinner
    private var servicesNames = ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin, container, false)

        dbHelper = DatabaseHelper(requireContext())
        technicians = dbHelper.allTechnicians as ArrayList<User>
        services = dbHelper.allServices as ArrayList<Service>
        val techniciansNames = ArrayList<String>()
        for (technician in technicians) {
            techniciansNames.add(technician.userFirstAndLastName)
        }

        for (service in services) {
            servicesNames.add(service.serviceName!!)
        }

        spinnerServices = view.findViewById(R.id.custom_spinner_services)
        val adapterServices = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, servicesNames)
        spinnerServices.adapter = adapterServices

        val spinnerTechnicians = view.findViewById<Spinner>(R.id.custom_spinner_technicians)
        val adapterTechnicians = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, techniciansNames)
        spinnerTechnicians.adapter = adapterTechnicians
        loadUI(view)
        loadEvents()
        return view
    }

    private fun loadUI(view: View) {
        btnAddTechnician = view.findViewById(R.id.btnAddTechnician)
        btnAddService = view.findViewById(R.id.btnAddService)
        btnUpdateService = view.findViewById(R.id.btnUpdateService)

        btnUpdateService.isEnabled = servicesNames.size > 0
        if (btnUpdateService.isEnabled) {
            btnUpdateService.setBackgroundResource(R.drawable.button_enabled)
            context?.let { btnUpdateService.setTextColor(it.getColor(R.color.white)) }
        } else {
            btnUpdateService.setBackgroundResource(R.drawable.button_disabled)
            context?.let { btnUpdateService.setTextColor(it.getColor(R.color.light_gray)) }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun loadEvents() {
        btnAddTechnician.setOnClickListener {
            val fragment = AddTechnicianFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack(null)
                .commit()
        }

        btnAddService.setOnClickListener {
            val fragment = AddServiceFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack(null)
                .commit()
        }

        btnUpdateService.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("selectedService", spinnerServices.selectedItem.toString())
            val fragment = UpdateServiceFragment()
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}