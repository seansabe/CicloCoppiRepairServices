package com.example.repairservicesapp.view

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.repairservicesapp.R
import com.example.repairservicesapp.app.AppManager
import com.example.repairservicesapp.data.PassUserAsIntent
import com.example.repairservicesapp.database.FirebaseUtils
import com.example.repairservicesapp.model.ChatRoom
import com.example.repairservicesapp.model.User
import com.example.repairservicesapp.util.StatusBarUtils.setStatusBarColor
import com.google.firebase.Timestamp

class ChatActivity : AppCompatActivity() {
    private lateinit var btnBack : ImageButton
    private lateinit var btnSend : ImageButton
    private lateinit var txtChatTitle : TextView
    private lateinit var edTxtMessage : EditText
    private lateinit var recyclerViewChat : RecyclerView
    private lateinit var chatRoomId : String
    private lateinit var chatRoom : ChatRoom
    private var customer = User()

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
        initChatRoom()
    }

    private fun loadUI() {
        txtChatTitle = findViewById(R.id.txtChatTitle)
        edTxtMessage = findViewById(R.id.edTxtMessage)
        // Get intent data
        customer = PassUserAsIntent.get(intent)
        txtChatTitle.text = "${customer.firstName} ${customer.lastName}"
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
        btnSend = findViewById(R.id.btnSend)
    }

    private fun initChatRoom() {
        // Get chat room id
        chatRoomId = FirebaseUtils.getChatRoomId(AppManager.instance.user.getUserId(), customer.getUserId())
        // Get chat room
        FirebaseUtils.getChatRoomReference(chatRoomId).get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                chatRoom = documentSnapshot.toObject(ChatRoom::class.java)!!
                Log.d("ChatActivity", "Chat room exists with ${customer.firstName} ${customer.lastName} and ${AppManager.instance.user.firstName} ${AppManager.instance.user.lastName}")
            } else {
                chatRoom = ChatRoom(chatRoomId, arrayListOf(AppManager.instance.user.getUserId(), customer.getUserId()), Timestamp.now(), 0)
                FirebaseUtils.getChatRoomReference(chatRoomId).set(chatRoom).addOnSuccessListener {
                    Log.d("ChatActivity", "Chat room created")
                }
            }
        }
    }
}