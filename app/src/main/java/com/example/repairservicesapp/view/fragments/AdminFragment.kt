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
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.repairservicesapp.R
import com.example.repairservicesapp.app.AppManager
import com.example.repairservicesapp.database.FirebaseUtils
import com.example.repairservicesapp.model.Booking
import com.example.repairservicesapp.model.Service
import com.example.repairservicesapp.model.Stats
import com.example.repairservicesapp.model.User
import com.example.repairservicesapp.util.MapUtils
import com.google.firebase.firestore.toObject



class AdminFragment : Fragment() {
    private lateinit var btnAddTechnician : Button
    private lateinit var btnAddService : Button
    private lateinit var btnUpdateService : Button
    private lateinit var spinnerServices : Spinner
    private var servicesNames = ArrayList<String>()

    private lateinit var txtPendingBookingsCounter: TextView
    private lateinit var txtAssignedBookingsCounter: TextView
    private lateinit var txtAwaitingBikesCounter: TextView
    private lateinit var txtInProcessBookingsCounter: TextView
    private lateinit var txtCompletedBookingsCounter: TextView
    private lateinit var txtCancelledBookingsCounter: TextView
    private lateinit var txtTotalRevenueCounter: TextView
    private lateinit var txtTotalHoursCounter: TextView
    private lateinit var txtTotalBookingsCounter: TextView
    private lateinit var txtTotalCustomersCounter: TextView
    private lateinit var txtTotalTechniciansCounter: TextView
    private lateinit var txtAvailableTechniciansCounter: TextView
    private lateinit var txtUnavailableTechniciansCounter: TextView

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

        txtPendingBookingsCounter = view.findViewById(R.id.txtStatPendingBookingsCounter)
        txtAssignedBookingsCounter = view.findViewById(R.id.txtStatAssignedBookingsCounter)
        txtAwaitingBikesCounter = view.findViewById(R.id.txtStatAwaitingBikeBookingsCounter)
        txtInProcessBookingsCounter = view.findViewById(R.id.txtStatInProcessBookingsCounter)
        txtCompletedBookingsCounter = view.findViewById(R.id.txtStatCompletedBookingsCounter)
        txtCancelledBookingsCounter = view.findViewById(R.id.txtStatCancelledBookingsCounter)
        txtTotalRevenueCounter = view.findViewById(R.id.txtStatTotalRevenueCounter)
        txtTotalHoursCounter = view.findViewById(R.id.txtStatTotalHoursCounter)
        txtTotalBookingsCounter = view.findViewById(R.id.txtStatTotalBookingsCounter)
        txtTotalCustomersCounter = view.findViewById(R.id.txtStatTotalCustomersCounter)
        txtTotalTechniciansCounter = view.findViewById(R.id.txtStatTotalTechniciansCounter)
        txtAvailableTechniciansCounter = view.findViewById(R.id.txtStatAvailableTechniciansCounter)
        txtUnavailableTechniciansCounter = view.findViewById(R.id.txtStatUnavailableTechniciansCounter)

        // Get all technicians and services
        getAllTechnicians()
        getAllServices()
        getStats()
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

    private fun getStats() {
        FirebaseUtils.firestore.collection("bookings")
            .get()
            .addOnSuccessListener { result ->
                val bookings = ArrayList<Booking>()
                for (document in result) {
                    val booking = document.toObject<Booking>()
                    bookings.add(booking)
                }
                getStatsFromBookings(bookings)
            }
            .addOnFailureListener { e ->
                Log.d("AdminFragment", "Error getting documents: " + e.message)
            }
    }

    private fun getStatsFromBookings(bookings: ArrayList<Booking>) {
        // Get the bookings and only updates the counters in AppManager.instance.stats when there is new data available.
        FirebaseUtils.firestore.collection("bookings")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("ScheduleFragment", "listen:error", e)
                    return@addSnapshotListener
                }
                if (snapshots != null) {
                    val stats = Stats()
                    for (document in snapshots) {
                        val booking = document.toObject<Booking>()
                        stats.totalBookings++
                        when (booking.bookingStatus) {
                            Booking.BookingStatus.PENDING -> stats.pending++
                            Booking.BookingStatus.ASSIGNED -> stats.assigned++
                            Booking.BookingStatus.AWAITING_BIKE -> stats.awaitingBikes++
                            Booking.BookingStatus.IN_PROCESS -> stats.inProcess++
                            Booking.BookingStatus.COMPLETED -> stats.completed++
                            Booking.BookingStatus.CANCELLED -> stats.cancelled++
                            else -> {
                                // Do nothing
                            }
                        }
                        stats.totalRevenue += booking.bookingCost
                        stats.totalHours += booking.bookingDuration
                        if (!stats.customers.contains(booking.customer?.getUserId())) {
                            stats.customers.add(booking.customer?.getUserId()!!)
                        }
                        if (!stats.technicians.contains(booking.technician?.getUserId())) {
                            stats.technicians.add(booking.technician?.getUserId()!!)
                        }
                        if (booking.technician?.userAvailability == 100) {
                            stats.availableTechnicians++
                        } else {
                            stats.unavailableTechnicians++
                        }
                    }
                    AppManager.instance.stats = stats
                    updateStatsUI()
                }
            }
    }

    private fun updateStatsUI() {
        txtPendingBookingsCounter.text = AppManager.instance.stats.pending.toString()
        txtAssignedBookingsCounter.text = AppManager.instance.stats.assigned.toString()
        txtAwaitingBikesCounter.text = AppManager.instance.stats.awaitingBikes.toString()
        txtInProcessBookingsCounter.text = AppManager.instance.stats.inProcess.toString()
        txtCompletedBookingsCounter.text = AppManager.instance.stats.completed.toString()
        txtCancelledBookingsCounter.text = AppManager.instance.stats.cancelled.toString()
        txtTotalRevenueCounter.text = "$${AppManager.instance.stats.totalRevenue}"
        txtTotalHoursCounter.text = AppManager.instance.stats.totalHours.toString()
        txtTotalBookingsCounter.text = AppManager.instance.stats.totalBookings.toString()
        txtTotalCustomersCounter.text = AppManager.instance.stats.customers.size.toString()
        txtTotalTechniciansCounter.text = AppManager.instance.stats.technicians.size.toString()
        txtAvailableTechniciansCounter.text = AppManager.instance.stats.availableTechnicians.toString()
        txtUnavailableTechniciansCounter.text = AppManager.instance.stats.unavailableTechnicians.toString()
    }
}