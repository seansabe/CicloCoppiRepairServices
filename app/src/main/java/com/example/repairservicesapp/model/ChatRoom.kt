package com.example.repairservicesapp.model

import com.google.firebase.Timestamp

class ChatRoom {
    private var chatRoomId: String = ""
    private var userIds: ArrayList<String> = ArrayList()
    private var lastMessageTimestamp: Timestamp = Timestamp.now()
    private var lastMessageSenderId: String = ""
    private var unreadMessages: Int = 0

    constructor()

    constructor(chatRoomId: String, userIds: ArrayList<String>, lastMessageTimestamp: Timestamp, lastMessageSenderId: String) {
        this.chatRoomId = chatRoomId
        this.userIds = userIds
        this.lastMessageTimestamp = lastMessageTimestamp
        this.lastMessageSenderId = lastMessageSenderId
        this.unreadMessages = 0
    }

    fun getChatRoomId(): String {
        return chatRoomId
    }

    fun setChatRoomId(chatRoomId: String) {
        this.chatRoomId = chatRoomId
    }

    fun getUserIds(): ArrayList<String> {
        return userIds
    }

    fun setUserIds(userIds: ArrayList<String>) {
        this.userIds = userIds
    }

    fun getLastMessageTimestamp(): Timestamp {
        return lastMessageTimestamp
    }

    fun setLastMessageTimestamp(lastMessageTimestamp: Timestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp
    }

    fun getLastMessageSenderId(): String {
        return lastMessageSenderId
    }

    fun setLastMessageSenderId(lastMessageSenderId: String) {
        this.lastMessageSenderId = lastMessageSenderId
    }

    fun getUnreadMessages(): Int {
        return unreadMessages
    }

    fun setUnreadMessages(unreadMessages: Int) {
        this.unreadMessages = unreadMessages
    }
}