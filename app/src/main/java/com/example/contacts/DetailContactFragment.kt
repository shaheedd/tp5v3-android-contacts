package com.example.contacts

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream

/** Écran 2 : consultation, modification et actions du contact. */
class DetailContactFragment : Fragment(R.layout.fragment_detail) {

    private lateinit var contact: Contact
    private var currentPhotoPath: String? = null
    private var cameraPhotoUri: Uri? = null

    private lateinit var imgPhotoDetail: ImageView
    private lateinit var etNom: EditText
    private lateinit var etPrenom: EditText
    private lateinit var etTel: EditText
    private lateinit var etEmail: EditText
    private lateinit var etLat: EditText
    private lateinit var etLon: EditText

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) {
                toast(R.string.msg_photo_galerie_annulee)
            } else {
                val savedPath = copyToInternalStorage(uri)
                if (savedPath == null) toast(R.string.msg_erreur_photo) else {
                    currentPhotoPath = savedPath
                    displayPhoto()
                }
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraPhotoUri != null) {
                currentPhotoPath = cameraPhotoUri.toString()
                displayPhoto()
            } else {
                toast(R.string.msg_photo_camera_annulee)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getString(ARG_CONTACT_ID)
        contact = id?.let { ContactRepository.find(it) } ?: run {
            parentFragmentManager.popBackStack()
            return
        }

        imgPhotoDetail = view.findViewById(R.id.imgPhotoDetail)
        etNom = view.findViewById(R.id.etNom)
        etPrenom = view.findViewById(R.id.etPrenom)
        etTel = view.findViewById(R.id.etTel)
        etEmail = view.findViewById(R.id.etEmail)
        etLat = view.findViewById(R.id.etLat)
        etLon = view.findViewById(R.id.etLon)

        if (savedInstanceState == null) {
            etNom.setText(contact.nom)
            etPrenom.setText(contact.prenom)
            etTel.setText(contact.telephone)
            etEmail.setText(contact.email)
            etLat.setText(contact.latitude?.toString().orEmpty())
            etLon.setText(contact.longitude?.toString().orEmpty())
            currentPhotoPath = contact.photoPath
        } else {
            etNom.setText(savedInstanceState.getString(KEY_NOM, contact.nom))
            etPrenom.setText(savedInstanceState.getString(KEY_PRENOM, contact.prenom))
            etTel.setText(savedInstanceState.getString(KEY_TEL, contact.telephone))
            etEmail.setText(savedInstanceState.getString(KEY_EMAIL, contact.email))
            etLat.setText(savedInstanceState.getString(KEY_LAT, contact.latitude?.toString().orEmpty()))
            etLon.setText(savedInstanceState.getString(KEY_LON, contact.longitude?.toString().orEmpty()))
            currentPhotoPath = savedInstanceState.getString(KEY_PHOTO, contact.photoPath)
        }
        displayPhoto()

        view.findViewById<Button>(R.id.btnChoisirPhoto).setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        view.findViewById<Button>(R.id.btnPrendrePhoto).setOnClickListener { takePhoto() }
        view.findViewById<Button>(R.id.btnAppeler).setOnClickListener { callContact() }
        view.findViewById<Button>(R.id.btnEmail).setOnClickListener { emailContact() }
        view.findViewById<Button>(R.id.btnVoirCarte).setOnClickListener { showMap() }
        view.findViewById<Button>(R.id.btnEnregistrer).setOnClickListener { saveContact() }
        view.findViewById<Button>(R.id.btnAnnuler).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (view != null) {
            outState.putString(KEY_NOM, etNom.text.toString())
            outState.putString(KEY_PRENOM, etPrenom.text.toString())
            outState.putString(KEY_TEL, etTel.text.toString())
            outState.putString(KEY_EMAIL, etEmail.text.toString())
            outState.putString(KEY_LAT, etLat.text.toString())
            outState.putString(KEY_LON, etLon.text.toString())
            outState.putString(KEY_PHOTO, currentPhotoPath)
        }
    }

    private fun displayPhoto() {
        val bitmap = PhotoLoader.load(requireContext().contentResolver, currentPhotoPath)
        if (bitmap != null) imgPhotoDetail.setImageBitmap(bitmap)
        else imgPhotoDetail.setImageResource(R.drawable.ic_person)
    }

    /** Copie l'image de la galerie dans les fichiers de l'application pour qu'elle reste lisible. */
    private fun copyToInternalStorage(source: Uri): String? {
        return try {
            val dir = File(requireContext().filesDir, "photos").apply { mkdirs() }
            val file = File(dir, "photo_${System.currentTimeMillis()}.jpg")
            val input = requireContext().contentResolver.openInputStream(source) ?: return null
            input.use { stream ->
                FileOutputStream(file).use { output -> stream.copyTo(output) }
            }
            FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            ).toString()
        } catch (e: Exception) {
            null
        }
    }

    private fun takePhoto() {
        if (!requireContext().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            toast(R.string.msg_pas_camera)
            return
        }
        val dir = File(requireContext().cacheDir, "camera").apply { mkdirs() }
        val file = File(dir, "photo_${System.currentTimeMillis()}.jpg")
        cameraPhotoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )
        try {
            takePictureLauncher.launch(cameraPhotoUri!!)
        } catch (e: Exception) {
            toast(R.string.msg_pas_camera)
        }
    }

    /** Intent implicite : ouvre le composeur téléphonique. */
    private fun callContact() {
        val number = etTel.text.toString().replace(" ", "")
        if (number.isBlank()) {
            etTel.error = getString(R.string.msg_champs_vides)
            return
        }
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            toast(R.string.msg_pas_app_tel)
        }
    }

    /** Intent implicite : ouvre une application de messagerie avec le destinataire prérempli. */
    private fun emailContact() {
        val address = etEmail.text.toString().trim()
        if (address.isBlank()) {
            etEmail.error = getString(R.string.msg_champs_vides)
            return
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
            putExtra(Intent.EXTRA_SUBJECT, "Contact")
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(Intent.createChooser(intent, getString(R.string.envoyer_email)))
        } else {
            toast(R.string.msg_pas_app_mail)
        }
    }

    /** Lit les coordonnées : null/null = absentes, null seul = erreur. */
    private fun readCoordinates(): Pair<Double?, Double?>? {
        val latText = etLat.text.toString().trim()
        val lonText = etLon.text.toString().trim()
        if (latText.isEmpty() && lonText.isEmpty()) return Pair(null, null)
        if (latText.isEmpty() || lonText.isEmpty()) {
            toast(R.string.msg_coord_absentes)
            return null
        }
        val lat = latText.toDoubleOrNull()
        val lon = lonText.toDoubleOrNull()
        if (lat == null || lon == null) {
            toast(R.string.msg_coord_numerique)
            return null
        }
        if (lat !in -90.0..90.0 || lon !in -180.0..180.0) {
            toast(R.string.msg_coord_invalides)
            return null
        }
        return Pair(lat, lon)
    }

    private fun validateFields(): Boolean {
        val fields = listOf(etNom, etPrenom, etTel, etEmail)
        val empty = fields.filter { it.text.isBlank() }
        if (empty.isNotEmpty()) {
            empty.forEach { it.error = getString(R.string.msg_champs_vides) }
            toast(R.string.msg_champs_vides)
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
            etEmail.error = getString(R.string.msg_email_invalide)
            return false
        }
        return readCoordinates() != null
    }

    private fun saveContact() {
        if (!validateFields()) return
        val coords = readCoordinates() ?: return
        val updated = Contact(
            id = contact.id,
            nom = etNom.text.toString().trim(),
            prenom = etPrenom.text.toString().trim(),
            telephone = etTel.text.toString().trim(),
            email = etEmail.text.toString().trim(),
            photoPath = currentPhotoPath,
            latitude = coords.first,
            longitude = coords.second
        )
        ContactRepository.update(updated)
        parentFragmentManager.setFragmentResult(REQ_CONTACT_SAVED, bundleOf(KEY_UPDATED to true))
        Toast.makeText(requireContext(), R.string.msg_enregistre, Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }

    private fun showMap() {
        val lat = contact.latitude
        val lon = contact.longitude
        if (lat == null || lon == null) {
            toast(R.string.msg_coord_absentes)
            return
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CarteContactFragment.newInstance(contact.id))
            .addToBackStack("carte")
            .commit()
    }

    private fun toast(messageRes: Int) {
        Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ARG_CONTACT_ID = "contact_id"
        const val KEY_UPDATED = "updated"
        private const val KEY_NOM = "nom"
        private const val KEY_PRENOM = "prenom"
        private const val KEY_TEL = "tel"
        private const val KEY_EMAIL = "email"
        private const val KEY_LAT = "lat"
        private const val KEY_LON = "lon"
        private const val KEY_PHOTO = "photo"

        fun newInstance(contactId: String) = DetailContactFragment().apply {
            arguments = bundleOf(ARG_CONTACT_ID to contactId)
        }
    }
}
