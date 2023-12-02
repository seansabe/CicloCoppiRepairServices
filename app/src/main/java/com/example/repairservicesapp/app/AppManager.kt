package com.example.repairservicesapp.app

import com.example.repairservicesapp.model.Stats
import com.example.repairservicesapp.model.User

class AppManager private constructor() {
    @JvmField
    var user = User()
    @JvmField
    var stats = Stats()
    fun setUser(user: User) {
        this.user = user
    }

    fun setStats(stats: Stats) {
        this.stats = stats
    }

    companion object {
        @JvmField
        var instance = AppManager()
    }
}