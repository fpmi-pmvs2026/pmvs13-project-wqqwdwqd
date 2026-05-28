package ru.yusupov.project

import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import ru.yusupov.project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),
    ContactsFragment.ContactsFragmentListener,
    DetailFragment.DetailFragmentListener,
    AddEditFragment.AddEditFragmentListener {

    companion object {
        const val CONTACT_URI = "contact_uri"
    }

    private lateinit var binding: ActivityMainBinding
    private var contactsFragment: ContactsFragment? = null
    private var fragmentContainer: FrameLayout? = null
    private var rightPaneContainer: FrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        fragmentContainer = findViewById(R.id.fragmentContainer)
        rightPaneContainer = findViewById(R.id.rightPaneContainer)

        if (savedInstanceState != null && fragmentContainer != null) {
            contactsFragment = ContactsFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, contactsFragment!!)
                .commit()
        } else {
            contactsFragment = supportFragmentManager.findFragmentById(R.id.contactsFragment) as ContactsFragment?
        }
    }

    override fun onContactSelected(contactUri: Uri) {
        if (fragmentContainer != null) {
            displayContact(contactUri, R.id.fragmentContainer)
        } else {
            supportFragmentManager.popBackStack()
            displayContact(contactUri, R.id.rightPaneContainer)
        }
    }

    override fun onAddContact() {
        if (fragmentContainer != null) {
            displayAddEditFragment(R.id.fragmentContainer, null)
        } else {
            displayAddEditFragment(R.id.rightPaneContainer, null)
        }
    }

    override fun onContactDeleted() {
        supportFragmentManager.popBackStack()
        contactsFragment?.updateContactList()
    }

    override fun onEditContact(contactUri: Uri) {
        if (fragmentContainer != null) {
            displayAddEditFragment(R.id.fragmentContainer, contactUri)
        } else {
            displayAddEditFragment(R.id.rightPaneContainer, contactUri)
        }
    }

    override fun onAddEditCompleted(contactUri: Uri) {
        supportFragmentManager.popBackStack()
        contactsFragment?.updateContactList()
        if (fragmentContainer == null) {
            supportFragmentManager.popBackStack()
            displayContact(contactUri, R.id.rightPaneContainer)
        }
    }

    private fun displayContact(contactUri: Uri, viewID: Int) {
        val detailFragment = DetailFragment().apply {
            arguments = Bundle().apply { putParcelable(CONTACT_URI, contactUri) }
        }
        supportFragmentManager.beginTransaction()
            .replace(viewID, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun displayAddEditFragment(viewID: Int, contactUri: Uri?) {
        val addEditFragment = AddEditFragment().apply {
            if (contactUri != null) {
                arguments = Bundle().apply { putParcelable(CONTACT_URI, contactUri) }
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(viewID, addEditFragment)
            .addToBackStack(null)
            .commit()
    }
}