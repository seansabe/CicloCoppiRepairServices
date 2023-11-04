package com.example.repairservicesapp.model

class Booking {
    enum class BookingStatus {
        PENDING, ACCEPTED, DECLINED, COMPLETED
    }

    private var bookingId = 0
    @JvmField
    var bookingDate: String? = null
    @JvmField
    var bookingTime: String? = null
    @JvmField
    var bookingStatus: BookingStatus? = null
    @JvmField
    var bookingPrice = 0.0
    @JvmField
    var bookingDuration = 0
    @JvmField
    var services: ArrayList<Service>? = null
    @JvmField
    var user: User? = null
    @JvmField
    var technician: User? = null

    constructor()
    constructor(
        bookingDate: String?,
        bookingTime: String?,
        bookingStatus: BookingStatus?,
        bookingPrice: Double,
        bookingDuration: Int,
        services: ArrayList<Service>?,
        user: User
    ) {
        this.bookingDate = bookingDate
        this.bookingTime = bookingTime
        this.bookingStatus = bookingStatus
        this.bookingPrice = bookingPrice
        this.bookingDuration = bookingDuration
        this.services = services
        this.user = user
    }

    constructor(
        bookingId: Int,
        bookingDate: String?,
        bookingTime: String?,
        bookingStatus: BookingStatus?,
        bookingPrice: Double,
        bookingDuration: Int,
        services: ArrayList<Service>?,
        user: User,
        technician: User
    ) {
        this.bookingId = bookingId
        this.bookingDate = bookingDate
        this.bookingTime = bookingTime
        this.bookingStatus = bookingStatus
        this.bookingPrice = bookingPrice
        this.bookingDuration = bookingDuration
        this.services = services
        this.user = user
        this.technician = technician
    }

    val isPending: Boolean
        get() = bookingStatus == BookingStatus.PENDING

    val isAccepted: Boolean
        get() = bookingStatus == BookingStatus.ACCEPTED

    val isDeclined: Boolean
        get() = bookingStatus == BookingStatus.DECLINED

    val isCompleted: Boolean
        get() = bookingStatus == BookingStatus.COMPLETED

    fun getBookingId(): Int {
        return bookingId
    }
}