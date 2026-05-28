package ru.yusupov.project

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import ru.yusupov.project.data.DatabaseDescription.Contact
import ru.yusupov.project.databinding.FragmentContactsBinding

class ContactsFragment : Fragment(),
    LoaderManager.LoaderCallbacks<Cursor> {

    interface ContactsFragmentListener {
        fun onContactSelected(contactUri: Uri)
        fun onAddContact()
    }

    private var listener: ContactsFragmentListener? = null
    private lateinit var binding: FragmentContactsBinding
    private lateinit var adapter: ContactsAdapter
    private val CONTACTS_LOADER = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as ContactsFragmentListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ContactsAdapter { contactUri ->
            listener?.onContactSelected(contactUri)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(ItemDivider(requireContext()))
        binding.recyclerView.setHasFixedSize(true)

        binding.addButton.setOnClickListener {
            listener?.onAddContact()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LoaderManager.getInstance(this).initLoader(CONTACTS_LOADER, null, this)
    }

    fun updateContactList() {
        adapter.notifyDataSetChanged()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return when (id) {
            CONTACTS_LOADER -> CursorLoader(
                requireContext(),
                Contact.CONTENT_URI,
                null,
                null,
                null,
                "${Contact.COLUMN_NAME} COLLATE NOCASE ASC"
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        adapter.swapCursor(data)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.swapCursor(null)
    }
}