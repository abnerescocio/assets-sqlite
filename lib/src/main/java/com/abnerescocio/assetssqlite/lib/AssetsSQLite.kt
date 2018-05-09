package com.abnerescocio.assetssqlite.lib

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabaseLockedException
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Environment
import android.os.StatFs
import android.util.Log
import java.io.*
import java.math.BigDecimal
import java.util.zip.ZipFile

/**
 * Created by abnerESC on 02/03/2018
 */
open class AssetsSQLite(private val context: Context, name: String,
                   factory: SQLiteDatabase.CursorFactory?, private val newVersion: Int)
    : SQLiteOpenHelper(context, name, factory, newVersion) {

    private val standardDatabaseDir: String
    private val standardDatabasePath: String
    private var listener: ProgressAssetsSQLiteListener? = null

    init {
        standardDatabaseDir = context.applicationInfo.dataDir + File.separator +
                DATABASES + File.separator
        standardDatabasePath = standardDatabaseDir + databaseName
        if (context is ProgressAssetsSQLiteListener) listener = context
    }

    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    override fun getWritableDatabase(): SQLiteDatabase? {
        var sqLiteDatabase = openStandardDatabase()
        if (sqLiteDatabase != null && sqLiteDatabase.version < newVersion) sqLiteDatabase = null
        if (sqLiteDatabase == null) sqLiteDatabase = openAssetsDatabase()
        if (sqLiteDatabase == null) sqLiteDatabase = openAssetsDatabaseCompacted()
        if (sqLiteDatabase == null) listener?.onErrorUnziping(SQLiteException("Esteja certo de ter adicionado na pasta " +
                "ASSETS arquivos com uma das extensões: $databaseName " +
                "ou ${databaseName.replace(".db", ".zip")}"))
        try {
            sqLiteDatabase?.beginTransaction()
            sqLiteDatabase?.version = newVersion
            sqLiteDatabase?.setTransactionSuccessful()
            sqLiteDatabase?.endTransaction()
        } catch (e: SQLiteDatabaseLockedException) {
            e.printStackTrace()
        }
        return sqLiteDatabase
    }

    private fun openStandardDatabase(): SQLiteDatabase? {
        return try {
            SQLiteDatabase.openDatabase(File(standardDatabasePath).absolutePath, null, 0)
        } catch (e: SQLiteException) {
            e.printStackTrace()
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
            val stateFs = StatFs(Environment.getExternalStorageDirectory().absolutePath)
            Log.i(TAG, context.getString(R.string.unziping_files))
            if (it.exists()) {
                ZipFile(it).use { zip ->
                    zip.getEntry(databaseName).let { entry ->
                        if (stateFs.availableBytes > entry.size) {
                            BufferedInputStream(zip.getInputStream(entry)).use { bis ->
                                File(standardDatabasePath).outputStream().buffered().use { bos ->
                                    var bytesSizeUnziped: Long = 0
                                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                                    var bytes = bis.read(buffer)
                                    context as Activity
                                    while (bytes >= 0) {
                                        bos.write(buffer, 0, bytes)
                                        bytesSizeUnziped += bytes
                                        bytes = bis.read(buffer)
                                        val progress = (bytesSizeUnziped.toDouble() / entry.size.toDouble()) * 100.0
                                        context.runOnUiThread(Runnable {
                                            listener?.onProgressAssetsSQLiteUnziping(entry.compressedSize,
                                                    entry.size, bytesSizeUnziped, progress.roundTo2DecimalPlaces())
                                        })
                                    }
                                    context.runOnUiThread(Runnable {
                                        listener?.onFinishUnzip(entry.compressedSize, bytesSizeUnziped)
                                    })
                                }
                            }
                            Log.i(TAG, context.getString(R.string.unziping_successfully))
                        } else {
                            listener?.onErrorUnziping(IOException("Armazenamento interno insuficiente. " +
                                    "Necessário ${(entry.size / 1024) / 1024} MB, " +
                                    "disponível ${(stateFs.availableBytes / 1024) / 1024} MB"))
                            return null
                        }
                    }
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

    interface ProgressAssetsSQLiteListener {
        fun onProgressAssetsSQLiteUnziping(compressedBytesSize: Long, bytesSize: Long,
                                           bytesSizeUnziped: Long, progress: Double)
        fun onFinishUnzip(compressedBytesSize: Long, bytesSizeUnziped: Long)
        fun onErrorUnziping(exception: Exception)
    }

    private fun Double.roundTo2DecimalPlaces() = BigDecimal(this)
            .setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()

    companion object {
        const val TAG = "AssetsSQLite"
        const val DATABASES = "databases"
    }
}