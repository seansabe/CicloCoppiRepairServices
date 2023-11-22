package com.example.repairservicesapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class User() : Parcelable {
    enum class UserType {
        CUSTOMER, TECHNICIAN, ADMIN
    }

    private var userId = 0
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

    constructor(
        firstName: String?,
        lastName: String?,
        address: String?,
        phoneNumber: String?,
        email: String?,
        password: String?,
        userType: UserType?,
        userAvailability: Int
    ) : this() {
        this.firstName = firstName
        this.lastName = lastName
        this.address = address
        this.phoneNumber = phoneNumber
        this.email = email
        this.password = password
        this.userType = userType
        this.userAvailability = userAvailability
    }

    constructor(
        firstName: String?,
        lastName: String?,
        address: String?,
        phoneNumber: String?,
        email: String?,
        password: String?,
        userType: UserType?,
    ) : this() {
        this.firstName = firstName
        this.lastName = lastName
        this.address = address
        this.phoneNumber = phoneNumber
        this.email = email
        this.password = password
        this.userType = userType
    }

    constructor(
        userId: Int,
        firstName: String?,
        lastName: String?,
        address: String?,
        phoneNumber: String?,
        email: String?,
        password: String?,
        userType: String?,
        token: String?,
        userAvailability: Int
    ) : this() {
        this.userId = userId
        this.firstName = firstName
        this.lastName = lastName
        this.address = address
        this.phoneNumber = phoneNumber
        this.email = email
        this.password = password
        this.userType = UserType.valueOf(userType!!)
        this.token = token
        this.userAvailability = userAvailability
    }

    constructor(
        userId: Int,
        firstName: String?,
        lastName: String?,
        address: String?,
        phoneNumber: String?
    ) : this() {
        this.userId = userId
        this.firstName = firstName
        this.lastName = lastName
        this.address = address
        this.phoneNumber = phoneNumber
    }

    constructor(
        userId: Int,
        firstName: String?,
        lastName: String?,
        address: String?,
        phoneNumber: String?,
        email: String?,
    ) : this() {
        this.userId = userId
        this.firstName = firstName
        this.lastName = lastName
        this.address = address
        this.phoneNumber = phoneNumber
        this.email = email
    }

    constructor(
        firstName: String?,
        lastName: String?,
        address: String?,
        phoneNumber: String?,
        email: String?
    ) : this() {
        this.firstName = firstName
        this.lastName = lastName
        this.address = address
        this.phoneNumber = phoneNumber
        this.email = email
    }

    val userFirstAndLastName: String
        get() = "$firstName $lastName"

    val isCustomer: Boolean
        get() = userType == UserType.CUSTOMER

    val isTechnician: Boolean
        get() = userType == UserType.TECHNICIAN

    val isAdmin: Boolean
        get() = userType == UserType.ADMIN

    fun getUserId(): Int {
        return userId
    }

    fun setToken(token: String?) {
        this.token = token
    }

    fun getToken(): String? {
        return token
    }
}