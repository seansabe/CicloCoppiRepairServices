package com.example.repairservicesapp.model

class User {
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

    constructor()
    constructor(
        firstName: String?,
        lastName: String?,
        address: String?,
        phoneNumber: String?,
        email: String?,
        password: String?,
        userType: UserType?
    ) {
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
        userType: String?
    ) {
        this.userId = userId
        this.firstName = firstName
        this.lastName = lastName
        this.address = address
        this.phoneNumber = phoneNumber
        this.email = email
        this.password = password
        this.userType = UserType.valueOf(userType!!)
    }

    constructor(
        userId: Int,
        firstName: String?,
        lastName: String?,
        address: String?,
        phoneNumber: String?
    ) {
        this.userId = userId
        this.firstName = firstName
        this.lastName = lastName
        this.address = address
        this.phoneNumber = phoneNumber
    }

    constructor(
        firstName: String?,
        lastName: String?,
        address: String?,
        phoneNumber: String?,
        email: String?
    ) {
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
}