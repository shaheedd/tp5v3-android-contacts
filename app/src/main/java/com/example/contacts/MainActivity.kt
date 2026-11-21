package com.example.contacts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Activité unique : elle ne fait qu'héberger les fragments dans le
 * FragmentContainerView. La pile de fragments est gérée par le FragmentManager.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // savedInstanceState != null signifie une recréation (rotation) :
        // le FragmentManager restaure déjà les fragments, on évite donc d'en empiler un autre.
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ListeContactsFragment())
                .commit()
        }
    }
}
