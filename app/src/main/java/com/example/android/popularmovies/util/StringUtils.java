package com.example.android.popularmovies.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringUtils {

    public static String getFormattedDate(String oldDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date convertedDate = dateFormat.parse(oldDate);
            SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd yyyy", Locale.getDefault());
            if (convertedDate != null) {
                return newFormat.format(convertedDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return oldDate;
    }
}
