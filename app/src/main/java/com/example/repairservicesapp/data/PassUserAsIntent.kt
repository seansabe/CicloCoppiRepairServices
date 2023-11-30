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
        intent.putExtra("email", user.email)
        intent.putExtra("password", user.password)
        intent.putExtra("userType", user.userType)
        intent.putExtra("userAvailability", user.userAvailability)
        intent.putExtra("token", user.token)
    }

    fun get(intent: Intent) : User {
        return User(
            intent.getStringExtra("userId") as String,
            intent.getStringExtra("firstName") as String,
            intent.getStringExtra("lastName") as String,
            intent.getStringExtra("address") as String,
            intent.getStringExtra("phoneNumber") as String,
            intent.getStringExtra("email") as String,
            intent.getStringExtra("password") as String,
            intent.getStringExtra("userType") as User.UserType?,
            intent.getStringExtra("token"),
            intent.getIntExtra("userAvailability", 100)
        )
    }
}
