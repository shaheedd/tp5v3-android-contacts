package com.example.contacts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter RecyclerView avec ViewHolder. Le filtrage porte sur une copie de la liste :
 * les identifiants stables des contacts sont conservés, donc le clic sur une ligne
 * filtrée ouvre toujours le bon contact.
 */
class ContactAdapter(
    private val context: Context,
    private val onItemClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private val allContacts = mutableListOf<Contact>()
    private val visibleContacts = mutableListOf<Contact>()

    fun submitList(contacts: List<Contact>) {
        allContacts.clear()
        allContacts.addAll(contacts)
        filter("")
    }

    fun filter(query: String) {
        val q = query.trim().lowercase()
        visibleContacts.clear()
        visibleContacts.addAll(
            if (q.isEmpty()) {
                allContacts
            } else {
                allContacts.filter {
                    it.nom.lowercase().contains(q) ||
                        it.prenom.lowercase().contains(q) ||
                        it.telephone.lowercase().contains(q)
                }
            }
        )
        notifyDataSetChanged()
    }

    fun isEmpty() = visibleContacts.isEmpty()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = visibleContacts[position]
        holder.bind(contact)
        holder.itemView.setOnClickListener { onItemClick(contact) }
    }

    override fun getItemCount(): Int = visibleContacts.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgPhoto: ImageView = view.findViewById(R.id.imgPhoto)
        private val tvNom: TextView = view.findViewById(R.id.tvNom)
        private val tvPrenom: TextView = view.findViewById(R.id.tvPrenom)
        private val tvTel: TextView = view.findViewById(R.id.tvTel)

        fun bind(contact: Contact) {
            tvNom.text = contact.nom
            tvPrenom.text = contact.prenom
            tvTel.text = contact.telephone
            val bitmap = PhotoLoader.load(context.contentResolver, contact.photoPath)
            if (bitmap != null) {
                imgPhoto.setImageBitmap(bitmap)
            } else {
                imgPhoto.setImageResource(R.drawable.ic_person)
            }
        }
    }
}
