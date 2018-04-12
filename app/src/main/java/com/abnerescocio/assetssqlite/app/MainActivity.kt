package com.abnerescocio.assetssqlite.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.abnerescocio.assetssqlite.app.adapters.ProgrammingLanguagesAdapter
import com.abnerescocio.assetssqlite.app.databases.controllers.ProgrammingLanguagesDBController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var controller: ProgrammingLanguagesDBController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        controller = ProgrammingLanguagesDBController(this)
        var cursor = controller.getLanguages()
        val adapter = ProgrammingLanguagesAdapter(this, cursor)
        recycler_view.adapter = adapter
        swipe_refresh.setOnRefreshListener {
            controller.getLanguages().let {
                cursor = it
                adapter.notifyDataSetChanged()
                swipe_refresh.isRefreshing = false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.close()
    }
}
