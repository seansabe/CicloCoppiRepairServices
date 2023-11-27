package com.example.repairservicesapp.database

import android.util.Log
import com.example.repairservicesapp.model.Service
import com.example.repairservicesapp.model.User
import com.example.repairservicesapp.util.MapUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase


object FirebaseUtils {
    val firestore: FirebaseFirestore get() = Firebase.firestore

    fun getChatRoomId(userId1: String, userId2: String): String {
        return if (userId1.hashCode() < userId2.hashCode()) {
            "$userId1-$userId2"
        } else {
            "$userId2-$userId1"
        }
    }

    fun getChatRoomReference(chatRoomId: String) = firestore.collection("chatRooms").document(chatRoomId)
    fun getChatRoomMessageReference(chatRoomId: String) = getChatRoomReference(chatRoomId).collection("messages")

    fun setServiceId(serviceId: String) {
        // Update serviceId in Firebase
        firestore.collection("services")
            .document(serviceId)
            .update("serviceId", serviceId)
            .addOnSuccessListener {result ->
                Log.d(
                    "AddServiceFragment",
                    "serviceId: $result updated"
                )
            }
            .addOnFailureListener { e: Exception ->
                Log.d(
                    "AddServiceFragment",
                    "serviceId update error: " + e.message
                )
            }
    }

    fun setUserId(userId: String) {
        // Update userId in Firebase
        firestore.collection("users")
            .document(userId)
            .update("userId", userId)
            .addOnSuccessListener {result ->
                Log.d(
                    "RegistrationActivity",
                    "userId: $result updated"
                )
            }
            .addOnFailureListener { e: Exception ->
                Log.d(
                    "RegistrationActivity",
                    "userId update error: " + e.message
                )
            }
    }

    // Get all users from Firebase of type TECHNICIAN and return an ArrayList<User>
    fun getAllTechnicians(): ArrayList<User> {
        val technicians = ArrayList<User>()
        firestore.collection("users")
            .whereEqualTo("userType", User.UserType.TECHNICIAN)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val technician = document.toObject<User>()
                    technicians.add(technician)
                }
            }
            .addOnFailureListener { e ->
                Log.d("AdminFragment", "Error getting documents: " + e.message)
            }
        Log.d("AdminFragment", "getAllTechnicians: ${technicians.size}")
        return technicians
    }

    fun getAllServices(): ArrayList<Service> {
        val services = ArrayList<Service>()
        firestore.collection("services")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val service = document.toObject<Service>()
                    services.add(service)
                }
            }
            .addOnFailureListener { e ->
                Log.d("AdminFragment", "Error getting documents: " + e.message)
            }
        Log.d("AdminFragment", "getAllServices: ${services.size}")
        return services
    }
}