package com.example.repairservicesapp.database

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseUtils {
    val firestore: FirebaseFirestore get() = FirebaseFirestore.getInstance()

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
}