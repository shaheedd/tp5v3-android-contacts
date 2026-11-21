package com.example.contacts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

/**
 * Vue cartographique de démonstration utilisée lorsqu'aucune clé Google Maps
 * n'est configurée localement. Elle centre un marqueur portant le nom du contact.
 */
class MapFallbackView(context: Context, private val label: String) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()

        // Fond « terrain ».
        paint.color = Color.rgb(224, 235, 220)
        canvas.drawRect(0f, 0f, w, h, paint)

        // Étendue d'eau.
        paint.color = Color.rgb(176, 214, 235)
        canvas.drawRect(0f, h * 0.62f, w * 0.34f, h, paint)

        // Parc.
        paint.color = Color.rgb(196, 222, 185)
        canvas.drawRect(w * 0.62f, h * 0.12f, w * 0.92f, h * 0.42f, paint)

        // Réseau de routes.
        paint.color = Color.WHITE
        paint.strokeWidth = 14f
        canvas.drawLine(0f, h * 0.30f, w, h * 0.30f, paint)
        canvas.drawLine(0f, h * 0.72f, w, h * 0.72f, paint)
        canvas.drawLine(w * 0.28f, 0f, w * 0.28f, h, paint)
        canvas.drawLine(w * 0.70f, 0f, w * 0.70f, h, paint)
        paint.strokeWidth = 4f
        paint.color = Color.rgb(250, 205, 120)
        canvas.drawLine(0f, h * 0.50f, w, h * 0.50f, paint)

        // Marqueur centré.
        val cx = w / 2f
        val cy = h / 2f
        paint.color = Color.rgb(210, 70, 60)
        canvas.drawCircle(cx, cy - 18f, 28f, paint)
        paint.color = Color.rgb(210, 70, 60)
        canvas.drawLine(cx - 12f, cy - 4f, cx, cy + 34f, paint)
        canvas.drawLine(cx + 12f, cy - 4f, cx, cy + 34f, paint)
        paint.color = Color.WHITE
        canvas.drawCircle(cx, cy - 18f, 10f, paint)

        // Nom du contact.
        paint.color = Color.rgb(35, 45, 35)
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText(label, cx, cy + 70f, paint)
    }
}
