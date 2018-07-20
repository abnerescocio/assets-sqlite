# assets-sqlite

It's a simple and light library to access sqlite database from assets folder on android appllications

Your database can be
* .db
* .zip

## How to add ##

Look this followings steps

**Step 1.** Add the JitPack repository to your build file

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add the dependency

```
dependencies {
    implementation 'com.github.abnerescocio:assets-sqlite:1.3.1'
}
```

## How to use ##

**Step 1.** Put your `database.db` or `database.zip` on assets folder

**Step 2.** Extend the class `com.abnerescocio.assetssqlite.lib.AssetsSQLite`
```
class AppAssetsSQLite(context: Context): AssetsSQLite(context, DATABASE_NAME) {
    companion object {
        const val DATABASE_NAME = "database.db"
    }
}
```
The `DATABASE_NAME` must be the name of your file database filanizing with extension `.db`

**Please, not put `DATABASE_NAME` as `database.zip`**

**Step 3.** Create a controller to manipule your database
```
open class AppDBController(context: Context) {
    private val sqLiteDatabase: SQLiteDatabase?

    init {
        val appAssetsSQLite = AppAssetsSQLite(context)
        sqLiteDatabase = appAssetsSQLite.writableDatabase
    }

    fun selectAll(tableName: String): Cursor? {
        return sqLiteDatabase?.rawQuery(tableName, null)
    }

    fun close() {
        sqLiteDatabase?.close()
    }
}
```

**Step final.** Instantiate your controller to access your database
```
val controller = AppDBController(context)
val cursor = controller.selectAll("your_table_name")
```

We are open to your contribuition. Thanks for use it!
