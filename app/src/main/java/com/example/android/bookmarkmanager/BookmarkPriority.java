package com.example.android.bookmarkmanager;

import java.util.ArrayList;

/**
 * Created by vpetrosyan on 22.05.2015.
 */
public class BookmarkPriority {
    public static final byte HIGH_PRIOR = 2;
    public static final byte NORM_PRIOR = 1;
    public static final byte LOW_PRIOR = 0;

    public static final String HIGH_PRIOR_STRING = "High";
    public static final String NORMAL_PRIOR_STRING = "Normal";
    public static final String LOW_PRIOR_STRING = "Low";

    public static ArrayList<String> getPriorityStrings()
    {
        ArrayList<String> priorStrings = new ArrayList<>();
        priorStrings.add(HIGH_PRIOR_STRING);
        priorStrings.add(NORMAL_PRIOR_STRING);
        priorStrings.add(LOW_PRIOR_STRING);
        return priorStrings;
    }
}
