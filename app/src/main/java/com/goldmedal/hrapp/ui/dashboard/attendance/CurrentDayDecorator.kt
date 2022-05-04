package com.goldmedal.hrapp.ui.dashboard.attendance

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.goldmedal.hrapp.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class CurrentDayDecorator(context: Activity?, currentDay: CalendarDay,status: String) : DayViewDecorator {
    private val drawable: Drawable?
    var myDay = currentDay
    var status = 0
    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == myDay
    }


    override fun decorate(view: DayViewFacade) {
        view.setBackgroundDrawable(drawable!!)
        view.addSpan(ForegroundColorSpan(Color.WHITE))
    }


    init {
        // You can set background for Decorator via drawable here
        when {
            status.equals(AttendanceFragment.TAG_PRESENT,ignoreCase = true) -> {
                drawable = ContextCompat.getDrawable(context!!,R.drawable.present_circle_background)
            }
            status.equals(AttendanceFragment.TAG_ABSENT,ignoreCase = true) -> {
                drawable = ContextCompat.getDrawable(context!!,R.drawable.absent_circle_background)
            }
            status.equals(AttendanceFragment.TAG_HALF_DAY,ignoreCase = true) -> {
                drawable = ContextCompat.getDrawable(context!!,R.drawable.halfday_circle_background)
            }
            status.equals(AttendanceFragment.TAG_CHECKOUT_MISSING,ignoreCase = true) -> {
                drawable = ContextCompat.getDrawable(context!!,R.drawable.punchmiss_circle_background)
            }
            status.equals(AttendanceFragment.TAG_HOLIDAY,ignoreCase = true) -> {
                drawable = ContextCompat.getDrawable(context!!,R.drawable.holiday_circle_background)
            }
            else -> {
                drawable = ContextCompat.getDrawable(context!!,R.drawable.holiday_circle_background)
            }
        }
    }
}