package com.android.petshop

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.logging.Logger

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_NAME_FEEDS " +
                    "($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_FEED TEXT, $COLUMN_PRODUCER_ID INTEGER, $COLUMN_COST INTEGER)"
        )
        db.execSQL(
            "CREATE TABLE $TABLE_NAME_PRODUCERS " +
                    "($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_PRODUCER TEXT, $COLUMN_ADDRESS TEXT)"
        )

        val cv = ContentValues()

        val feeds =
            arrayOf("CatFeed", "DogFeed", "RabbitFeed", "FishFeed", "HamsterFeed", "ParrotFeed")
        val producersId =
            arrayOf("1", "2", "2", "3", "2", "1")
        val costs =
            arrayOf(50, 25, 20, 35, 30, 10)
        for (i in feeds.indices)
        {
            cv.clear()
            cv.put(COLUMN_FEED, feeds[i])
            cv.put(COLUMN_PRODUCER_ID, producersId[i])
            cv.put(COLUMN_COST, costs[i])
            db.insert(TABLE_NAME_FEEDS, null, cv)
        }


        val producers =
            arrayOf("SuperFood", "SmallWorld", "Pedigree")
        val addresses =
            arrayOf("Poland", "Belarus", "Russia")
        for (i in producers.indices)
        {
            cv.clear()
            cv.put(COLUMN_PRODUCER, producers[i])
            cv.put(COLUMN_ADDRESS, addresses[i])
            db.insert(TABLE_NAME_PRODUCERS, null, cv)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_FEEDS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_PRODUCERS")
        onCreate(db)
    }

    fun sortFeeds(orderBy: String): ArrayList<FeedModel>
    {
        val feedModelArrayList = ArrayList<FeedModel>()
        val db = this.readableDatabase
        val c = db.query(TABLE_NAME_FEEDS, null, null, null, null, null, orderBy)
        if (c.moveToFirst())
        {
            do {
                val feedModel = FeedModel()
                feedModel.setProducerIds(c.getInt(c.getColumnIndex(COLUMN_PRODUCER_ID)))
                feedModel.setFeeds(c.getString(c.getColumnIndex(COLUMN_FEED)))
                feedModel.setCosts(c.getInt(c.getColumnIndex(COLUMN_COST)))
                val selectProducerQuery =
                    "SELECT  * FROM " + TABLE_NAME_PRODUCERS + " WHERE " + COLUMN_ID + " = " + feedModel.getProducerIds()
                Log.d("Producer query", selectProducerQuery)
                val cProducer = db.rawQuery(selectProducerQuery, null)

                if (cProducer.moveToFirst())
                {
                    do {
                        feedModel.setProducers(cProducer.getString(cProducer.getColumnIndex(COLUMN_PRODUCER)))
                    } while (cProducer.moveToNext())
                }

                feedModelArrayList.add(feedModel)
            } while (c.moveToNext())
        }
        db.close()
        return feedModelArrayList
    }

    fun groupSumFeedsByProducer(groupBy: String): ArrayList<FeedModel>
    {
        val feedModelArrayList = ArrayList<FeedModel>()
        val db = this.readableDatabase
        val orderBy = groupBy
        var columns = arrayOf(COLUMN_FEED,  COLUMN_PRODUCER_ID, "sum($COLUMN_COST) as SUM_COST")
        val c = db.query(TABLE_NAME_FEEDS, columns, null, null, groupBy, null, orderBy)
        if (c.moveToFirst())
        {
            do {
                val feedModel = FeedModel()
                feedModel.setProducerIds(c.getInt(c.getColumnIndex(COLUMN_PRODUCER_ID)))
                feedModel.setFeeds(c.getString(c.getColumnIndex(COLUMN_FEED)))
                feedModel.setCosts(c.getInt(c.getColumnIndex("SUM_COST")))
                val selectProducerQuery =
                    "SELECT  * FROM " + TABLE_NAME_PRODUCERS + " WHERE " + COLUMN_ID + " = " + feedModel.getProducerIds()
                Log.d("Producer query", selectProducerQuery)
                val cProducer = db.rawQuery(selectProducerQuery, null)

                if (cProducer.moveToFirst())
                {
                    do {
                        feedModel.setProducers(cProducer.getString(cProducer.getColumnIndex(COLUMN_PRODUCER)))
                    } while (cProducer.moveToNext())
                }

                feedModelArrayList.add(feedModel)
            } while (c.moveToNext())
        }
        db.close()
        return feedModelArrayList
    }

    fun minMaxCostFeeds(producer: String): ArrayList<FeedModel>
    {
        Logger.getLogger(DBHelper::class.java.name).warning(String().format("Producer = " + producer))
        val feedModelArrayList = ArrayList<FeedModel>()
        val db = this.readableDatabase

        var producerId: Int = 0
        val selectProducerIdQuery =
            "SELECT  $COLUMN_ID FROM $TABLE_NAME_PRODUCERS WHERE $COLUMN_PRODUCER = '$producer'"
        val cProducerId = db.rawQuery(selectProducerIdQuery, null)

        if (cProducerId.moveToFirst())
        {
            producerId = cProducerId.getInt(cProducerId.getColumnIndex(COLUMN_ID)) // ??
        }

        //Logger.getLogger(DBHelper::class.java.name).warning(String().format("Producer id = %d", producerId))

        val selection = "$COLUMN_PRODUCER_ID = $producerId"
        var columns = arrayOf(COLUMN_FEED,  COLUMN_PRODUCER_ID, "min($COLUMN_COST) as MIN_COST")
        var c = db.query(TABLE_NAME_FEEDS, columns, selection, null, null, null, null)
        if (c.moveToFirst())
        {
                var feedModel = FeedModel()
                feedModel.setProducerIds(c.getInt(c.getColumnIndex(COLUMN_PRODUCER_ID)))
                feedModel.setFeeds(c.getString(c.getColumnIndex(COLUMN_FEED)))
                feedModel.setCosts(c.getInt(c.getColumnIndex("MIN_COST")))
                feedModel.setProducers(producer)
                feedModelArrayList.add(feedModel)
        }

        columns = arrayOf(COLUMN_FEED,  COLUMN_PRODUCER_ID, "max($COLUMN_COST) as MAX_COST")
        c = db.query(TABLE_NAME_FEEDS, columns, selection, null, null, null, null)
        if (c.moveToFirst())
        {
            var feedModel = FeedModel()
            feedModel.setProducerIds(c.getInt(c.getColumnIndex(COLUMN_PRODUCER_ID)))
            feedModel.setFeeds(c.getString(c.getColumnIndex(COLUMN_FEED)))
            feedModel.setCosts(c.getInt(c.getColumnIndex("MAX_COST")))
            feedModel.setProducers(producer)
            feedModelArrayList.add(feedModel)
        }

        db.close()
        return feedModelArrayList
    }

    fun getFunctionFeeds(function: String): String
    {
        val db = this.readableDatabase
        var columns = arrayOf("$function($COLUMN_COST) as RES")
        if (function == "COUNT")
            columns = arrayOf("$function($COLUMN_ID) as RES")
        val c = db.query(TABLE_NAME_FEEDS, columns, null, null, null, null, null)
        var ans: String = ""
        if (c.moveToFirst())
        {
            ans = c.getInt(c.getColumnIndex("RES")).toString()
        }
        db.close()
        return ans
    }

    fun getAllFeeds(): ArrayList<FeedModel>
    {
        val feedModelArrayList = ArrayList<FeedModel>()

        val selectQuery = "SELECT  * FROM $TABLE_NAME_FEEDS"
        val db = this.readableDatabase
        val c = db.rawQuery(selectQuery, null)

        if (c.moveToFirst())
        {
            do {
                val feedModel = FeedModel()
                feedModel.setProducerIds(c.getInt(c.getColumnIndex(COLUMN_PRODUCER_ID)))
                feedModel.setFeeds(c.getString(c.getColumnIndex(COLUMN_FEED)))
                feedModel.setCosts(c.getInt(c.getColumnIndex(COLUMN_COST)))
                val selectProducerQuery =
                    "SELECT  * FROM " + TABLE_NAME_PRODUCERS + " WHERE " + COLUMN_ID + " = " + feedModel.getProducerIds()
                Log.d("Producer query", selectProducerQuery)
                val cProducer = db.rawQuery(selectProducerQuery, null)

                if (cProducer.moveToFirst())
                {
                    do {
                        feedModel.setProducers(cProducer.getString(cProducer.getColumnIndex(COLUMN_PRODUCER)))
                    } while (cProducer.moveToNext())
                }

                feedModelArrayList.add(feedModel)
            } while (c.moveToNext())
        }
        db.close()
        return feedModelArrayList
    }

    fun getAllProducers(): ArrayList<ProducerModel>
    {
        val producerModelArrayList = ArrayList<ProducerModel>()

        val selectQuery = "SELECT  * FROM $TABLE_NAME_PRODUCERS"
        val db = this.readableDatabase
        val c = db.rawQuery(selectQuery, null)

        if (c.moveToFirst())
        {
            do {
                val producerModel = ProducerModel()
                producerModel.setProducers(c.getString(c.getColumnIndex(COLUMN_PRODUCER)))
                producerModel.setAddresses(c.getString(c.getColumnIndex(COLUMN_ADDRESS)))
                producerModelArrayList.add(producerModel)
            } while (c.moveToNext())
        }
        db.close()
        return producerModelArrayList
    }

    companion object {
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "PetShopDatabase.db"
        const val TABLE_NAME_FEEDS = "Feeds"
        const val TABLE_NAME_PRODUCERS = "Producers"

        const val COLUMN_ID = "id"
        const val COLUMN_FEED = "feed"
        const val COLUMN_PRODUCER_ID = "producerId"
        const val COLUMN_COST = "cost"
        const val COLUMN_PRODUCER = "producer"
        const val COLUMN_ADDRESS = "address"
    }


}