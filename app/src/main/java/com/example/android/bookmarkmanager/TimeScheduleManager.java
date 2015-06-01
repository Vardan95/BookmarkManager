package com.example.android.bookmarkmanager;

import android.text.format.Time;

/**
 * Created by vpetrosyan on 22.05.2015.
 * Special class which defines needed event time
 */
public class TimeScheduleManager {

    static long getDefaultScheduleTime(int priority)
    {
        Time dayTime = new Time();
        dayTime.setToNow();

        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        long dateTime;

        switch (priority) {
            case BookmarkPriority.HIGH_PRIOR:
            {
                dateTime = dayTime.setJulianDay(julianStartDay+DAY_COUNT_FOR_HIGH_PRIORITY);
                break;
            }
            case BookmarkPriority.NORM_PRIOR:
            {
                dateTime = dayTime.setJulianDay(julianStartDay+DAY_COUNT_FOR_MEDIUM_PRIORITY);
                break;
            }
            case BookmarkPriority.LOW_PRIOR:
            {
                dateTime = dayTime.setJulianDay(julianStartDay+DAY_COUNT_FOR_LOW_PRIORITY);
                break;
            }
            default:
            {
                dateTime = dayTime.setJulianDay(julianStartDay+DAY_COUNT_FOR_DEFAULT_PRIORITY);
                break;
            }
        }
        return dateTime;
    }

    private static final int DAY_COUNT_FOR_LOW_PRIORITY = 6;
    private static final int DAY_COUNT_FOR_MEDIUM_PRIORITY = 4;
    private static final int DAY_COUNT_FOR_HIGH_PRIORITY = 2;
    private static final int DAY_COUNT_FOR_DEFAULT_PRIORITY = 5;
}
