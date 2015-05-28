package com.example.android.bookmarkmanager;

import java.util.ArrayList;

/**
 * Created by vpetrosyan on 22.05.2015.
 */
public class BookmarkPriority {
    public static final int HIGH_PRIOR = 2;
    public static final int NORM_PRIOR = 1;
    public static final int LOW_PRIOR = 0;

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

    public static String getPriorityString(int priority)
    {
        switch (priority)
        {
            case HIGH_PRIOR:
            {
                return  HIGH_PRIOR_STRING;
            }
            case LOW_PRIOR:
            {
                return  LOW_PRIOR_STRING;
            }
            case NORM_PRIOR:
            {
                return NORMAL_PRIOR_STRING;
            }
        }

        return null;
    }
}
