package com.example.repairservicesapp.view.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.example.repairservicesapp.R
import com.example.repairservicesapp.app.AppManager
import com.example.repairservicesapp.database.DatabaseHelper


class ScheduleFragment : Fragment() {
    private lateinit var dbHelper : DatabaseHelper
    private lateinit var btnSave: Button
    private lateinit var rdAvailable: RadioButton
    private lateinit var rdUnavailable: RadioButton
    //private lateinit var txtDateTime : EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)
        loadUI(view)
        loadEvents()
        dbHelper = DatabaseHelper(requireContext())
        return view
    }

    private fun loadUI(view: View) {
        btnSave = view.findViewById(R.id.btnSaveAvailability)
        rdAvailable = view.findViewById(R.id.rdAvailable)
        rdUnavailable = view.findViewById(R.id.rdUnavailable)
        // Change the color of the radio buttons to match the theme of the app.
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ), intArrayOf(
                view.context.getColor(R.color.gray),  // Unchecked color
                view.context.getColor(R.color.blue) // Checked color
            )
        )
        rdAvailable.buttonTintList = colorStateList
        rdUnavailable.buttonTintList = colorStateList
        if (AppManager.instance.user.userAvailability == 100) {
            rdAvailable.isChecked = true
        } else {
            rdUnavailable.isChecked = true
        }

        //txtDateTime = view.findViewById(R.id.edTxtDatePicker)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun loadEvents() {
        btnSave.setOnClickListener {
            if (rdAvailable.isChecked) {
                AppManager.instance.user.userAvailability = 100
            } else {
                AppManager.instance.user.userAvailability = 0
            }
            dbHelper.updateTechnicianAvailability(AppManager.instance.user.getUserId(), AppManager.instance.user.userAvailability)
        }
        /*
        txtDateTime.setOnClickListener {
            showDatePicker()
        }
        */
    }

    /*
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
    }*/
}