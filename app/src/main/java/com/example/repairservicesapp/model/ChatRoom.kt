package com.example.repairservicesapp.model

import com.google.firebase.Timestamp

class ChatRoom {
    private var chatRoomId: String = ""
    private var userIds: ArrayList<Int> = ArrayList()
    private var lastMessageTimestamp: Timestamp = Timestamp.now()
    private var lastMessageSenderId: Int = 0

    constructor()

    constructor(chatRoomId: String, userIds: ArrayList<Int>, lastMessageTimestamp: Timestamp, lastMessageSenderId: Int) {
        this.chatRoomId = chatRoomId
        this.userIds = userIds
        this.lastMessageTimestamp = lastMessageTimestamp
        this.lastMessageSenderId = lastMessageSenderId
    }

    fun getChatRoomId(): String {
        return chatRoomId
    }

    fun setChatRoomId(chatRoomId: String) {
        this.chatRoomId = chatRoomId
    }

    fun getUserIds(): ArrayList<Int> {
        return userIds
    }

    fun setUserIds(userIds: ArrayList<Int>) {
        this.userIds = userIds
    }

    fun getLastMessageTimestamp(): Timestamp {
        return lastMessageTimestamp
    }

    fun setLastMessageTimestamp(lastMessageTimestamp: Timestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp
    }

    fun getLastMessageSenderId(): Int {
        return lastMessageSenderId
    }

    fun setLastMessageSenderId(lastMessageSenderId: Int) {
        this.lastMessageSenderId = lastMessageSenderId
    }
}