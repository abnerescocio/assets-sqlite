package com.abnerescocio.assetssqlite.lib

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.*
import java.util.zip.ZipFile

/**
 * Created by abnerESC on 02/03/2018
 */
open class AssetsSQLite(private val context: Context, name: String,
                   factory: SQLiteDatabase.CursorFactory?, private val newVersion: Int)
    : SQLiteOpenHelper(context, name, factory, newVersion) {

    private val standardDatabaseDir: String
    private val standardDatabasePath: String

    init {
        standardDatabaseDir = context.applicationInfo.dataDir + File.separator +
                DATABASES + File.separator
        standardDatabasePath = standardDatabaseDir + databaseName
        context as Activity
    }

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    override fun getWritableDatabase(): SQLiteDatabase? {
        var sqLiteDatabase = openStandardDatabase()
        val oldVersion = sqLiteDatabase?.version
        if (oldVersion!! < newVersion) sqLiteDatabase = null
        if (sqLiteDatabase == null) sqLiteDatabase = openAssetsDatabase()
        if (sqLiteDatabase == null) sqLiteDatabase = openAssetsDatabaseCompacted()
        if (sqLiteDatabase == null) throw SQLiteException("Esteja certo de ter adicionado na pasta " +
                "ASSETS arquivos com uma das extensões: $databaseName " +
                "ou ${databaseName.replace(".db", ".zip")}")
        sqLiteDatabase.beginTransaction()
        sqLiteDatabase.version = newVersion
        sqLiteDatabase.setTransactionSuccessful()
        sqLiteDatabase.endTransaction()
        return sqLiteDatabase
    }

    private fun openStandardDatabase(): SQLiteDatabase? {
        return try {
            SQLiteDatabase.openDatabase(File(standardDatabasePath).absolutePath, null, SQLiteDatabase.OPEN_READWRITE)
        } catch (e: SQLiteException) {
            null
        }
    }

    private fun openAssetsDatabase(): SQLiteDatabase? {
        return File(standardDatabasePath).let {
            if (!it.exists()) {
                copyFileFromAssetsToStandardPath(it)
                openStandardDatabase()
            } else null
        }
    }

    private fun openAssetsDatabaseCompacted(): SQLiteDatabase? {
        return File(standardDatabasePath.replace(".db", ".zip")).let {
            if (!it.exists()) copyFileFromAssetsToStandardPath(it)
            Log.i(TAG, context.getString(R.string.unziping_files))
            if (it.exists()) {
                ZipFile(it).use {
                    BufferedInputStream(it.getInputStream(it.getEntry(databaseName))).use { bis: BufferedInputStream ->
                        File(standardDatabasePath).outputStream().buffered().use {
                            bis.copyTo(it)
                        }
                    }
                    Log.i(TAG, context.getString(R.string.unziping_successfully))
                }
                openStandardDatabase()
            } else null
        }
    }

    private fun getInputStreamFromAssets(databaseRealFileName: String): InputStream? {
        return try {
            context.assets.open(databaseRealFileName)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    private fun copyFileFromAssetsToStandardPath(file: File) {
        File(standardDatabaseDir).let { if (!it.exists()) it.mkdir() }
        getInputStreamFromAssets(file.name).let { inputStream: InputStream? ->
            if (inputStream != null) FileOutputStream(file.absolutePath).let {
                Log.i(TAG, context.getString(R.string.moving_files))
                inputStream.copyTo(it)
            }
        }
    }

    companion object {
        const val TAG = "AssetsSQLite"
        const val DATABASES = "databases"
    }
}