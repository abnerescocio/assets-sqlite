package com.abnerescocio.assetssqlite.app.databases.contracts

import android.provider.BaseColumns

/**
 * Created by abnerESC on 02/03/2018
 */
class ProgrammingLanguagesEntries: BaseColumns {
    companion object {
        const val TABLE_NAME = "programming_languages"
        const val NAME = "name"
        const val CREATED_IN = "created_in"
    }
}