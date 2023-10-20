package com.example.repairservicesapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment

import com.example.repairservicesapp.R
import com.example.repairservicesapp.database.DatabaseHelper
import com.example.repairservicesapp.model.User

class ScheduleFragment : Fragment() {
    private lateinit var technicians : ArrayList<User>
    private lateinit var dbhelper : DatabaseHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        val btnAddTechnician = view.findViewById<View>(R.id.btnAddTechnician)
        btnAddTechnician.setOnClickListener {
            val fragment = AddTechnicianFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container_view, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        dbhelper = DatabaseHelper(requireContext())
        technicians = dbhelper.allTechnicians as ArrayList<User>
        var technicianNames = ArrayList<String>()
        for (technician in technicians) {
            technicianNames.add(technician.firstName + " " + technician.lastName)
        }

        val spinner = view.findViewById<Spinner>(R.id.custom_spinner)
        val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, technicianNames)
        spinner.adapter = adapter

        return view
    }
}