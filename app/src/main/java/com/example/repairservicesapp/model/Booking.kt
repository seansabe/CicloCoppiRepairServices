package com.example.repairservicesapp.model

class Booking {
    enum class BookingStatus {
        PENDING, ASSIGNED, IN_PROCESS, ACCEPTED, DECLINED, COMPLETED
    }

    @JvmField
    var bookingDate: String? = null
    @JvmField
    var bookingTime: String? = null
    @JvmField
    var dropInTime: String? = null
    @JvmField
    var bookingStatus: BookingStatus? = null
    @JvmField
    var bookingCost = 0.0
    @JvmField
    var bookingDuration = 0
    @JvmField
    var bikeType = ""
    @JvmField
    var bikeColor = ""
    @JvmField
    var bikeWheelSize = ""
    @JvmField
    var comments = ""
    @JvmField
    var services: ArrayList<Service>? = null
    @JvmField
    var customer: User? = null
    @JvmField
    var technician: User? = null
    @JvmField
    var rating = 0.0

    constructor()
    constructor(
        dropInTime: String?,
        bookingStatus: BookingStatus?,
        bookingCost: Double,
        bookingDuration: Int,
        bikeType: String,
        bikeColor: String,
        bikeWheelSize: String,
        comments: String,
        services: ArrayList<Service>?,
        customer: User
    ) {
        this.dropInTime = dropInTime
        this.bookingStatus = bookingStatus
        this.bookingCost = bookingCost
        this.bookingDuration = bookingDuration
        this.bikeType = bikeType
        this.bikeColor = bikeColor
        this.bikeWheelSize = bikeWheelSize
        this.comments = comments
        this.services = services
        this.customer = customer
    }

    constructor(
        dropInTime: String?,
        bookingDate: String?,
        bookingTime: String?,
        bookingStatus: BookingStatus?,
        bookingCost: Double,
        bookingDuration: Int,
        bikeType: String,
        bikeColor: String,
        bikeWheelSize: String,
        comments: String,
        services: ArrayList<Service>?,
        customer: User,
        technician: User
    ) {
        this.dropInTime = dropInTime
        this.bookingDate = bookingDate
        this.bookingTime = bookingTime
        this.bookingStatus = bookingStatus
        this.bookingCost = bookingCost
        this.bookingDuration = bookingDuration
        this.bikeType = bikeType
        this.bikeColor = bikeColor
        this.bikeWheelSize = bikeWheelSize
        this.comments = comments
        this.services = services
        this.customer = customer
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

    val isInProcess: Boolean
        get() = bookingStatus == BookingStatus.IN_PROCESS

    val isAssigned: Boolean
        get() = bookingStatus == BookingStatus.ASSIGNED

    fun setBookingStatus(bookingStatus: BookingStatus?) {
        this.bookingStatus = bookingStatus
    }

    fun setBookingTechnician(technician: User?) {
        this.technician = technician
    }

    fun setBookingDate(bookingDate: String?) {
        this.bookingDate = bookingDate
    }

    fun setBookingTime(bookingTime: String?) {
        this.bookingTime = bookingTime
    }

    fun setBookingRating(rating: Double) {
        this.rating = rating
    }
}