package com.example.repairservicesapp.view.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.repairservicesapp.R
import com.example.repairservicesapp.app.AppManager
import com.example.repairservicesapp.data.PassUserAsIntent
import com.example.repairservicesapp.database.FirebaseUtils
import com.example.repairservicesapp.model.Booking
import com.example.repairservicesapp.model.Service
import com.example.repairservicesapp.model.User
import com.example.repairservicesapp.util.UnitsUtils
import com.example.repairservicesapp.view.ChatActivity
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.QuerySnapshot

class ServiceHistoryTechnicianFragment : Fragment() {
    private lateinit var container : LinearLayout
    private lateinit var containerHistory : LinearLayout
    private lateinit var txtNothingHereYetHistory : TextView
    private lateinit var txtNothingHereYetBookings : TextView
    private var bookingsList = ArrayList<Booking>()
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
        containerHistory = view.findViewById(R.id.layoutCardsHistory)
        txtNothingHereYetHistory = view.findViewById(R.id.txtNothingHereYetHistory)
        txtNothingHereYetBookings = view.findViewById(R.id.txtNothingHereYetBookings)
        // Retrieving Bookings from Firebase
        getFirebaseData()
    }

    private fun getFirebaseData() {
        if (AppManager.instance.user.isTechnician) {
            FirebaseUtils.fireStoreDatabase.collection("bookings")
                .where(Filter.or(
                    Filter.equalTo("technician", null),
                    Filter.equalTo("technician.technicianId", AppManager.instance.user.getUserId())
                ))
                .orderBy("bookingDate")
                .orderBy("bookingTime")
                .get()
                .addOnSuccessListener { result ->
                    buildCards(result)
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                    Log.d("Firebase", "Error getting bookings", exception)
                }
        } else {
            FirebaseUtils.fireStoreDatabase.collection("bookings")
                .orderBy("bookingDate")
                .orderBy("bookingTime")
                .get()
                .addOnSuccessListener { result ->
                    buildCards(result)
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                    Log.d("Firebase", "Error getting bookings", exception)
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun buildCards(result : QuerySnapshot) {
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
        for (booking in bookingsList) {
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
            txtBicycle.text = "${booking.bikeType} ${booking.bikeWheelSize}, ${booking.bikeColor}\n${booking.comments}"
            txtServices.text = ""
            for (service in booking.services!!) {
                txtServices.text = txtServices.text.toString() + "${service.serviceName}, "
            }

            txtEstCost.text = "$${booking.bookingCost}"
            txtEstDuration.text = "${booking.bookingDuration} h"
            txtStatus.text = booking.bookingStatus?.getStatusValue(requireContext())

            // Button to cancel the booking
            val btnCancelAppointment = cardView.findViewById<ImageButton>(R.id.btnCancelBooking)
            btnCancelAppointment.setOnClickListener {
                handleBookingCancellation(booking, cardView)
            }

            // Button to open the chat with the customer
            val btnOpenChatRead = cardView.findViewById<ImageButton>(R.id.btnOpenChatRead)
            btnOpenChatRead.setOnClickListener {
                // Pass the customer object to the ChatActivity and start it
                val intent = Intent(requireContext(), ChatActivity::class.java)
                PassUserAsIntent.send(intent, booking.customer!!)
                startActivity(intent)
            }

            // Button to call the customer
            val btnCallCustomer = cardView.findViewById<ImageButton>(R.id.btnCall)
            btnCallCustomer.setOnClickListener {
                booking.customer?.phoneNumber?.let { phoneNumber -> initiateCall(phoneNumber) }
            }

            // Button to edit the booking
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

            // If the booking is cancelled will be displayed in the history container
            if (booking.isCancelled) {
                txtDate.text = if (booking.bookingDate != null) "${booking.bookingDate}, ${booking.bookingTime}" else Booking.BookingStatus.CANCELLED.getStatusValue(requireContext())
                txtTechnician.text = booking.technician?.let { "${it.firstName} ${it.lastName}" } ?: Booking.BookingStatus.CANCELLED.getStatusValue(requireContext())
                booking.setBookingStatus(Booking.BookingStatus.CANCELLED)
            }

            // If the booking is pending, accepted, assigned, awaiting bike or in process will be displayed in the bookings container
            if (booking.isPending || booking.isAccepted || booking.isAssigned || booking.isAwaitingBike || booking.isInProcess) {
                txtNothingHereYetBookings.visibility = View.GONE
            }

            // If the booking is completed or cancelled the buttons will be hidden
            if (booking.isCancelled || booking.isCompleted) {
                txtNothingHereYetHistory.visibility = View.GONE
                txtStatus.text = booking.bookingStatus?.getStatusValue(requireContext())
                val layoutActionButtons = cardView.findViewById<LinearLayout>(R.id.layoutActionButtons)
                layoutActionButtons.visibility = View.GONE
                containerHistory.addView(cardView)
            } else {
                container.addView(cardView)
            }
        }
    }

    private fun handleBookingCancellation(booking: Booking, cardView: View) {
        FirebaseUtils.fireStoreDatabase.collection("bookings").document(booking.bookingId!!)
            .update("bookingStatus", Booking.BookingStatus.CANCELLED.getStatusValue(requireContext()))
            .addOnSuccessListener {
                // Remove the canceled booking from container and add it to containerHistory
                container.removeView(cardView)
                cardView.findViewById<TextView>(R.id.txtDate).text = Booking.BookingStatus.CANCELLED.getStatusValue(requireContext())
                cardView.findViewById<TextView>(R.id.txtTechnician).text = Booking.BookingStatus.CANCELLED.getStatusValue(requireContext())
                txtNothingHereYetHistory.visibility = View.GONE
                booking.setBookingStatus(Booking.BookingStatus.CANCELLED)
                cardView.findViewById<TextView>(R.id.txtStatus).text = booking.bookingStatus?.getStatusValue(requireContext())
                containerHistory.addView(cardView)
                val layoutActionButtons = cardView.findViewById<LinearLayout>(R.id.layoutActionButtons)
                layoutActionButtons.visibility = View.GONE
                Toast.makeText(requireContext(), context?.getString(R.string.txtBookingCancelled), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error updating booking status", exception)
            }
    }

    // This function is used to call the customer
    private fun initiateCall(phoneNumber: String) {
        // Call the phone number
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }
}