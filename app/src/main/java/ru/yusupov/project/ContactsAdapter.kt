package ru.yusupov.project

import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.yusupov.project.data.DatabaseDescription.Contact

class ContactsAdapter(private val clickListener: (Uri) -> Unit) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    private var cursor: Cursor? = null

    inner class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val textView: android.widget.TextView = itemView.findViewById(android.R.id.text1)
        private var rowId: Long = 0

        init {
            itemView.setOnClickListener {
                clickListener(Contact.buildContactUri(rowId))
            }
        }

        fun bind(cursor: Cursor) {
            rowId = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            textView.text = cursor.getString(cursor.getColumnIndexOrThrow(Contact.COLUMN_NAME))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor?.moveToPosition(position)
        cursor?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = cursor?.count ?: 0

    fun swapCursor(newCursor: Cursor?) {
        cursor = newCursor
        notifyDataSetChanged()
    }
}