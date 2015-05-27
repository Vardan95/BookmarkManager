package com.example.android.bookmarkmanager;

import java.text.SimpleDateFormat;

/**
 * Created by vpetrosyan on 22.05.2015.
 */
public  class TimeUtils {

    /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
    public static String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }
}
