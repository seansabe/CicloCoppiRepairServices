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
import com.example.repairservicesapp.app.AppManager
import com.example.repairservicesapp.database.FirebaseUtils
import com.example.repairservicesapp.model.Booking
import com.example.repairservicesapp.model.Service
import com.example.repairservicesapp.model.User
import com.example.repairservicesapp.util.UnitsUtils
import com.google.api.Distribution.BucketOptions.Linear

class ServiceHistoryCustomerFragment : Fragment() {
    private lateinit var container : LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_service_history_customer, container, false)
        loadUI(view)
        return view
    }

    private fun isBookingCreatedByCurrentUser(customerData: Map<String, Any>): Boolean {
        val loggedInUser = AppManager.instance.user
        val customerFirstName = customerData["customerFirstName"] as String
        val customerLastName = customerData["customerLastName"] as String
        val customerAddress = customerData["customerAddress"] as String
        val customerPhoneNumber = customerData["customerPhoneNumber"] as String
        val customerEmail = customerData["customerEmail"] as String


        return loggedInUser?.firstName == customerFirstName &&
                loggedInUser.lastName == customerLastName &&
                loggedInUser.address == customerAddress &&
                loggedInUser.phoneNumber == customerPhoneNumber &&
                loggedInUser.email == customerEmail
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
                    if (isBookingCreatedByCurrentUser(customer)) {
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
                    //val txtCustomer = cardView.findViewById<TextView>(R.id.txtCustomer)
                    val txtBicycle = cardView.findViewById<TextView>(R.id.txtBicycle)
                    val txtServices = cardView.findViewById<TextView>(R.id.txtServices)
                    val txtEstCost = cardView.findViewById<TextView>(R.id.txtEstCost)
                    val txtEstDuration = cardView.findViewById<TextView>(R.id.txtEstTime)
                    val txtStatus = cardView.findViewById<TextView>(R.id.txtStatus)

                    val layoutCustomerRow = cardView.findViewById<LinearLayout>(R.id.layoutCustomerRow)
                    layoutCustomerRow.visibility = View.GONE

                    txtDropInTime.text = booking.dropInTime
                    txtDate.text = if (booking.bookingDate != null) "${booking.bookingDate}, ${booking.bookingTime}" else Booking.BookingStatus.PENDING.getStatusValue(requireContext())
                    txtTechnician.text = booking.technician?.let { "${it.firstName} ${it.lastName}" } ?: Booking.BookingStatus.PENDING.getStatusValue(requireContext())
                    //txtCustomer.text = "${booking.customer?.firstName} ${booking.customer?.lastName}"
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
                        Toast.makeText(requireContext(), "Appointment Cancelled", Toast.LENGTH_SHORT).show()
                    }

                    val btnOpenChatRead = cardView.findViewById<ImageButton>(R.id.btnOpenChatRead)
                    btnOpenChatRead.setOnClickListener {

                    }

                    val btnCallTechnician = cardView.findViewById<ImageButton>(R.id.btnCall)
                    btnCallTechnician.setOnClickListener {
                        Toast.makeText(requireContext(), "Calling Technician", Toast.LENGTH_SHORT).show()
                    }

                    if (booking.isCompleted) {
                        val btnRate = cardView.findViewById<ImageButton>(R.id.btnRate)
                        btnRate.setOnClickListener {
                            Toast.makeText(requireContext(), "Rate the Service", Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (booking.isCancelled) {
                        val layoutActionButtons = cardView.findViewById<LinearLayout>(R.id.layoutActionButtons)
                        layoutActionButtons.visibility = View.GONE
                    }

                    container.addView(cardView)
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
                Log.d("Firebase", "Error getting bookings", exception)
            }
    }
}