package ru.yusupov.project.data

import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

object DatabaseDescription {
    const val AUTHORITY = "ru.yusupov.project.data"
    val BASE_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY")

    object Contact : BaseColumns {
        const val TABLE_NAME = "contacts"
        val CONTENT_URI: Uri = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build()

        const val COLUMN_NAME = "name"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_STREET = "street"
        const val COLUMN_CITY = "city"
        const val COLUMN_STATE = "state"
        const val COLUMN_ZIP = "zip"

        fun buildContactUri(id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, id)
        }
    }
}