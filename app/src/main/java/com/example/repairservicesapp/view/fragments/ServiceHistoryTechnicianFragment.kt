package com.example.repairservicesapp.view.fragments

import android.app.Activity
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
import com.example.repairservicesapp.model.ChatRoom
import com.example.repairservicesapp.model.Service
import com.example.repairservicesapp.util.MapUtils
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
    private lateinit var cardView : View
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
            FirebaseUtils.firestore.collection("bookings")
                .where(Filter.or(
                    Filter.equalTo("technician", null),
                    Filter.equalTo("technician.userId", AppManager.instance.user.getUserId())
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
            FirebaseUtils.firestore.collection("bookings")
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

    private fun buildCards(result : QuerySnapshot) {
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
                getNewMessagesCounter(booking.customer?.getUserId()!!)
            }
        }

        // Display Bookings Using Cards
        for (booking in bookingsList) {
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, UnitsUtils.dpToPx(10, requireContext()), 0, 0)
            cardView = layoutInflater.inflate(R.layout.custom_card, null)
            cardView.layoutParams = layoutParams

            cardView.findViewById<ImageButton>(R.id.btnOpenChatRead).setImageResource(R.drawable.outline_chat_24)

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
            txtBicycle.text = "${booking.bikeType} ${booking.bikeWheelSize}, ${booking.bikeColor}\n${getString(R.string.txtNote)}: ${booking.comments}"
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
                if (booking.technician != null) {
                    // Pass the customer object to the ChatActivity and start it
                    val intent = Intent(requireContext(), ChatActivity::class.java)
                    PassUserAsIntent.send(intent, booking.customer!!)
                    startActivityForResult(intent, REQUEST_CHAT)
                } else {
                    Toast.makeText(requireContext(), context?.getString(R.string.txtTechnicianNotAssignedYet), Toast.LENGTH_SHORT).show()
                }
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

    // This function is used to call the customer
    private fun initiateCall(phoneNumber: String) {
        // Call the phone number
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    private fun getNewMessagesCounter(receiverId: String) {
        val chatRoomId = FirebaseUtils.getChatRoomId(AppManager.instance.user.getUserId(), receiverId)
        // Get the number of unread messages
        FirebaseUtils.firestore.collection("chatRooms").document(chatRoomId)
            .get()
            .addOnSuccessListener { result ->
                val chatRoom = result.toObject(ChatRoom::class.java)
                if (chatRoom != null) {
                    val newMessagesCounter = chatRoom.getUnreadMessages()
                    if (chatRoom.getLastMessageSenderId() != AppManager.instance.user.getUserId() && newMessagesCounter > 0) {
                        cardView.findViewById<ImageButton>(R.id.btnOpenChatRead).setImageResource(R.drawable.outline_mark_unread_chat_alt_24)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ServiceHistoryTechnician", "Error getting chat room", exception)
            }
    }

    companion object {
        const val REQUEST_CHAT = 8888
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CHAT && resultCode == Activity.RESULT_OK) {
            // Handle the result data here
            //val value = data?.getStringExtra("read")
            cardView.findViewById<ImageButton>(R.id.btnOpenChatRead).setImageResource(R.drawable.outline_chat_24)
        }
    }
}