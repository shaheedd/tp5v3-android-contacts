package com.example.contacts

/** Collection en mémoire des contacts (le stockage permanent n'est pas exigé). */
object ContactRepository {

    private val contacts = mutableListOf(
        Contact(
            id = "c1",
            nom = "Dupont",
            prenom = "Claire",
            telephone = "06 12 34 56 78",
            email = "claire.dupont@example.com",
            latitude = 48.8566,
            longitude = 2.3522
        ),
        Contact(
            id = "c2",
            nom = "Martin",
            prenom = "Thomas",
            telephone = "07 23 45 67 89",
            email = "thomas.martin@example.com",
            latitude = 45.7640,
            longitude = 4.8357
        ),
        Contact(
            id = "c3",
            nom = "Bernard",
            prenom = "Sophie",
            telephone = "06 98 76 54 32",
            email = "sophie.bernard@example.com"
        ),
        Contact(
            id = "c4",
            nom = "Petit",
            prenom = "Lucas",
            telephone = "07 11 22 33 44",
            email = "lucas.petit@example.com",
            latitude = 43.2965,
            longitude = 5.3698
        ),
        Contact(
            id = "c5",
            nom = "Moreau",
            prenom = "Julien",
            telephone = "06 55 44 33 22",
            email = "julien.moreau@example.com",
            latitude = 50.6292,
            longitude = 3.0573
        )
    )

    fun all(): List<Contact> = contacts.toList()

    fun find(id: String): Contact? = contacts.find { it.id == id }

    fun update(updated: Contact) {
        val index = contacts.indexOfFirst { it.id == updated.id }
        if (index >= 0) contacts[index] = updated
    }
}
