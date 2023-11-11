package com.example.repairservicesapp.view.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.repairservicesapp.R
import com.example.repairservicesapp.app.AppManager
import com.example.repairservicesapp.data.Bicycle
import com.example.repairservicesapp.database.DatabaseHelper
import com.example.repairservicesapp.database.FirebaseUtils
import com.example.repairservicesapp.model.Booking
import com.example.repairservicesapp.model.Service
import com.example.repairservicesapp.model.User
import com.example.repairservicesapp.util.UnitsUtils

class EditBookingFragment : Fragment() {
    private lateinit var technicians : ArrayList<User>
    private lateinit var services : ArrayList<Service>
    private var selectedServices : ArrayList<Service> = ArrayList()
    private var techniciansNames : ArrayList<String> = ArrayList()
    private var servicesNames : ArrayList<String> = ArrayList()
    private var statusList : ArrayList<String> = ArrayList()
    private lateinit var dbHelper : DatabaseHelper
    private lateinit var btnAddService : Button
    private lateinit var btnUpdateBooking : Button
    private lateinit var btnDelete : Button
    private lateinit var btnCancel : Button
    private lateinit var txtEstimatedCost : TextView
    private lateinit var txtEstimatedDuration : TextView
    private lateinit var edTxtDate : EditText
    private lateinit var edTxtTime : EditText
    private lateinit var edTxtComments : EditText
    private var spinnerCount = 0
    private lateinit var container : LinearLayout
    private lateinit var rdMorning: RadioButton
    private lateinit var rdAfternoon: RadioButton
    private lateinit var spinnerTechnicians : Spinner
    private lateinit var spinnerBikeType : Spinner
    private lateinit var spinnerBikeColor : Spinner
    private lateinit var spinnerBikeWheelSize : Spinner
    private lateinit var spinnerStatus : Spinner
    private lateinit var booking: Booking
    private var estimatedCost = 0.0
    private var estimatedDuration = 0
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_booking, container, false)
        dbHelper = DatabaseHelper(requireContext())
        services = dbHelper.allServices as ArrayList<Service>
        technicians = dbHelper.allTechnicians as ArrayList<User>
        booking = arguments?.getParcelable("selectedBooking", Booking::class.java)!!

        for (technician in technicians) {
            techniciansNames.add(technician.userFirstAndLastName)
        }

        for (service in services) {
            servicesNames.add(service.serviceName!!)
        }

        statusList.add(Booking.BookingStatus.PENDING.getStatusValue(requireContext())!!)
        statusList.add(Booking.BookingStatus.ASSIGNED.getStatusValue(requireContext())!!)
        statusList.add(Booking.BookingStatus.AWAITING_BIKE.getStatusValue(requireContext())!!)
        statusList.add(Booking.BookingStatus.IN_PROCESS.getStatusValue(requireContext())!!)
        statusList.add(Booking.BookingStatus.ACCEPTED.getStatusValue(requireContext())!!)
        statusList.add(Booking.BookingStatus.CANCELLED.getStatusValue(requireContext())!!)
        statusList.add(Booking.BookingStatus.COMPLETED.getStatusValue(requireContext())!!)

        loadUI(view)
        getBookingData()
        loadEvents()
        return view
    }

    private fun getBookingData() {
        rdMorning.isChecked = booking.dropInTime == "Morning"
        rdAfternoon.isChecked = booking.dropInTime == "Afternoon"
        spinnerStatus.setSelection((spinnerStatus.adapter as ArrayAdapter<String>).getPosition(booking.bookingStatus?.getStatusValue(requireContext())))
        spinnerTechnicians.setSelection((spinnerTechnicians.adapter as ArrayAdapter<String>).getPosition(booking.technician?.userFirstAndLastName))
        spinnerBikeType.setSelection((spinnerBikeType.adapter as ArrayAdapter<String>).getPosition(booking.bikeType))
        spinnerBikeColor.setSelection((spinnerBikeColor.adapter as ArrayAdapter<String>).getPosition(booking.bikeColor))
        spinnerBikeWheelSize.setSelection((spinnerBikeWheelSize.adapter as ArrayAdapter<String>).getPosition(booking.bikeWheelSize))
        edTxtDate.setText(booking.bookingDate)
        edTxtTime.setText(booking.bookingTime)
        edTxtComments.setText(booking.comments)

        // Create the spinners for the selected services
        for (service in booking.services!!) {
            addService(container)
            val spinner = container.getChildAt(container.childCount - 1) as Spinner
            spinner.setSelection((spinner.adapter as ArrayAdapter<String>).getPosition(service.serviceName))
        }

        // Update estimated cost and duration based on existing services
        getEstimatedCostAndHours(container)
    }

    private fun loadUI(view: View) {
        btnAddService = view.findViewById(R.id.btnAddService)
        container = view.findViewById(R.id.linearLayoutServices)
        btnUpdateBooking = view.findViewById(R.id.btnUpdateBooking)
        btnDelete = view.findViewById(R.id.btnDelete)
        btnCancel = view.findViewById(R.id.btnCancel)
        rdMorning = view.findViewById(R.id.rdMorning)
        rdAfternoon = view.findViewById(R.id.rdAfternoon)
        spinnerStatus = view.findViewById(R.id.custom_spinner_status)
        spinnerTechnicians = view.findViewById(R.id.custom_spinner_technicians)
        spinnerBikeType = view.findViewById(R.id.custom_spinner_bike_type)
        spinnerBikeColor = view.findViewById(R.id.custom_spinner_bike_color)
        spinnerBikeWheelSize = view.findViewById(R.id.custom_spinner_wheel_size)
        txtEstimatedCost = view.findViewById(R.id.txtEstimatedCost)
        txtEstimatedDuration = view.findViewById(R.id.txtEstimatedDuration)
        edTxtDate = view.findViewById(R.id.edTxtAssignedDate)
        edTxtTime = view.findViewById(R.id.edTxtAssignedTime)
        edTxtComments = view.findViewById(R.id.edTxtComments)
        val adapterStatus = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, statusList)
        val adapterTechnicians = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, techniciansNames)
        val adapterBikeType = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, Bicycle(requireContext()).type)
        val adapterBikeColor = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, Bicycle(requireContext()).color)
        val adapterBikeWheelSize = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, Bicycle(requireContext()).wheelSize)
        spinnerStatus.adapter = adapterStatus
        spinnerTechnicians.adapter = adapterTechnicians
        spinnerBikeType.adapter = adapterBikeType
        spinnerBikeColor.adapter = adapterBikeColor
        spinnerBikeWheelSize.adapter = adapterBikeWheelSize
        //addDefaultSpinner(container)

        // Disable the delete button if there's only one spinner
        btnDelete.isEnabled = container.childCount > 1

        // Check the child count in the container and enable/disable the delete button accordingly
        container.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            btnDelete.isEnabled = container.childCount > 1
            if (btnDelete.isEnabled) {
                btnDelete.setBackgroundResource(R.drawable.button_enabled)
                context?.let { btnDelete.setTextColor(it.getColor(R.color.white)) }
            } else {
                btnDelete.setBackgroundResource(R.drawable.button_disabled)
                context?.let { btnDelete.setTextColor(it.getColor(R.color.light_gray)) }
            }
        }

        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ), intArrayOf(
                view.context.getColor(R.color.gray),  // Unchecked color
                view.context.getColor(R.color.blue) // Checked color
            )
        )
        rdMorning.buttonTintList = colorStateList
        rdAfternoon.buttonTintList = colorStateList

        setSpinnerListeners(container)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun loadEvents() {
        btnAddService.setOnClickListener {
            addService(container)
        }

        btnUpdateBooking.setOnClickListener {
            saveBooking(container)
        }

        btnDelete.setOnClickListener {
            deleteLastSpinner(container)
        }

        btnCancel.setOnClickListener {
            val fragment = ServiceHistoryTechnicianFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack(null)
                .commit() }
    }

    private fun addDefaultSpinner(container: LinearLayout) {
        val spinner = Spinner(requireContext())
        val layoutParams = LinearLayout.LayoutParams(UnitsUtils.dpToPx(300, requireContext()), UnitsUtils.dpToPx(50, requireContext()))
        layoutParams.setMargins(0, UnitsUtils.dpToPx(10, requireContext()), 0, 0)
        spinner.setBackgroundResource(R.drawable.edit_field)
        spinner.adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, servicesNames)
        spinner.layoutParams = layoutParams
        spinner.tag = "Spinner $spinnerCount" // Set a tag to identify the spinners
        container.addView(spinner)
        spinnerCount++
    }

    private fun addService(container : LinearLayout) {
        val spinner = Spinner(requireContext())
        val layoutParams = LinearLayout.LayoutParams(UnitsUtils.dpToPx(300, requireContext()), UnitsUtils.dpToPx(50, requireContext()))
        layoutParams.setMargins(0, UnitsUtils.dpToPx(10, requireContext()), 0, 0)
        spinner.setBackgroundResource(R.drawable.edit_field)
        spinner.adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, servicesNames)
        spinner.layoutParams = layoutParams
        spinner.tag = "Spinner $spinnerCount" // Set a tag to identify the spinners
        container.addView(spinner)
        spinnerCount++
        setSpinnerListeners(container)
    }

    private fun getEstimatedCostAndHours(container: LinearLayout) {
        var estimatedCost = 0.0 // Reset the estimated cost
        var estimatedDuration = 0 // Reset the estimated duration
        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            if (view is Spinner) {
                val selectedService = view.selectedItem.toString()
                for (service in services) {
                    if (service.serviceName == selectedService) {
                        estimatedCost += service.serviceCost // Update the estimated cost
                        estimatedDuration += service.serviceDuration // Update the estimated duration
                    }
                }
            }
        }
        txtEstimatedCost.text = "$$estimatedCost" // Update the displayed cost
        txtEstimatedDuration.text = "$estimatedDuration h" // Update the displayed duration
        this.estimatedCost = estimatedCost
        this.estimatedDuration = estimatedDuration
    }

    private fun saveBooking(container: LinearLayout) {
        if (rdMorning.isChecked || rdAfternoon.isChecked) {
            // Create updated booking data
            val updatedBookingData = createBookingData(container)
            // Update the existing booking document in Firebase
            updateBookingInFirebase(updatedBookingData)
        } else {
            showDropInTimeSelectionError()
        }
    }

    private fun showDropInTimeSelectionError() {
        Toast.makeText(requireContext(), "Please select a drop in time", Toast.LENGTH_SHORT).show()
    }

    private fun createBookingData(container: LinearLayout): HashMap<String, Any?> {

        val selectedDropInTime = if (rdMorning.isChecked) "Morning" else "Afternoon"
        val selectedDate = edTxtDate.text.toString()
        val selectedTime = edTxtTime.text.toString()
        val selectedStatus = spinnerStatus.selectedItem.toString()
        val selectedBikeType = spinnerBikeType.selectedItem.toString()
        val selectedBikeColor = spinnerBikeColor.selectedItem.toString()
        val selectedBikeWheelSize = spinnerBikeWheelSize.selectedItem.toString()
        val comments = edTxtComments.text.toString().ifEmpty { "No comments" }
        val customer = this.booking.customer!!
        // Split the technician's name into first and last name to use in dbHelper.getTechnicianByName()
        val technicianName = spinnerTechnicians.selectedItem.toString().split(" ")
        val technician = dbHelper.getTechnicianByName(technicianName[0], technicianName[1])

        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            if (view is Spinner) {
                val selectedService = view.selectedItem.toString()
                services.find { it.serviceName == selectedService }?.let { selectedServices.add(it) }
            }
        }

        val booking = Booking(
            selectedDropInTime,
            selectedDate,
            selectedTime,
            Booking.BookingStatus.valueOf(selectedStatus),
            estimatedCost,
            estimatedDuration,
            selectedBikeType,
            selectedBikeColor,
            selectedBikeWheelSize,
            comments,
            selectedServices,
            customer,
            technician
        )

        val mappedServices = booking.services?.map {
            mapOf(
                "serviceId" to it.getServiceId(),
                "serviceName" to it.serviceName!!,
                "serviceDescription" to it.serviceDescription!!,
                "servicePrice" to it.serviceCost,
                "serviceDuration" to it.serviceDuration
            )
        }

        val mappedCustomer = hashMapOf(
            "customerId" to booking.customer?.getUserId(),
            "customerFirstName" to booking.customer?.firstName!!,
            "customerLastName" to booking.customer?.lastName!!,
            "customerAddress" to booking.customer?.address!!,
            "customerPhoneNumber" to booking.customer?.phoneNumber!!,
            "customerEmail" to booking.customer?.email!!
        )

        val mappedTechnician = hashMapOf(
            "technicianId" to booking.technician?.getUserId(),
            "technicianFirstName" to booking.technician?.firstName!!,
            "technicianLastName" to booking.technician?.lastName!!,
            "technicianAddress" to booking.technician?.address!!,
            "technicianPhoneNumber" to booking.technician?.phoneNumber!!,
            "technicianEmail" to booking.technician?.email!!
        )

        return hashMapOf(
            "dropInTime" to booking.dropInTime!!,
            "bookingDate" to booking.bookingDate!!,
            "bookingTime" to booking.bookingTime!!,
            "bookingStatus" to booking.bookingStatus!!.name,
            "bookingCost" to booking.bookingCost,
            "bookingDuration" to booking.bookingDuration,
            "bikeType" to booking.bikeType,
            "bikeColor" to booking.bikeColor,
            "bikeWheelSize" to booking.bikeWheelSize,
            "services" to mappedServices,
            "comments" to booking.comments,
            "customer" to mappedCustomer,
            "technician" to mappedTechnician
        )
    }

    private fun updateBookingInFirebase(bookingData: HashMap<String, Any?>) {
        // Use the existing bookingId to update the document in Firebase
        val bookingId = booking.bookingId
        if (bookingId != null) {
            FirebaseUtils.fireStoreDatabase.collection("bookings").document(bookingId)
                .update(bookingData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Booking updated successfully", Toast.LENGTH_SHORT).show()
                    Log.d("EditBookingFragment", "Booking updated successfully")

                    // Navigate back or perform other actions
                    val fragment = ServiceHistoryTechnicianFragment()
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container_view, fragment)
                        .addToBackStack(null)
                        .commit()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error updating booking", Toast.LENGTH_SHORT).show()
                    Log.d("EditBookingFragment", "Error updating booking")
                }
        }
    }

    private fun deleteLastSpinner(container: LinearLayout) {
        if (container.childCount > 0) {
            container.removeViewAt(container.childCount - 1)
            // Listen for layout changes and then update the cost and hours
            container.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                override fun onLayoutChange(
                    v: View?,
                    left: Int,
                    top: Int,
                    right: Int,
                    bottom: Int,
                    oldLeft: Int,
                    oldTop: Int,
                    oldRight: Int,
                    oldBottom: Int
                ) {
                    getEstimatedCostAndHours(container) // Update the estimated cost when the layout changes
                    container.removeOnLayoutChangeListener(this) // Remove the listener to avoid repetitive calls
                }
            })
        }
    }

    private fun setSpinnerListeners(container: LinearLayout) {
        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            if (view is Spinner) {
                view.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                        getEstimatedCostAndHours(container) // Update the estimated cost when a selection changes
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }
    }
}