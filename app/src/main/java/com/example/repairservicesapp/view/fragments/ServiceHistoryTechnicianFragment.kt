package com.example.repairservicesapp.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.repairservicesapp.R
import com.example.repairservicesapp.database.FirebaseUtils
import com.example.repairservicesapp.model.Booking
import com.example.repairservicesapp.model.Service
import com.example.repairservicesapp.model.User
import com.example.repairservicesapp.util.UnitsUtils

class ServiceHistoryTechnicianFragment : Fragment() {
    private var cardCount = 0
    private lateinit var container : LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_service_history_technician, container, false)
        loadUI(view)
        return view
    }

    private fun loadUI(view: View) {
        container = view.findViewById(R.id.layoutCards)
        // Retrieving Bookings from Firebase
        val bookingsList = mutableListOf<Booking>()

        FirebaseUtils.fireStoreDatabase.collection("bookings")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val bookingId = document.id
                    val bookingData = document.data
                    val servicesData = bookingData["services"] as? List<Map<String, Any>> ?: emptyList()
                    val customer = bookingData["customer"] as Map<String, Any>
                    val technician = bookingData["technician"] as? Map<String, Any>
                    val servicesList = ArrayList<Service>()
                    for (serviceMap in servicesData) {
                        val service = Service(
                            (serviceMap["serviceId"] as Long).toInt(),
                            serviceMap["serviceName"].toString(),
                            serviceMap["serviceDescription"].toString(),
                            (serviceMap["servicePrice"] as? Double) ?: 0.0,
                            (serviceMap["serviceDuration"] as? Long)?.toInt() ?: 0
                        )
                        servicesList.add(service)
                    }

                    if (technician == null) {
                        // Convert bookingData to Booking object and add to bookingsList
                        val booking = Booking(
                            bookingId,
                            bookingData["dropInTime"] as String,
                            Booking.BookingStatus.valueOf(bookingData["bookingStatus"] as String),
                            bookingData["bookingCost"] as Double,
                            (bookingData["bookingDuration"] as Long).toInt(),
                            bookingData["bikeType"] as String,
                            bookingData["bikeColor"] as String,
                            bookingData["bikeWheelSize"] as String,
                            bookingData["comments"] as String,
                            servicesList,
                            User(
                                customer["customerFirstName"] as String,
                                customer["customerLastName"] as String,
                                customer["customerAddress"] as String,
                                customer["customerPhoneNumber"] as String,
                                customer["customerEmail"] as String
                            )
                        )
                        bookingsList.add(booking)
                    } else {
                        // Convert bookingData to Booking object and add to bookingsList
                        val booking = Booking(
                            bookingId,
                            bookingData["dropInTime"] as String,
                            bookingData["bookingDate"] as String,
                            bookingData["bookingTime"] as String,
                            Booking.BookingStatus.valueOf(bookingData["bookingStatus"] as String),
                            bookingData["bookingCost"] as Double,
                            (bookingData["bookingDuration"] as Long).toInt(),
                            bookingData["bikeType"] as String,
                            bookingData["bikeColor"] as String,
                            bookingData["bikeWheelSize"] as String,
                            bookingData["comments"] as String,
                            servicesList,
                            User(
                                customer["customerFirstName"] as String,
                                customer["customerLastName"] as String,
                                customer["customerAddress"] as String,
                                customer["customerPhoneNumber"] as String,
                                customer["customerEmail"] as String
                            ),
                            User(
                                technician["technicianFirstName"] as? String,
                                technician["technicianLastName"] as? String,
                                technician["technicianAddress"] as? String,
                                technician["technicianPhoneNumber"] as? String,
                                technician["technicianEmail"] as? String
                            )
                        )
                        bookingsList.add(booking)
                    }
                }

                // Display Bookings Using Cards
                for (i in bookingsList.size - 1 downTo 0) {
                    val booking = bookingsList[i]
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.setMargins(0, UnitsUtils.dpToPx(10, requireContext()), 0, 0)
                    val cardView = layoutInflater.inflate(R.layout.custom_card, null)
                    cardView.layoutParams = layoutParams

                    val txtDate = cardView.findViewById<TextView>(R.id.txtDate)
                    val txtDropInTime = cardView.findViewById<TextView>(R.id.txtSelectedDropInFrame)
                    val txtTechnician = cardView.findViewById<TextView>(R.id.txtTechnician)
                    val txtCustomer = cardView.findViewById<TextView>(R.id.txtCustomer)
                    val txtBicycle = cardView.findViewById<TextView>(R.id.txtBicycle)
                    val txtServices = cardView.findViewById<TextView>(R.id.txtServices)
                    val txtEstCost = cardView.findViewById<TextView>(R.id.txtEstCost)
                    val txtEstDuration = cardView.findViewById<TextView>(R.id.txtEstTime)
                    val txtStatus = cardView.findViewById<TextView>(R.id.txtStatus)

                    txtDropInTime.text = booking.dropInTime
                    txtDate.text = if (booking.bookingDate != null) "${booking.bookingDate}, ${booking.bookingTime}" else Booking.BookingStatus.PENDING.getStatusValue(requireContext())
                    txtTechnician.text = booking.technician?.let { "${it.firstName} ${it.lastName}" } ?: Booking.BookingStatus.PENDING.getStatusValue(requireContext())
                    txtCustomer.text = "${booking.customer?.firstName} ${booking.customer?.lastName}"
                    txtBicycle.text =
                        "${booking.bikeType} ${booking.bikeWheelSize}, ${booking.bikeColor}\n${booking.comments}"
                    txtServices.text = ""
                    for (service in booking.services!!) {
                        txtServices.text =
                            txtServices.text.toString() + "${service.serviceName}, "
                    }

                    txtEstCost.text = "$${booking.bookingCost}"
                    txtEstDuration.text = "${booking.bookingDuration} h"
                    txtStatus.text = booking.bookingStatus?.getStatusValue(requireContext())

                    val btnCancelAppointment = cardView.findViewById<ImageButton>(R.id.btnCancelAppointment)
                    btnCancelAppointment.setOnClickListener {
                        Toast.makeText(requireContext(), "Cancel Appointment", Toast.LENGTH_SHORT).show()
                    }

                    val btnOpenChatRead = cardView.findViewById<ImageButton>(R.id.btnOpenChatRead)
                    btnOpenChatRead.setOnClickListener {
                        Toast.makeText(requireContext(), "Chat with " + booking.customer?.firstName, Toast.LENGTH_SHORT).show()
                    }

                    val btnCallTechnician = cardView.findViewById<ImageButton>(R.id.btnCall)
                    btnCallTechnician.setOnClickListener {
                        Toast.makeText(requireContext(), "Call " + booking.customer?.firstName, Toast.LENGTH_SHORT).show()
                    }

                    val btnEdit = cardView.findViewById<ImageButton>(R.id.btnRate)
                    btnEdit.setImageResource(R.drawable.outline_edit_24)
                    btnEdit.setOnClickListener {
                        val fragment = EditBookingFragment()
                        val bundle = Bundle()
                        bundle.putParcelable("selectedBooking", booking)
                        fragment.arguments = bundle
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_view, fragment)
                            .addToBackStack(null)
                            .commit()
                    }

                    // Add the cardView to the layout
                    container.addView(cardView)
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
                Log.d("Firebase", "Error getting bookings", exception)
            }
    }
}