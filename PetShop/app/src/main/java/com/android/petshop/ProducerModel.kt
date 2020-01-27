package com.android.petshop

import java.io.Serializable

class ProducerModel : Serializable {

    var producer: String? = null
    var address: String? = null
    var id: Int = 0

    fun getProducers(): String {
        return producer.toString()
    }

    fun setProducers(producer: String) {
        this.producer = producer
    }

    fun getIds(): Int {
        return id
    }

    fun setIds(id: Int) {
        this.id = id
    }

    fun getAddresses(): String {
        return address.toString()
    }

    fun setAddresses(address: String) {
        this.address = address
    }

}