package com.example.repairservicesapp.view

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.repairservicesapp.R
import com.example.repairservicesapp.data.PassUserAsIntent
import com.example.repairservicesapp.database.DatabaseHelper
import com.example.repairservicesapp.model.User
import com.example.repairservicesapp.util.StatusBarUtils.setStatusBarColor

class ChatActivity : AppCompatActivity() {
    private lateinit var btnBack : ImageButton
    private lateinit var txtChatTitle : TextView
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
    }

    private fun loadUI() {
        txtChatTitle = findViewById(R.id.txtChatTitle)
        // Get intent data
        customer = PassUserAsIntent.get(intent)
        txtChatTitle.text = "${customer.firstName} ${customer.lastName}"
        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }
    }
}