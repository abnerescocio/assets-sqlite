package com.abnerescocio.assetssqlite.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.abnerescocio.assetssqlite.app.adapters.ProgrammingLanguagesAdapter
import com.abnerescocio.assetssqlite.app.databases.controllers.ProgrammingLanguagesDBController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val controller = ProgrammingLanguagesDBController(this)
        val cursor = controller.getLanguages()
        val adapter = ProgrammingLanguagesAdapter(this, cursor)
        recycler_view.adapter = adapter
    }
}
