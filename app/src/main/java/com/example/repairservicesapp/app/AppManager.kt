package com.example.repairservicesapp.app

import com.example.repairservicesapp.model.User

class AppManager private constructor() {
    @JvmField
    var user = User()
    fun setUser(user: User) {
        this.user = user
    }

    companion object {
        @JvmField
        var instance = AppManager()
    }
}