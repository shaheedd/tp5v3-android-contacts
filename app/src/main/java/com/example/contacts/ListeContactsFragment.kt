package com.example.contacts

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

const val REQ_CONTACT_SAVED = "contact_saved"

/** Écran 1 : liste filtrable des contacts dans une RecyclerView. */
class ListeContactsFragment : Fragment(R.layout.fragment_liste) {

    private lateinit var adapter: ContactAdapter
    private lateinit var tvEmpty: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvEmpty = view.findViewById(R.id.tvEmpty)
        adapter = ContactAdapter(requireContext()) { contact -> openDetail(contact.id) }

        view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@ListeContactsFragment.adapter
        }

        // Fragment Result API : le détail notifie la liste après un enregistrement.
        parentFragmentManager.setFragmentResultListener(REQ_CONTACT_SAVED, viewLifecycleOwner) { _, _ ->
            refresh()
        }

        view.findViewById<EditText>(R.id.etRecherche).addTextChangedListener { text ->
            adapter.filter(text.toString())
            updateEmptyState()
        }

        refresh()
    }

    private fun refresh() {
        adapter.submitList(ContactRepository.all())
        updateEmptyState()
    }

    private fun updateEmptyState() {
        tvEmpty.visibility = if (adapter.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun openDetail(contactId: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, DetailContactFragment.newInstance(contactId))
            .addToBackStack("detail")
            .commit()
    }
}
