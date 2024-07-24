package fr.steve.fresh.util;

import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for date and time manipulation.
 * <p>
 * This class provides static methods for converting {@link DatePicker} and {@link TimePicker} inputs into {@link Date} objects.
 * </p>
 */
public class DateUtils {

    /**
     * Converts the date selected in a {@link DatePicker} to a {@link Date} object.
     * <p>
     * The method retrieves the year, month, and day from the {@link DatePicker} and creates a {@link Date} object
     * representing the selected date. The time is set to the start of the day (00:00).
     * </p>
     *
     * @param datePicker the {@link DatePicker} containing the selected date
     * @return a {@link Date} object representing the date selected in the {@link DatePicker}
     */
    public static Date toDate(DatePicker datePicker) {
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int day = datePicker.getDayOfMonth();

        Calendar selectedDateTime = Calendar.getInstance();
        selectedDateTime.set(year, month, day);

        return selectedDateTime.getTime();
    }

    /**
     * Converts a {@link Date} object and a {@link TimePicker} to a {@link Date} object with the specified time.
     * <p>
     * This method updates the time of the provided {@link Date} object using the hour and minute from the {@link TimePicker}.
     * </p>
     *
     * @param date       the {@link Date} object to be updated with the time from the {@link TimePicker}
     * @param timePicker the {@link TimePicker} containing the hour and minute to be set in the {@link Date} object
     * @return a {@link Date} object representing the date and time specified by the provided {@link Date} and {@link TimePicker}
     */
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
