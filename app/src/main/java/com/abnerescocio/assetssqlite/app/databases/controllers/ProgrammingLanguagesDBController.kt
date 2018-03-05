package com.abnerescocio.assetssqlite.app.databases.controllers

import android.content.Context
import android.database.Cursor
import com.abnerescocio.assetssqlite.app.databases.contracts.ProgrammingLanguagesEntries

/**
 * Created by abnerESC on 02/03/2018
 */
class ProgrammingLanguagesDBController(context: Context) : AppDBController(context) {
    fun getLanguages(): Cursor {
        return selectAll(ProgrammingLanguagesEntries.TABLE_NAME)
    }
}