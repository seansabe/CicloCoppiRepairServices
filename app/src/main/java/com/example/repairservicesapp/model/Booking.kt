package com.example.repairservicesapp.model

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import com.example.repairservicesapp.R
import kotlinx.parcelize.Parcelize

@Parcelize
class Booking() : Parcelable {
    enum class BookingStatus(@StringRes val statusValueRes: Int?) {
        PENDING(R.string.pending),
        ASSIGNED(R.string.assigned),
        AWAITING_BIKE(R.string.awaitingBike),
        IN_PROCESS(R.string.inProcess),
        ACCEPTED(R.string.accepted),
        CANCELLED(R.string.cancelled),
        COMPLETED(R.string.completed);

        // Function to get the localized status value
        fun getStatusValue(context: Context): String? {
            return statusValueRes?.let { context.getString(it) }
        }
    }
    @JvmField
    var bookingId: String? = null
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
    ) : this() {
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
    ) : this() {
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

    constructor(
        bookingId: String?,
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
    ) : this() {
        this.bookingId = bookingId
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

    constructor(
        bookingId: String?,
        dropInTime: String?,
        bookingStatus: BookingStatus?,
        bookingCost: Double,
        bookingDuration: Int,
        bikeType: String,
        bikeColor: String,
        bikeWheelSize: String,
        comments: String,
        services: ArrayList<Service>?,
        customer: User,
    ) : this() {
        this.bookingId = bookingId
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

    val isPending: Boolean
        get() = bookingStatus == BookingStatus.PENDING

    val isAccepted: Boolean
        get() = bookingStatus == BookingStatus.ACCEPTED

    val isCancelled: Boolean
        get() = bookingStatus == BookingStatus.CANCELLED

    val isCompleted: Boolean
        get() = bookingStatus == BookingStatus.COMPLETED

    val isInProcess: Boolean
        get() = bookingStatus == BookingStatus.IN_PROCESS

    val isAssigned: Boolean
        get() = bookingStatus == BookingStatus.ASSIGNED

    val isAwaitingBike: Boolean
        get() = bookingStatus == BookingStatus.AWAITING_BIKE

    fun setBookingId(bookingId: String?) {
        this.bookingId = bookingId
    }

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