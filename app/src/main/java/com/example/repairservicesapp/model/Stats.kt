package com.example.repairservicesapp.model

class Stats {
    @JvmField
    var pending: Int = 0
    @JvmField
    var assigned: Int = 0
    @JvmField
    var awaitingBikes: Int = 0
    @JvmField
    var inProcess: Int = 0
    @JvmField
    var completed: Int = 0
    @JvmField
    var cancelled: Int = 0
    @JvmField
    var accepted: Int = 0
    @JvmField
    var totalRevenue: Double = 0.0
    @JvmField
    var totalHours: Int = 0
    @JvmField
    var totalBookings: Int = 0
    @JvmField
    var totalCustomers: Int = 0
    @JvmField
    var totalTechnicians: Int = 0
    @JvmField
    var availableTechnicians: Int = 0
    @JvmField
    var unavailableTechnicians: Int = 0
    @JvmField
    var customers: ArrayList<String> = ArrayList()
    @JvmField
    var technicians: ArrayList<String> = ArrayList()

    constructor()

    constructor(
        pending: Int,
        assigned: Int,
        awaitingBikes: Int,
        inProcess: Int,
        completed: Int,
        cancelled: Int,
        accepted: Int,
        totalRevenue: Double,
        totalHours: Int,
        totalBookings: Int,
        totalCustomers: Int,
        totalTechnicians: Int,
        availableTechnicians: Int,
        unavailableTechnicians: Int,
        customers: ArrayList<String>,
        technicians: ArrayList<String>
    ) {
        this.pending = pending
        this.assigned = assigned
        this.awaitingBikes = awaitingBikes
        this.inProcess = inProcess
        this.completed = completed
        this.cancelled = cancelled
        this.accepted = accepted
        this.totalRevenue = totalRevenue
        this.totalHours = totalHours
        this.totalBookings = totalBookings
        this.totalCustomers = totalCustomers
        this.totalTechnicians = totalTechnicians
        this.availableTechnicians = availableTechnicians
        this.unavailableTechnicians = unavailableTechnicians
        this.customers = customers
        this.technicians = technicians
    }

    fun setAvailableTechnicians(availableTechnicians: Int) {
        this.availableTechnicians = availableTechnicians
    }

    fun setUnavailableTechnicians(unavailableTechnicians: Int) {
        this.unavailableTechnicians = unavailableTechnicians
    }

}