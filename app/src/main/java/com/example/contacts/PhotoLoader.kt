package com.example.contacts

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri

/** Décode une photo stockée sous forme d'Uri, avec réduction d'échantillonnage. */
object PhotoLoader {
    fun load(contentResolver: ContentResolver, path: String?) = try {
        if (path.isNullOrBlank()) {
            null
        } else {
            val options = BitmapFactory.Options().apply { inSampleSize = 2 }
            contentResolver.openInputStream(Uri.parse(path))?.use {
                BitmapFactory.decodeStream(it, null, options)
            }
        }
    } catch (e: Exception) {
        null
    }
}
