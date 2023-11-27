package com.example.repairservicesapp.model

import com.google.firebase.Timestamp

class Service {
    private var serviceId = ""
    @JvmField
    var serviceName: String? = null
    @JvmField
    var serviceDescription: String? = null
    @JvmField
    var serviceCost = 0.0
    @JvmField
    var serviceDuration = 0

    private lateinit var timestamp: Timestamp

    constructor()
    constructor(
        serviceName: String?,
        serviceDescription: String?,
        servicePrice: Double,
        serviceDuration: Int
    ) {
        this.serviceName = serviceName
        this.serviceDescription = serviceDescription
        this.serviceCost = servicePrice
        this.serviceDuration = serviceDuration
        this.timestamp = Timestamp.now()
    }

    constructor(
        serviceId: String,
        serviceName: String?,
        serviceDescription: String?,
        servicePrice: Double,
        serviceDuration: Int
    ) {
        this.serviceId = serviceId
        this.serviceName = serviceName
        this.serviceDescription = serviceDescription
        this.serviceCost = servicePrice
        this.serviceDuration = serviceDuration
        this.timestamp = Timestamp.now()
    }

    fun getServiceId(): String {
        return serviceId
    }

    fun setServiceId(serviceId: String) {
        this.serviceId = serviceId
    }

    fun getTimestamp(): Timestamp {
        return timestamp
    }

    fun setTimestamp(timestamp: Timestamp) {
        this.timestamp = timestamp
    }

}