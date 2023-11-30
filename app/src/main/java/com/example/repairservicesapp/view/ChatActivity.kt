package com.example.repairservicesapp.view

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.repairservicesapp.R
import com.example.repairservicesapp.adapter.ChatRecyclerAdapter
import com.example.repairservicesapp.app.AppManager
import com.example.repairservicesapp.data.PassUserAsIntent
import com.example.repairservicesapp.database.FirebaseUtils
import com.example.repairservicesapp.model.ChatMessage
import com.example.repairservicesapp.model.ChatRoom
import com.example.repairservicesapp.model.User
import com.example.repairservicesapp.util.StatusBarUtils.setStatusBarColor
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity() {
    private lateinit var btnBack : ImageButton
    private lateinit var btnSend : ImageButton
    private lateinit var txtChatTitle : TextView
    private lateinit var edTxtMessage : EditText
    private lateinit var recyclerViewChat : RecyclerView
    private lateinit var chatRoomId : String
    private lateinit var chatRoom : ChatRoom
    private var receiver = User()
    private var sender = AppManager.instance.user
    private var chatMessages = arrayListOf<ChatMessage>()
    private lateinit var chatRecyclerAdapter : ChatRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        // Custom status and nav bar
        setStatusBarColor(
            window,
            ContextCompat.getColor(this, R.color.white),
            ContextCompat.getColor(this, R.color.white)
        )
        loadUI()
        createEvents()
        initChatRoom()
        setupChatRecyclerView()
    }

    private fun loadUI() {
        txtChatTitle = findViewById(R.id.txtChatTitle)
        edTxtMessage = findViewById(R.id.edTxtMessage)
        // Get intent data
        receiver = PassUserAsIntent.get(intent)
        txtChatTitle.text = if (sender.isCustomer) ("${receiver.firstName} (${getString(R.string.txtTechnicianTitle)})") else ("${receiver.firstName} (${getString(R.string.txtCustomerTitle)})")
        btnBack = findViewById(R.id.btnBack)
        btnSend = findViewById(R.id.btnSend)
    }

    private fun createEvents() {
        btnBack.setOnClickListener {
            finish()
        }

        btnSend.setOnClickListener {
            val message = edTxtMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                edTxtMessage.text.clear()
            }
        }
    }

    private fun sendMessage(message : String) {
        chatRoom.setLastMessageSenderId(sender.getUserId())
        chatRoom.setLastMessageTimestamp(Timestamp.now())
        FirebaseUtils.getChatRoomReference(chatRoomId).set(chatRoom).addOnSuccessListener {
            Log.d("ChatActivity", "Chat room updated")
        }
        val chatMessage = ChatMessage(message, sender.getUserId(), Timestamp.now())
        FirebaseUtils.getChatRoomMessageReference(chatRoomId).add(chatMessage).addOnCompleteListener(
            this
        ) { task ->
            if (task.isSuccessful) {
                Log.d("ChatActivity", "Message added")
            } else {
                Log.d("ChatActivity", "Message not added")
            }
        }
    }

    private fun setupChatRecyclerView() {
        recyclerViewChat = findViewById(R.id.recyclerViewChat)
        // Get list of chats from Firebase
        FirebaseUtils.getChatRoomMessageReference(chatRoomId).orderBy("timestamp", Query.Direction.ASCENDING).addSnapshotListener { value, error ->
            if (error != null) {
                Log.d("ChatActivity", "Listen failed")
                return@addSnapshotListener
            }
            chatMessages.clear()
            for (document in value!!) {
                val chatMessage = document.toObject(ChatMessage::class.java)
                chatMessages.add(chatMessage)
            }
            chatRecyclerAdapter = ChatRecyclerAdapter(chatMessages, applicationContext)
            val linearLayoutManager = LinearLayoutManager(this)
            linearLayoutManager.stackFromEnd = true
            recyclerViewChat.layoutManager = linearLayoutManager
            recyclerViewChat.adapter = chatRecyclerAdapter
            recyclerViewChat.scrollToPosition(chatMessages.size - 1)
            chatRecyclerAdapter.listen()
        }
    }

    private fun initChatRoom() {
        // Get chat room id
        chatRoomId = FirebaseUtils.getChatRoomId(sender.getUserId(), receiver.getUserId())
        // Get chat room
        FirebaseUtils.getChatRoomReference(chatRoomId).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                chatRoom = documentSnapshot.toObject(ChatRoom::class.java)!!
                Log.d("ChatActivity", "Chat room exists with ${receiver.firstName} ${receiver.lastName} and ${sender.firstName} ${sender.lastName}")
            } else {
                chatRoom = ChatRoom(chatRoomId, arrayListOf(sender.getUserId(), receiver.getUserId()), Timestamp.now(), "")
                FirebaseUtils.getChatRoomReference(chatRoomId).set(chatRoom).addOnSuccessListener {
                    Log.d("ChatActivity", "Chat room created")
                }
            }
        }
    }
}