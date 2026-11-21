package com.example.contacts

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/** Écran 3 : position GPS du contact, avec SDK Google Maps ou vue de repli. */
class CarteContactFragment : Fragment(R.layout.fragment_carte), OnMapReadyCallback {

    private var contact: Contact? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getString(DetailContactFragment.ARG_CONTACT_ID)
        contact = id?.let { ContactRepository.find(it) }
        val current = contact
        val lat = current?.latitude
        val lon = current?.longitude

        view.findViewById<TextView>(R.id.tvCoordonnees).text = getString(
            R.string.msg_position,
        ) + " ${lat ?: "—"} , ${lon ?: "—"}"

        val container = view.findViewById<FrameLayout>(R.id.mapContainer)
        if (lat != null && lon != null && BuildConfig.MAPS_API_KEY.isNotBlank()) {
            // Clé présente : utilisation réelle du SDK Google Maps.
            val mapFragment = SupportMapFragment.newInstance()
            childFragmentManager.beginTransaction()
                .replace(R.id.mapContainer, mapFragment)
                .commit()
            mapFragment.getMapAsync(this)
        } else {
            // Aucune clé publiée dans le dépôt : vue de repli avec marqueur.
            container.addView(MapFallbackView(requireContext(), current?.nom.orEmpty()))
        }

        view.findViewById<Button>(R.id.btnRetourDetail).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val current = contact ?: return
        val lat = current.latitude ?: return
        val lon = current.longitude ?: return
        val position = LatLng(lat, lon)
        googleMap.addMarker(MarkerOptions().position(position).title(current.nom))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13f))
    }

    companion object {
        fun newInstance(contactId: String) = CarteContactFragment().apply {
            arguments = bundleOf(DetailContactFragment.ARG_CONTACT_ID to contactId)
        }
    }
}
