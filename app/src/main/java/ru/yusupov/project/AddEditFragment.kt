package ru.yusupov.project

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.google.android.material.snackbar.Snackbar
import ru.yusupov.project.data.DatabaseDescription.Contact
import ru.yusupov.project.databinding.FragmentAddEditBinding

class AddEditFragment : Fragment(),
    LoaderManager.LoaderCallbacks<Cursor> {

    interface AddEditFragmentListener {
        fun onAddEditCompleted(contactUri: Uri)
    }

    private var listener: AddEditFragmentListener? = null
    private lateinit var binding: FragmentAddEditBinding
    private lateinit var coordinatorLayout: View
    private var contactUri: Uri? = null
    private var addingNewContact = true
    private val CONTACT_LOADER = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as AddEditFragmentListener
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
        binding = FragmentAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        coordinatorLayout = requireActivity().findViewById(R.id.coordinatorLayout)

        binding.nameTextInputLayout.editText?.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButton()
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        binding.saveFloatingActionButton.setOnClickListener {
            hideKeyboard()
            saveContact()
        }

        arguments?.let { args ->
            addingNewContact = false
            contactUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                args.getParcelable(MainActivity.CONTACT_URI, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                args.getParcelable(MainActivity.CONTACT_URI)
            }
        }

        if (contactUri != null) {
            LoaderManager.getInstance(this).initLoader(CONTACT_LOADER, null, this)
        } else {
            updateSaveButton()
        }
    }

    private fun updateSaveButton() {
        val name = binding.nameTextInputLayout.editText?.text.toString().trim()
        if (name.isNotEmpty()) binding.saveFloatingActionButton.show()
        else binding.saveFloatingActionButton.hide()
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun saveContact() {
        val values = ContentValues().apply {
            put(Contact.COLUMN_NAME, binding.nameTextInputLayout.editText?.text.toString())
            put(Contact.COLUMN_PHONE, binding.phoneTextInputLayout.editText?.text.toString())
            put(Contact.COLUMN_EMAIL, binding.emailTextInputLayout.editText?.text.toString())
            put(Contact.COLUMN_STREET, binding.streetTextInputLayout.editText?.text.toString())
            put(Contact.COLUMN_CITY, binding.cityTextInputLayout.editText?.text.toString())
            put(Contact.COLUMN_STATE, binding.stateTextInputLayout.editText?.text.toString())
            put(Contact.COLUMN_ZIP, binding.zipTextInputLayout.editText?.text.toString())
        }

        if (addingNewContact) {
            val newUri = requireContext().contentResolver.insert(Contact.CONTENT_URI, values)
            if (newUri != null) {
                Snackbar.make(coordinatorLayout, R.string.contact_added, Snackbar.LENGTH_LONG).show()
                listener?.onAddEditCompleted(newUri)
            } else {
                Snackbar.make(coordinatorLayout, R.string.contact_not_added, Snackbar.LENGTH_LONG).show()
            }
        } else {
            val updated = contactUri?.let {
                requireContext().contentResolver.update(it, values, null, null)
            } ?: 0
            if (updated > 0) {
                Snackbar.make(coordinatorLayout, R.string.contact_updated, Snackbar.LENGTH_LONG).show()
                listener?.onAddEditCompleted(contactUri!!)
            } else {
                Snackbar.make(coordinatorLayout, R.string.contact_not_updated, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return when (id) {
            CONTACT_LOADER -> CursorLoader(requireContext(), contactUri!!, null, null, null, null)
            else -> throw IllegalStateException()
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        if (data != null && data.moveToFirst()) {
            with(binding) {
                nameTextInputLayout.editText?.setText(data.getString(data.getColumnIndexOrThrow(Contact.COLUMN_NAME)))
                phoneTextInputLayout.editText?.setText(data.getString(data.getColumnIndexOrThrow(Contact.COLUMN_PHONE)))
                emailTextInputLayout.editText?.setText(data.getString(data.getColumnIndexOrThrow(Contact.COLUMN_EMAIL)))
                streetTextInputLayout.editText?.setText(data.getString(data.getColumnIndexOrThrow(Contact.COLUMN_STREET)))
                cityTextInputLayout.editText?.setText(data.getString(data.getColumnIndexOrThrow(Contact.COLUMN_CITY)))
                stateTextInputLayout.editText?.setText(data.getString(data.getColumnIndexOrThrow(Contact.COLUMN_STATE)))
                zipTextInputLayout.editText?.setText(data.getString(data.getColumnIndexOrThrow(Contact.COLUMN_ZIP)))
            }
            updateSaveButton()
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {}
}