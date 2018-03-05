package com.abnerescocio.assetssqlite.app.databases.controllers

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.abnerescocio.assetssqlite.app.databases.AppAssetsSQLite

/**
 * Created by abnerESC on 02/03/2018
 */
open class AppDBController(context: Context) {
    private val sqLiteDatabase: SQLiteDatabase

    init {
        val appAssetsSQLite = AppAssetsSQLite(context)
        sqLiteDatabase = appAssetsSQLite.writableDatabase!!
    }

    fun selectAll(tableName: String): Cursor {
        return sqLiteDatabase.query(tableName, null, null, null,
                null, null, null, null)
    }
}
