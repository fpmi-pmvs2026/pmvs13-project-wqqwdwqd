package ru.yusupov.project.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class AddressBookDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "AddressBook.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE ${DatabaseDescription.Contact.TABLE_NAME} (
                ${BaseColumns._ID} INTEGER PRIMARY KEY,
                ${DatabaseDescription.Contact.COLUMN_NAME} TEXT,
                ${DatabaseDescription.Contact.COLUMN_PHONE} TEXT,
                ${DatabaseDescription.Contact.COLUMN_EMAIL} TEXT,
                ${DatabaseDescription.Contact.COLUMN_STREET} TEXT,
                ${DatabaseDescription.Contact.COLUMN_CITY} TEXT,
                ${DatabaseDescription.Contact.COLUMN_STATE} TEXT,
                ${DatabaseDescription.Contact.COLUMN_ZIP} TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
}