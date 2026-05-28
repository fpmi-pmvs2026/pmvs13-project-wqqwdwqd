package ru.yusupov.project

import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import ru.yusupov.project.data.DatabaseDescription.Contact
import ru.yusupov.project.databinding.FragmentDetailBinding

class DetailFragment : Fragment(),
    LoaderManager.LoaderCallbacks<Cursor> {

    interface DetailFragmentListener {
        fun onContactDeleted()
        fun onEditContact(contactUri: Uri)
    }

    private var listener: DetailFragmentListener? = null
    private lateinit var binding: FragmentDetailBinding
    private lateinit var contactUri: Uri
    private val CONTACT_LOADER = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as DetailFragmentListener
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
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        contactUri = arguments?.getParcelable(MainActivity.CONTACT_URI) ?: throw IllegalStateException()
        LoaderManager.getInstance(this).initLoader(CONTACT_LOADER, null, this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_details_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                listener?.onEditContact(contactUri)
                true
            }
            R.id.action_delete -> {
                deleteContact()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteContact() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.confirm_title)
            .setMessage(R.string.confirm_message)
            .setPositiveButton(R.string.button_delete) { _, _ ->
                requireContext().contentResolver.delete(contactUri, null, null)
                listener?.onContactDeleted()
            }
            .setNegativeButton(R.string.button_cancel, null)
            .create()
            .show()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return when (id) {
            CONTACT_LOADER -> CursorLoader(requireContext(), contactUri, null, null, null, null)
            else -> throw IllegalStateException()
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (data != null && data.moveToFirst()) {
            with(binding) {
                nameTextView.text = data.getString(data.getColumnIndexOrThrow(Contact.COLUMN_NAME))
                phoneTextView.text = data.getString(data.getColumnIndexOrThrow(Contact.COLUMN_PHONE))
                emailTextView.text = data.getString(data.getColumnIndexOrThrow(Contact.COLUMN_EMAIL))
                streetTextView.text = data.getString(data.getColumnIndexOrThrow(Contact.COLUMN_STREET))
                cityTextView.text = data.getString(data.getColumnIndexOrThrow(Contact.COLUMN_CITY))
                stateTextView.text = data.getString(data.getColumnIndexOrThrow(Contact.COLUMN_STATE))
                zipTextView.text = data.getString(data.getColumnIndexOrThrow(Contact.COLUMN_ZIP))
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {}
}