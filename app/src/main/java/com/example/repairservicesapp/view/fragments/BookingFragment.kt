package com.example.repairservicesapp.view.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
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
import androidx.fragment.app.Fragment
import com.example.repairservicesapp.R
import com.example.repairservicesapp.app.AppManager
import com.example.repairservicesapp.data.Bicycle
import com.example.repairservicesapp.database.DatabaseHelper
import com.example.repairservicesapp.database.FirebaseUtils
import com.example.repairservicesapp.model.Booking
import com.example.repairservicesapp.model.Service
import com.example.repairservicesapp.util.UnitsUtils
import com.google.firebase.firestore.toObject


class BookingFragment : Fragment() {
    private lateinit var services : ArrayList<Service>
    private var selectedServices : ArrayList<Service> = ArrayList()
    private var servicesNames : ArrayList<String> = ArrayList()
    private lateinit var btnAddService : Button
    private lateinit var btnBook : Button
    private lateinit var btnDelete : Button
    private lateinit var txtEstimatedCost : TextView
    private lateinit var txtEstimatedDuration : TextView
    private lateinit var edTxtComments : EditText
    private var spinnerCount = 0
    private lateinit var container : LinearLayout
    private lateinit var rdMorning: RadioButton
    private lateinit var rdAfternoon: RadioButton
    private lateinit var spinnerBikeType : Spinner
    private lateinit var spinnerBikeColor : Spinner
    private lateinit var spinnerBikeWheelSize : Spinner
    private var estimatedCost = 0.0
    private var estimatedDuration = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)
        loadUI(view)
        loadEvents()
        return view
    }

    private fun loadUI(view: View) {
        btnAddService = view.findViewById(R.id.btnAddService)
        container = view.findViewById(R.id.linearLayoutServices)
        btnBook = view.findViewById(R.id.btnUpdateBooking)
        btnDelete = view.findViewById(R.id.btnDelete)
        rdMorning = view.findViewById(R.id.rdMorning)
        rdAfternoon = view.findViewById(R.id.rdAfternoon)
        spinnerBikeType = view.findViewById(R.id.custom_spinner_bike_type)
        spinnerBikeColor = view.findViewById(R.id.custom_spinner_bike_color)
        spinnerBikeWheelSize = view.findViewById(R.id.custom_spinner_wheel_size)
        txtEstimatedCost = view.findViewById(R.id.txtEstimatedCost)
        txtEstimatedDuration = view.findViewById(R.id.txtEstimatedDuration)
        edTxtComments = view.findViewById(R.id.edTxtComments)
        val adapterBikeType = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, Bicycle(requireContext()).type)
        val adapterBikeColor = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, Bicycle(requireContext()).color)
        val adapterBikeWheelSize = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, Bicycle(requireContext()).wheelSize)
        spinnerBikeType.adapter = adapterBikeType
        spinnerBikeColor.adapter = adapterBikeColor
        spinnerBikeWheelSize.adapter = adapterBikeWheelSize

        getAllServices()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun loadEvents() {
        btnAddService.setOnClickListener {
            addService(container)
        }

        btnBook.setOnClickListener {
            saveBooking(container)
        }

        btnDelete.setOnClickListener {
            deleteLastSpinner(container)
        }
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
            val bookingData = createBookingData(container)
            saveBookingToFirebase(bookingData)
        } else {
            showDropInTimeSelectionError()
        }
    }

    private fun showDropInTimeSelectionError() {
        Toast.makeText(requireContext(), "Please select a drop in time", Toast.LENGTH_SHORT).show()
    }

    private fun createBookingData(container: LinearLayout): HashMap<String, Any?> {

        val selectedDropInTime = if (rdMorning.isChecked) "Morning" else "Afternoon"
        val selectedBikeType = spinnerBikeType.selectedItem.toString()
        val selectedBikeColor = spinnerBikeColor.selectedItem.toString()
        val selectedBikeWheelSize = spinnerBikeWheelSize.selectedItem.toString()
        val comments = edTxtComments.text.toString().ifEmpty { "No comments" }
        val customer = AppManager.instance.user

        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            if (view is Spinner) {
                val selectedService = view.selectedItem.toString()
                services.find { it.serviceName == selectedService }?.let { selectedServices.add(it) }
            }
        }

        val booking = Booking(
            selectedDropInTime,
            Booking.BookingStatus.PENDING,
            estimatedCost,
            estimatedDuration,
            selectedBikeType,
            selectedBikeColor,
            selectedBikeWheelSize,
            comments,
            selectedServices,
            customer
        )

        val mappedServices = booking.services?.map {
            mapOf(
                "serviceId" to it.getServiceId(),
                "serviceName" to it.serviceName!!,
                "serviceDescription" to it.serviceDescription!!,
                "servicePrice" to it.serviceCost,
                "serviceDuration" to it.serviceDuration,
                "timestamp" to it.getTimestamp()
            )
        }

        val mappedCustomer = hashMapOf(
            "userId" to booking.customer?.getUserId(),
            "firstName" to booking.customer?.firstName!!,
            "lastName" to booking.customer?.lastName!!,
            "address" to booking.customer?.address!!,
            "phoneNumber" to booking.customer?.phoneNumber!!,
            "email" to booking.customer?.email!!,
            "password" to booking.customer?.password!!,
            "userType" to booking.customer?.userType!!.name,
            "userAvailability" to booking.customer?.userAvailability!!,
            "token" to booking.customer?.token,
            "timestamp" to booking.customer?.getTimestamp()
        )

        return hashMapOf(
            "dropInTime" to booking.dropInTime!!,
            "bookingDate" to null,
            "bookingTime" to null,
            "bookingStatus" to booking.bookingStatus!!.name,
            "bookingCost" to booking.bookingCost,
            "bookingDuration" to booking.bookingDuration,
            "bikeType" to booking.bikeType,
            "bikeColor" to booking.bikeColor,
            "bikeWheelSize" to booking.bikeWheelSize,
            "services" to mappedServices,
            "comments" to booking.comments,
            "customer" to mappedCustomer,
            "technician" to null,
            "timestamp" to booking.getTimestamp()
        )
    }

    private fun saveBookingToFirebase(bookingData: HashMap<String, Any?>) {
        FirebaseUtils.firestore.collection("bookings")
            .add(bookingData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), R.string.txtBookingAdded, Toast.LENGTH_SHORT).show()
                Log.d("BookingFragment", "Booking saved successfully")
                val fragment = BookingFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, fragment)
                    .addToBackStack(null)
                    .commit()
            }
            .addOnFailureListener {
                Log.d("BookingFragment", "Error saving booking")
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

    private fun getAllServices() {
        services = ArrayList()
        FirebaseUtils.firestore.collection("services")
            .orderBy("serviceName")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val service = document.toObject<Service>()
                    servicesNames.add(service.serviceName!!)
                    services.add(service)
                }
                addDefaultSpinner(container)

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
                        view?.context?.getColor(R.color.gray)!!,  // Unchecked color
                        view?.context?.getColor(R.color.blue)!! // Checked color
                    )
                )
                rdMorning.buttonTintList = colorStateList
                rdAfternoon.buttonTintList = colorStateList

                setSpinnerListeners(container)
            }
            .addOnFailureListener { e ->
                Log.d("AdminFragment", "Error getting documents: " + e.message)
            }
    }
}