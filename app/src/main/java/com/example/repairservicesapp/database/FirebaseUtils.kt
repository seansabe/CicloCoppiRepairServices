package com.example.repairservicesapp.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


object FirebaseUtils {
    val fireStoreDatabase: FirebaseFirestore get() = Firebase.firestore

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