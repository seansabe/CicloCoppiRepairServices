package com.example.repairservicesapp.model

import com.google.firebase.Timestamp

class ChatMessage {
    private var message : String = ""
    private var senderId : String = ""
    private var timestamp : Timestamp = Timestamp.now()

    constructor()

    constructor(message: String, senderId: String, timestamp: Timestamp) {
        this.message = message
        this.senderId = senderId
        this.timestamp = timestamp
    }

    fun getMessage() : String {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getSenderId() : String {
        return senderId
    }

    fun setSenderId(senderId: String) {
        this.senderId = senderId
    }

    fun getTimestamp() : Timestamp {
        return timestamp
    }

    fun setTimestamp(timestamp: Timestamp) {
        this.timestamp = timestamp
    }
}

