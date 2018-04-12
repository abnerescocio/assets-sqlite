package com.abnerescocio.assetssqlite.app.databases

import android.content.Context
import com.abnerescocio.assetssqlite.lib.AssetsSQLite

class AppAssetsSQLite(context: Context): AssetsSQLite(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "big_database.db"
        const val DATABASE_VERSION = 1
    }
}