package com.example.repairservicesapp.util

import com.example.repairservicesapp.model.Service
import com.example.repairservicesapp.model.User
import com.google.firebase.firestore.QueryDocumentSnapshot

object MapUtils {
    fun userToMap(user: User): HashMap<String, Any?> {
        return hashMapOf(
            "userId" to user.getUserId(),
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "address" to user.address,
            "phoneNumber" to user.phoneNumber,
            "email" to user.email,
            "password" to user.password,
            "userType" to user.userType,
            "userAvailability" to user.userAvailability,
            "token" to user.token,
            "timestamp" to user.getTimestamp()
        )
    }

    fun snapshotToUserObject(document: QueryDocumentSnapshot): User {
        val data = document.data
        return User(
            data["userId"] as String,
            data["firstName"] as String,
            data["lastName"] as String,
            data["address"] as String,
            data["phoneNumber"] as String,
            data["email"] as String,
            data["password"] as String,
            User.UserType.valueOf(data["userType"] as String),
            data["token"] as String?,
            (data["userAvailability"] as Long).toInt()
        )
    }

    fun mapToUserObject(data: Map<String, Any>): User {
        return User(
            data["userId"] as String,
            data["firstName"] as String,
            data["lastName"] as String,
            data["address"] as String,
            data["phoneNumber"] as String,
            data["email"] as String,
            data["password"] as String,
            User.UserType.valueOf(data["userType"] as String),
            data["token"] as String?,
            (data["userAvailability"] as Long).toInt()
        )
    }

    fun serviceToMap(service: Service): HashMap<String, Any?> {
        return hashMapOf(
            "serviceId" to service.getServiceId(),
            "serviceName" to service.serviceName,
            "serviceDescription" to service.serviceDescription,
            "serviceCost" to service.serviceCost,
            "serviceDuration" to service.serviceDuration,
            "timestamp" to service.getTimestamp()
        )
    }

    fun mapToServiceObject(data: Map<String, Any>): Service {
        return Service(
            data["serviceId"] as String,
            data["serviceName"].toString(),
            data["serviceDescription"].toString(),
            (data["servicePrice"] as? Double) ?: 0.0,
            (data["serviceDuration"] as? Long)?.toInt() ?: 0
        )
    }

    fun snapshotServiceObject(document: QueryDocumentSnapshot): Service {
        val data = document.data
        return Service(
            data["serviceId"] as String,
            data["serviceName"] as String,
            data["serviceDescription"] as String,
            data["serviceCost"] as Double,
            (data["serviceDuration"] as Long).toInt()
        )
    }
}