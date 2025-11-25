package com.example.helloworldproject.util;

import android.util.Pair;
import java.util.Calendar;

public class DateUtils {
    public static Pair<Long, Long> getDayRange(long selectedDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(selectedDate);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startOfDay = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long endOfDay = cal.getTimeInMillis();

        return new Pair<>(startOfDay, endOfDay);
    }
}
