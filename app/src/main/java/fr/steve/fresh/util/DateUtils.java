package fr.steve.fresh.util;

import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static Date toDate(DatePicker datePicker) {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        Calendar selectedDateTime = Calendar.getInstance();
        selectedDateTime.set(year, month, day);

        return selectedDateTime.getTime();
    }

    public static Date toDate(Date date, TimePicker timePicker) {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar selectedDateTime = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        selectedDateTime.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hour, minute);

        return selectedDateTime.getTime();
    }
}
