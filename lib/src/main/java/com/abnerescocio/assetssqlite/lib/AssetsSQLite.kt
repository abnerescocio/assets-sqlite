package com.abnerescocio.assetssqlite.lib

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.widget.ProgressBar
import android.widget.TextView
import java.io.*
import java.util.zip.ZipFile

/**
 * Created by abnerESC on 02/03/2018
 */
abstract class AssetsSQLite(private val context: Context, name: String,
                   factory: SQLiteDatabase.CursorFactory?, version: Int)
    : SQLiteOpenHelper(context, name, factory, version) {

    private val standardDatabaseDir: String
    private val standardDatabasePath: String
    private var textView: TextView? = null
    private var progressBar: ProgressBar? = null

    init {
        standardDatabaseDir = context.applicationInfo.dataDir + File.separator +
                DATABASES + File.separator
        standardDatabasePath = standardDatabaseDir + databaseName
        context as Activity
        textView = context.findViewById(android.R.id.text1)
        progressBar = context.findViewById(android.R.id.progress) as ProgressBar
    }

    abstract override fun onCreate(db: SQLiteDatabase?)

    abstract override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)

    override fun getWritableDatabase(): SQLiteDatabase? {
        var sqLiteDatabase: SQLiteDatabase? = null
        //if (File(standardDatabasePath).exists()) sqLiteDatabase = openStandardDatabase()
        if (sqLiteDatabase == null) sqLiteDatabase = openAssetsDatabase()
        if (sqLiteDatabase == null) sqLiteDatabase = openAssetsDatabaseCompacted()
        if (sqLiteDatabase == null) throw SQLiteException("Esteja certo de ter adicionado na pasta " +
                "ASSETS arquivos com uma das extensões: $databaseName " +
                "ou ${databaseName.replace(".db", ".zip")}")
        return sqLiteDatabase
    }

    private fun openStandardDatabase(): SQLiteDatabase? {
        return try {
            SQLiteDatabase.openDatabase(File(standardDatabasePath).absolutePath, null, 0)
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
            textView?.text = context.getText(R.string.unziping_files)
            if (it.exists()) {
                ZipFile(it).use {
                    BufferedInputStream(it.getInputStream(it.getEntry(databaseName))).use { bis: BufferedInputStream ->
                        File(standardDatabasePath).outputStream().buffered().use {
                            /*progressBar?.max = bis.read()
                            progressBar?.progress = */bis.copyTo(it)/*.toInt()*/
                        }
                    }
                    textView?.text = context.getText(R.string.unziping_successfully)
                }
                openStandardDatabase()
            } else null
        }
    }

    private fun getInputStreamFromAssets(databaseRealFileName: String): InputStream? {
        return try {
            context.assets.open(databaseRealFileName)
        } catch (e: FileNotFoundException) {
            null
        }
    }

    private fun copyFileFromAssetsToStandardPath(file: File) {
        File(standardDatabaseDir).let { if (!it.exists()) it.mkdir() }
        getInputStreamFromAssets(file.name).let { inputStream: InputStream? ->
            if (inputStream != null) FileOutputStream(file.absolutePath).let {
                textView?.text = context.getText(R.string.moving_files)
                inputStream.copyTo(it)
            }
        }
    }

    companion object {
        const val DATABASES = "databases"
    }
}