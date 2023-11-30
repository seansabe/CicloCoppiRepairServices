package com.example.repairservicesapp.view.fragments

import android.content.Intent
import android.net.Uri
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
import com.example.repairservicesapp.data.PassUserAsIntent
import com.example.repairservicesapp.database.FirebaseUtils
import com.example.repairservicesapp.model.Booking
import com.example.repairservicesapp.model.Service
import com.example.repairservicesapp.util.MapUtils
import com.example.repairservicesapp.util.UnitsUtils
import com.example.repairservicesapp.view.ChatActivity
import com.google.firebase.firestore.QuerySnapshot

class ServiceHistoryCustomerFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_service_history_customer, container, false)
        loadUI(view)
        return view
    }

    private fun loadUI(view: View) {
        container = view.findViewById(R.id.layoutCards)
        containerHistory = view.findViewById(R.id.layoutCardsHistory)
        txtNothingHereYetHistory = view.findViewById(R.id.txtNothingHereYetHistory)
        txtNothingHereYetBookings = view.findViewById(R.id.txtNothingHereYetBookings)
        // Retrieving Bookings from Firebase
        getBookings()
    }

    private fun getBookings() {
        FirebaseUtils.firestore.collection("bookings")
            .whereEqualTo("customer.userId", AppManager.instance.user.getUserId())
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

    private fun buildCards(result : QuerySnapshot) {
        // Convert each booking to Booking object and add to bookingsList
        for (document in result) {
            val bookingId = document.id
            val bookingData = document.data
            val servicesData = bookingData["services"] as? List<Map<String, Any>> ?: emptyList()
            val customer = bookingData["customer"] as Map<String, Any>
            val technician = bookingData["technician"] as? Map<String, Any>
            val servicesList = ArrayList<Service>()
            for (serviceMap in servicesData) {
                val service = MapUtils.mapToServiceObject(serviceMap)
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
                    MapUtils.mapToUserObject(customer),
                )
                bookingsList.add(booking)
            } else {
                // Convert bookingData to Booking object and add to bookingsList
                val booking = Booking(
                    bookingId,
                    bookingData["dropInTime"] as String,
                    bookingData["bookingDate"] as? String,
                    bookingData["bookingTime"] as? String,
                    Booking.BookingStatus.valueOf(bookingData["bookingStatus"] as String),
                    bookingData["bookingCost"] as Double,
                    (bookingData["bookingDuration"] as Long).toInt(),
                    bookingData["bikeType"] as String,
                    bookingData["bikeColor"] as String,
                    bookingData["bikeWheelSize"] as String,
                    bookingData["comments"] as String,
                    servicesList,
                    MapUtils.mapToUserObject(customer),
                    MapUtils.mapToUserObject(technician)
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
            txtBicycle.text = "${booking.bikeType} ${booking.bikeWheelSize}, ${booking.bikeColor}\n${getString(R.string.txtNote)}: ${booking.comments}"
            txtServices.text = ""
            for (service in booking.services!!) {
                txtServices.text = txtServices.text.toString() + "${service.serviceName}, "
            }

            txtEstCost.text = "$${booking.bookingCost}"
            txtEstDuration.text = "${booking.bookingDuration} h"
            txtStatus.text = booking.bookingStatus?.getStatusValue(requireContext())

            val btnCancelAppointment = cardView.findViewById<ImageButton>(R.id.btnCancelBooking)
            btnCancelAppointment.setOnClickListener {
                handleBookingCancellation(booking, cardView)
            }

            val btnOpenChatRead = cardView.findViewById<ImageButton>(R.id.btnOpenChatRead)
            btnOpenChatRead.setOnClickListener {
                if (booking.technician != null) {
                    // Pass the customer object to the ChatActivity and start it
                    val intent = Intent(requireContext(), ChatActivity::class.java)
                    PassUserAsIntent.send(intent, booking.technician!!)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), requireContext().getString(R.string.txtTechnicianNotAssignedYet), Toast.LENGTH_SHORT).show()
                }
            }

            val btnCallTechnician = cardView.findViewById<ImageButton>(R.id.btnCall)
            btnCallTechnician.setOnClickListener {
                if(booking.technician != null) {
                    booking.technician?.phoneNumber?.let { phoneNumber -> initiateCall(phoneNumber) }
                } else {
                    Toast.makeText(requireContext(), requireContext().getString(R.string.txtTechnicianNotAssignedYet), Toast.LENGTH_SHORT).show()
                }
            }

            if (booking.isCancelled) {
                txtDate.text = if (booking.bookingDate != null) "${booking.bookingDate}, ${booking.bookingTime}" else Booking.BookingStatus.CANCELLED.getStatusValue(requireContext())
                txtTechnician.text = booking.technician?.let { "${it.firstName} ${it.lastName}" } ?: Booking.BookingStatus.CANCELLED.getStatusValue(requireContext())
                booking.setBookingStatus(Booking.BookingStatus.CANCELLED)
                txtNothingHereYetHistory.visibility = View.GONE
                txtStatus.text = booking.bookingStatus?.getStatusValue(requireContext())
                val layoutActionButtons = cardView.findViewById<LinearLayout>(R.id.layoutActionButtons)
                layoutActionButtons.visibility = View.GONE
            }

            if (booking.isPending || booking.isAccepted || booking.isAssigned || booking.isAwaitingBike || booking.isInProcess) {
                txtNothingHereYetBookings.visibility = View.GONE
            }

            if (booking.isCancelled || booking.isCompleted) {
                containerHistory.addView(cardView)
            } else {
                container.addView(cardView)
            }

            if (booking.isCompleted) {
                val btnRate = cardView.findViewById<ImageButton>(R.id.btnRate)
                btnRate.setOnClickListener {
                    Toast.makeText(requireContext(), "Rate the Service", Toast.LENGTH_SHORT).show()
                }
                cardView.findViewById<ImageButton>(R.id.btnOpenChatRead).visibility = View.GONE
                cardView.findViewById<ImageButton>(R.id.btnCall).visibility = View.GONE
                cardView.findViewById<ImageButton>(R.id.btnCancelBooking).visibility = View.GONE
            }
        }
    }

    private fun handleBookingCancellation(booking: Booking, cardView: View) {
        FirebaseUtils.firestore.collection("bookings").document(booking.bookingId!!)
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

    private fun initiateCall(phoneNumber: String) {
        // Call the phone number
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }
}