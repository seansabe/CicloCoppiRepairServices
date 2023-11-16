package com.example.repairservicesapp.data

import android.content.Intent
import com.example.repairservicesapp.model.User

object PassUserAsIntent {
    fun send(intent: Intent, user: User) {
        intent.putExtra("userId", user.getUserId())
        intent.putExtra("firstName", user.firstName)
        intent.putExtra("lastName", user.lastName)
        intent.putExtra("address", user.address)
        intent.putExtra("phoneNumber", user.phoneNumber)
    }

    fun get(intent: Intent) : User {
        return User(
            intent.getIntExtra("userId", 0),
            intent.getStringExtra("firstName") as String,
            intent.getStringExtra("lastName") as String,
            intent.getStringExtra("address") as String,
            intent.getStringExtra("phoneNumber") as String
        )
    }
}
