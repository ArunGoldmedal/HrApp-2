package com.goldmedal.hrapp.ui.dashboard.attendance;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import com.goldmedal.hrapp.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

/**
 * Decorate several days with a dot
 */
public class EventDecorator implements DayViewDecorator {


    private HashSet<CalendarDay> dates;
//    private  Drawable backgroundDrawable;
    private final Drawable backgroundDrawable;

    public EventDecorator(Context context, Collection<CalendarDay> dates, int status) {

        this.dates = new HashSet<>(dates);

        if (status == 0) {
            backgroundDrawable = context.getResources().getDrawable(R.drawable.present_circle_background);
        } else if (status == 1) {
            backgroundDrawable = context.getResources().getDrawable(R.drawable.absent_circle_background);
        } else if (status == 2) {
            backgroundDrawable = context.getResources().getDrawable(R.drawable.halfday_circle_background);
        } else if (status == 3) {
            backgroundDrawable = context.getResources().getDrawable(R.drawable.punchmiss_circle_background);
        }else if (status == 4) {
            backgroundDrawable = context.getResources().getDrawable(R.drawable.holiday_circle_background);
        }

        else {
            backgroundDrawable = context.getResources().getDrawable(R.drawable.holiday_circle_background);
        }

    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(backgroundDrawable);
        view.addSpan(new ForegroundColorSpan(Color.WHITE));
    }
}


