package com.android.petshop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.core.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.activity_info.*
import java.util.logging.Logger

class InfoActivity : AppCompatActivity()
{

    val dbHandler = DBHelper(this@InfoActivity, null)
    val listOfFunctions = arrayOf("SUM", "AVG", "COUNT")
    val listOfProducers = arrayOf("SuperFood", "SmallWorld", "Pedigree")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        findViewById<ListView>(R.id.listView).adapter = FeedAdapter(this@InfoActivity, dbHandler.groupSumFeedsByProducer("producerId"))

        val a1 = ArrayAdapter(this@InfoActivity, android.R.layout.simple_spinner_item, listOfFunctions)
        a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        functionSpinner!!.adapter = a1
        functionSpinner?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                //Logger.getLogger(InfoActivity::class.java.name).warning("find selected")
                functionTextView.text = listOfFunctions[position]
            }
                override fun onNothingSelected(parent: AdapterView<*>) {
                    functionTextView.text = listOfFunctions[0]
        }
    })

    val a2 = ArrayAdapter(this@InfoActivity, android.R.layout.simple_spinner_item, listOfProducers)
        a2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        producerSpinner!!.adapter = a2
        producerSpinner?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                producerTextView.text = listOfProducers[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                producerTextView.text = listOfProducers[0]
            }
        })
    }

    fun backButton(view: View) {
        intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun okButtonCalculate(view: View) {
        resultTextView.text = dbHandler.getFunctionFeeds(functionTextView.text.toString())
    }

    fun okButtonFind(view: View) {
        findViewById<ListView>(R.id.listView).adapter = MinMaxResultAdapter(this@InfoActivity, dbHandler.minMaxCostFeeds(producerTextView.text.toString()))
    }

    fun sortByCostButton(view: View) {
        findViewById<ListView>(R.id.listView).adapter = FeedAdapter(this@InfoActivity, dbHandler.sortFeeds("cost"))
    }

    fun cortByFeedTitleButton(view: View) {
        findViewById<ListView>(R.id.listView).adapter = FeedAdapter(this@InfoActivity, dbHandler.sortFeeds("feed"))
    }
}
