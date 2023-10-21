package com.example.repairservicesapp.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.repairservicesapp.R
import com.example.repairservicesapp.database.DatabaseHelper
import com.example.repairservicesapp.model.User
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ScheduleFragment : Fragment() {
    private lateinit var technicians : ArrayList<User>
    private lateinit var dbhelper : DatabaseHelper
    private lateinit var btnAddTechnician : Button
    private lateinit var btnAddDatePicker: Button
    private lateinit var txtDateTime : EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        loadUI(view)
        loadEvents()
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

    private fun loadUI(view: View) {
        btnAddTechnician = view.findViewById(R.id.btnAddTechnician)
        btnAddDatePicker = view.findViewById(R.id.btnAddDate)
        txtDateTime = view.findViewById(R.id.edTxtDatePicker)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun loadEvents() {
        btnAddTechnician.setOnClickListener {
            val fragment = AddTechnicianFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container_view, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        btnAddDatePicker.setOnClickListener {

        }

        txtDateTime.setOnClickListener {
            showDatePicker()
        }

    }

    private fun showDatePicker() {
        // Makes only dates from today forward selectable.
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now()).build()
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.txtSelectDate)
            .setTheme(R.style.ThemeOverlay_App_DatePicker)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder)
            .build()
        datePicker.show(parentFragmentManager, "DATE_PICKER")
        datePicker.addOnPositiveButtonClickListener { selection: Any? ->
            // Respond to positive button click.
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = selection as Long
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            var formattedDate: String = sdf.format(calendar.time)
            txtDateTime.setText(formattedDate)
            showTimePicker()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showTimePicker() {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setTheme(R.style.ThemeOverlay_App_TimePicker)
            .setHour(8)
            .setMinute(0)
            .setTitleText(R.string.txtSelectTime)
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .build()
        timePicker.show(parentFragmentManager, "TIME_PICKER")
        timePicker.addOnPositiveButtonClickListener {
            // Get the selected hour and minute
            val hour = timePicker.hour
            val minute = timePicker.minute

            // Determine whether it's AM or PM
            val amPm = if (hour < 12) "AM" else "PM"

            // Adjust the hour for 12-hour format
            val formattedHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour

            // Create the formatted time string
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", formattedHour, minute, amPm)

            // Set the text in the EditText
            txtDateTime.setText(txtDateTime.text.toString() + " - " + formattedTime)
        }
    }
}