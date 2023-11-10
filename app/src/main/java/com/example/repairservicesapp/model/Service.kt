package com.example.repairservicesapp.model

class Service {
    private var serviceId = 0
    @JvmField
    var serviceName: String? = null
    @JvmField
    var serviceDescription: String? = null
    @JvmField
    var serviceCost = 0.0
    @JvmField
    var serviceDuration = 0

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
    }

    constructor(
        serviceId: Int,
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
    }

    fun getServiceId(): Int {
        return serviceId
    }

}