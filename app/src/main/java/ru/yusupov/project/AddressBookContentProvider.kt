package ru.yusupov.project.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.provider.BaseColumns
import ru.yusupov.project.R

class AddressBookContentProvider : ContentProvider() {

    private lateinit var dbHelper: AddressBookDatabaseHelper

    private companion object {
        private const val ONE_CONTACT = 1
        private const val CONTACTS = 2
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(DatabaseDescription.AUTHORITY, "${DatabaseDescription.Contact.TABLE_NAME}/#", ONE_CONTACT)
            addURI(DatabaseDescription.AUTHORITY, DatabaseDescription.Contact.TABLE_NAME, CONTACTS)
        }
    }

    override fun onCreate(): Boolean {
        dbHelper = AddressBookDatabaseHelper(context!!)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val queryBuilder = SQLiteQueryBuilder().apply {
            tables = DatabaseDescription.Contact.TABLE_NAME
        }
        when (uriMatcher.match(uri)) {
            ONE_CONTACT -> queryBuilder.appendWhere("${BaseColumns._ID}=${uri.lastPathSegment}")
            CONTACTS -> { /* select all */ }
            else -> throw UnsupportedOperationException(
                context?.getString(R.string.invalid_query_uri) + uri)
        }
        val cursor = queryBuilder.query(
            dbHelper.readableDatabase,
            projection, selection, selectionArgs, null, null, sortOrder
        )
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (uriMatcher.match(uri)) {
            CONTACTS -> {
                val rowId = dbHelper.writableDatabase.insert(DatabaseDescription.Contact.TABLE_NAME, null, values)
                if (rowId > 0) {
                    val newUri = DatabaseDescription.Contact.buildContactUri(rowId)
                    context?.contentResolver?.notifyChange(uri, null)
                    return newUri
                } else {
                    throw android.database.SQLException(context?.getString(R.string.insert_failed) + uri)
                }
            }
            else -> throw UnsupportedOperationException(
                context?.getString(R.string.invalid_insert_uri) + uri)
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        var numberOfRowsUpdated = 0
        when (uriMatcher.match(uri)) {
            ONE_CONTACT -> {
                val id = uri.lastPathSegment
                numberOfRowsUpdated = dbHelper.writableDatabase.update(
                    DatabaseDescription.Contact.TABLE_NAME,
                    values,
                    "${BaseColumns._ID}=$id",
                    selectionArgs
                )
            }
            else -> throw UnsupportedOperationException(
                context?.getString(R.string.invalid_update_uri) + uri)
        }
        if (numberOfRowsUpdated != 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return numberOfRowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        var numberOfRowsDeleted = 0
        when (uriMatcher.match(uri)) {
            ONE_CONTACT -> {
                val id = uri.lastPathSegment
                numberOfRowsDeleted = dbHelper.writableDatabase.delete(
                    DatabaseDescription.Contact.TABLE_NAME,
                    "${BaseColumns._ID}=$id",
                    selectionArgs
                )
            }
            else -> throw UnsupportedOperationException(
                context?.getString(R.string.invalid_delete_uri) + uri)
        }
        if (numberOfRowsDeleted != 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return numberOfRowsDeleted
    }

    override fun getType(uri: Uri): String? = null
}