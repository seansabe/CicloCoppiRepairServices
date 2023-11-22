package com.example.repairservicesapp.database

import android.util.Log
import com.example.repairservicesapp.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

object FirebaseUtils {
    val fireStoreDatabase = Firebase.firestore

    fun addUser(user: User
    ) {
        val user = hashMapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "address" to user.address,
            "phoneNumber" to user.phoneNumber,
            "email" to user.email,
            "password" to user.password,
            "userType" to user.userType,
            "userAvailability" to user.userAvailability,
            "token" to FirebaseMessaging.getInstance().token.result.toString()
        )
        fireStoreDatabase.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("FirebaseUtils", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("FirebaseUtils", "Error adding document", e)
            }
    }

    fun getChatRoomId(userId1: Int, userId2: Int): String {
        return if (userId1.hashCode() < userId2.hashCode()) {
            "$userId1-$userId2"
        } else {
            "$userId2-$userId1"
        }
    }

    fun getChatRoomReference(chatRoomId: String) = fireStoreDatabase.collection("chatRooms").document(chatRoomId)
    fun getChatRoomMessageReference(chatRoomId: String) = getChatRoomReference(chatRoomId).collection("messages")
}