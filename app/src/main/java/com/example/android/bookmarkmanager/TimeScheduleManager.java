package com.example.android.bookmarkmanager;

import android.text.format.Time;

/**
 * Created by vpetrosyan on 22.05.2015.
 * Special class which defines needed event time
 */
public class TimeScheduleManager {

    static long getDefaultScheduleTime()
    {
        Time dayTime = new Time();
        dayTime.setToNow();

        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        long dateTime;
        // Cheating to convert this to UTC time, which is what we want anyhow
        dateTime = dayTime.setJulianDay(julianStartDay+DAY_COUNT);

        return dateTime;
    }

    private static final int DAY_COUNT = 4;
}
