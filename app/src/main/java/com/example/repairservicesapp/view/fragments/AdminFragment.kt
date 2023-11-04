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
    private lateinit var dbhelper : DatabaseHelper
    private lateinit var btnAddTechnician : Button
    private lateinit var btnAddService : Button
    private lateinit var btnUpdateService : Button
    private lateinit var spinnerServices : Spinner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin, container, false)
        loadUI(view)
        loadEvents()
        dbhelper = DatabaseHelper(requireContext())
        technicians = dbhelper.allTechnicians as ArrayList<User>
        services = dbhelper.allServices as ArrayList<Service>
        var techniciansNames = ArrayList<String>()
        for (technician in technicians) {
            techniciansNames.add(technician.userFirstAndLastName)
        }

        var servicesNames = ArrayList<String>()
        for (service in services) {
            servicesNames.add(service.serviceName!!)
        }

        spinnerServices = view.findViewById(R.id.custom_spinner_services)
        val adapterServices = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, servicesNames)
        spinnerServices.adapter = adapterServices

        val spinnerTechnicians = view.findViewById<Spinner>(R.id.custom_spinner_technicians)
        val adapterTechnicians = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, techniciansNames)
        spinnerTechnicians.adapter = adapterTechnicians

        return view
    }

    private fun loadUI(view: View) {
        btnAddTechnician = view.findViewById(R.id.btnAddTechnician)
        btnAddService = view.findViewById(R.id.btnAddService)
        btnUpdateService = view.findViewById(R.id.btnUpdateService)
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
            val fragment = UpdateServiceFragment()
            val bundle = Bundle()
            bundle.putString("selectedService", spinnerServices.selectedItem.toString())
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}