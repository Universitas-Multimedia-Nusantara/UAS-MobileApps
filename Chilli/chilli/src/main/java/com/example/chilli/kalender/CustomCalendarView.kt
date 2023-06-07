package com.example.chilli.kalender

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.CalendarView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.chilli.R
import java.util.*

class CustomCalendarView(context: Context, attrs: AttributeSet?) : CalendarView(context, attrs) {

    private val redColor = ContextCompat.getColor(context, R.color.teal_200)
    private val defaultDates = listOf("2023-06-01", "2023-06-10", "2023-12-20")

    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//        drawDefaultBackground(canvas)
    }

    private fun drawDefaultBackground(canvas: Canvas?) {
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is TextView) {
                val dateMillis = child.tag as Long
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = dateMillis
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                val dateString = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                if (defaultDates.contains(dateString)) {
                    child.setTextColor(redColor)
                }
            }
        }
    }
}
