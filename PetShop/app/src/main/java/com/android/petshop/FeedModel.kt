package com.android.petshop

import java.io.Serializable

/**
 * Created by Parsania Hardik on 26-Apr-17.
 */
class FeedModel : Serializable {

    var feed: String? = null
    var producer: String? = null
    var cost: Int = 0 // ??
    var id: Int = 0
    var producerId: Int = 0

    fun getFeeds(): String {
        return feed.toString()
    }

    fun setFeeds(feed: String) {
        this.feed = feed
    }

    fun getIds(): Int {
        return id
    }

    fun setIds(id: Int) {
        this.id = id
    }

    fun getProducers(): String {
        return producer.toString()
    }

    fun setProducers(producer: String) {
        this.producer = producer
    }

    fun getCosts(): Int {
        return cost
    }

    fun setCosts(cost: Int) {
        this.cost = cost
    }

    fun getProducerIds(): Int {
        return producerId
    }

    fun setProducerIds(producerId: Int) {
        this.producerId = producerId
    }

}