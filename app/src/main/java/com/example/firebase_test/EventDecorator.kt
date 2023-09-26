package com.example.firebase_test

import android.graphics.Color.RED
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import io.grpc.Context

class EventDecorator(private val color: Int, dates: Collection<CalendarDay>?) : DayViewDecorator {
    private val dates: HashSet<CalendarDay>


    init {
        this.dates = HashSet(dates)
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {

        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(10f, color))
    }
}