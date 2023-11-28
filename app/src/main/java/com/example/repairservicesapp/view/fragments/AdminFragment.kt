package com.example.repairservicesapp.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.repairservicesapp.R
import com.example.repairservicesapp.database.FirebaseUtils
import com.example.repairservicesapp.model.Service
import com.example.repairservicesapp.model.User
import com.example.repairservicesapp.util.MapUtils
import com.google.firebase.firestore.toObject



class AdminFragment : Fragment() {
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
        loadUI(view)
        loadEvents()
        return view
    }

    private fun loadUI(view: View) {
        btnAddTechnician = view.findViewById(R.id.btnAddTechnician)
        btnAddService = view.findViewById(R.id.btnAddService)
        btnUpdateService = view.findViewById(R.id.btnUpdateService)

        // Get all technicians and services
        getAllTechnicians()
        getAllServices()
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

    private fun getAllTechnicians() {
        FirebaseUtils.firestore.collection("users")
            .whereEqualTo("userType", User.UserType.TECHNICIAN)
            .orderBy("firstName")
            .get()
            .addOnSuccessListener { result ->
                val techniciansNames = ArrayList<String>()
                for (document in result) {
                    val technician = MapUtils.snapshotToUserObject(document)
                    techniciansNames.add(technician.userFirstAndLastName)
                }
                val spinnerTechnicians = view?.findViewById<Spinner>(R.id.custom_spinner_technicians)
                val adapterTechnicians = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, techniciansNames)
                spinnerTechnicians?.adapter = adapterTechnicians
            }
            .addOnFailureListener { e ->
                Log.d("AdminFragment", "Error getting documents: " + e.message)
            }
    }

    private fun getAllServices() {
        FirebaseUtils.firestore.collection("services")
            .orderBy("serviceName")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val service = MapUtils.snapshotServiceObject(document)
                    servicesNames.add(service.serviceName!!)
                }
                spinnerServices = view?.findViewById(R.id.custom_spinner_services)!!
                val adapterServices = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, servicesNames)
                spinnerServices.adapter = adapterServices
                enableUpdateServiceButton()
            }
            .addOnFailureListener { e ->
                Log.d("AdminFragment", "Error getting documents: " + e.message)
            }
    }

    private fun enableUpdateServiceButton() {
        btnUpdateService.isEnabled = servicesNames.size > 0
        if (btnUpdateService.isEnabled) {
            btnUpdateService.setBackgroundResource(R.drawable.button_enabled)
            context?.let { btnUpdateService.setTextColor(it.getColor(R.color.white)) }
        } else {
            btnUpdateService.setBackgroundResource(R.drawable.button_disabled)
            context?.let { btnUpdateService.setTextColor(it.getColor(R.color.light_gray)) }
        }
    }
}