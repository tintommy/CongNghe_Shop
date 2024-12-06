package com.example.tttn_electronicsstore_manager_admin_app.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteAbortException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "myDatabase.db"
        private const val DATABASE_VERSION = 1

        // Tên bảng và cột
        const val TABLE_NAME = "MyStrings"
        const val COLUMN_ID = "id"
        const val COLUMN_STRING = "stringValue"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_STRING TEXT)"
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun isTableEmpty(): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
        var isEmpty = true
        if (cursor.moveToFirst()) {
            isEmpty = cursor.getInt(0) == 0
        }
        cursor.close()
        db.close()
        return isEmpty
    }

    fun insertStringList(values: List<String>) {
        if (isTableEmpty()) {
            val db = this.writableDatabase
            db.beginTransaction() // Bắt đầu transaction

            try {
                for (value in values) {
                    val contentValues = ContentValues().apply {
                        put(COLUMN_STRING, value)
                    }
                    db.insert(TABLE_NAME, null, contentValues)
                }

                db.setTransactionSuccessful()
            } catch (e: Exception) {

                e.printStackTrace()
            } finally {
                db.endTransaction()
                db.close()
            }
        }
    }

    fun getAllStrings(): List<String> {
        val db = this.readableDatabase
        val list = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val stringValue = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STRING))
                list.add(stringValue)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return list
    }
}