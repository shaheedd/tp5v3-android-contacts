package com.example.contacts

/**
 * Modèle de contact. L'identifiant stable permet de transmettre uniquement un id
 * entre fragments, ce qui évite les erreurs d'indice après filtrage.
 */
data class Contact(
    val id: String,
    val nom: String,
    val prenom: String,
    val telephone: String,
    val email: String,
    val photoPath: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)
