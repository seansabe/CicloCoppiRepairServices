package com.example.repairservicesapp.model

import com.google.firebase.Timestamp

class User {
    enum class UserType {
        CUSTOMER, TECHNICIAN, ADMIN
    }

    private var userId: String = ""
    @JvmField
    var firstName: String? = null
    @JvmField
    var lastName: String? = null
    @JvmField
    var address: String? = null
    @JvmField
    var phoneNumber: String? = null
    @JvmField
    var email: String? = null
    @JvmField
    var password: String? = null
    @JvmField
    var userType: UserType? = null
    @JvmField
    var userAvailability = 1
    @JvmField
    var token: String? = null

    private lateinit var timestamp: Timestamp

    constructor()

    constructor(
        userId: String,
        firstName: String?,
        lastName: String?,
        address: String?,
        phoneNumber: String?,
        email: String?,
        password: String?,
        userType: UserType?,
        token: String?,
        userAvailability: Int,
    ) {
        this.userId = userId
        this.firstName = firstName
        this.lastName = lastName
        this.address = address
        this.phoneNumber = phoneNumber
        this.email = email
        this.password = password
        this.userType = userType
        this.token = token
        this.userAvailability = userAvailability
        this.timestamp = Timestamp.now()
    }

    constructor(
        firstName: String?,
        lastName: String?,
        address: String?,
        phoneNumber: String?,
        email: String?,
        password: String?,
        userType: UserType?,
        token: String?,
        userAvailability: Int
    ) {
        this.firstName = firstName
        this.lastName = lastName
        this.address = address
        this.phoneNumber = phoneNumber
        this.email = email
        this.password = password
        this.userType = userType
        this.token = token
        this.userAvailability = userAvailability
        this.timestamp = Timestamp.now()
    }

    val userFirstAndLastName: String
        get() = "$firstName $lastName"

    val isCustomer: Boolean
        get() = userType == UserType.CUSTOMER

    val isTechnician: Boolean
        get() = userType == UserType.TECHNICIAN

    val isAdmin: Boolean
        get() = userType == UserType.ADMIN

    fun getUserId(): String {
        return userId
    }

    fun setUserId(userId: String) {
        this.userId = userId
    }

    fun setToken(token: String?) {
        this.token = token
    }

    fun getToken(): String? {
        return token
    }

    fun getTimestamp(): Timestamp {
        return timestamp
    }

    fun setTimestamp(timestamp: Timestamp) {
        this.timestamp = timestamp
    }
}