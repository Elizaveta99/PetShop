package com.android.petshop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView

class MainActivity : AppCompatActivity() {

    val dbHandler = DBHelper(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun feedsButton(view: View) {
        findViewById<ListView>(R.id.listView).adapter = FeedAdapter(this@MainActivity, dbHandler.getAllFeeds())
    }

    fun producersButton(view: View) {
        findViewById<ListView>(R.id.listView).adapter = ProducerAdapter(this@MainActivity, dbHandler.getAllProducers())
    }

    fun infoButton(view: View) {
        intent = Intent(this, InfoActivity::class.java)
        startActivity(intent)
    }
}
