package com.wasim.calendarApp.utils;


import android.util.Log;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    private static String TAG = "DateUtils";

    public static Date parseDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date parsedDate = null;
        try {
            parsedDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    public static String addDays(String date, int days){
        String dt = date;  // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, days);  // number of days to add
        dt = sdf.format(c.getTime());  // dt is now the new date

        return dt;
    }

    public static boolean isDateInBetween(String dateToCheck, String startDate, String endDate){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date1 = sdf.parse(dateToCheck);
            Date date2 = sdf.parse(startDate);
            Date date3 = sdf.parse(endDate);

            return (date1.after(date2) || date1.equals(date2)) && date1.before(date3);

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static long minutesInBetween(String startDateTime, String endDateTime){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = sdf.parse(startDateTime);
            d2 = sdf.parse(endDateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
            //in milliseconds
        long diff = 0;
        if (d2 != null) {
            diff = d2.getTime() - d1.getTime();
        }

        long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            Log.e(TAG, diffDays + " days, ");
            Log.e(TAG, diffHours + " hours, ");
            Log.e(TAG, diffMinutes + " minutes, ");
            Log.e(TAG, diffSeconds + " seconds.");
            Log.e(TAG, diffMinutes+(diffHours*60)+" Minutes totally");



        return diffMinutes+(diffHours*60);
    }

    public static long daysInBetween(String startDateTime, String endDateTime){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = sdf.parse(startDateTime);
            d2 = sdf.parse(endDateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //in milliseconds
        long diff = 0;
        if (d2 != null) {
            diff = d2.getTime() - d1.getTime();
        }

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        Log.e(TAG, diffDays + " days, ");
        Log.e(TAG, diffHours + " hours, ");
        Log.e(TAG, diffMinutes + " minutes, ");
        Log.e(TAG, diffSeconds + " seconds.");
        Log.e(TAG, diffMinutes+(diffHours*60)+" Minutes totally");



        return diffDays;
    }

    public static boolean compareDates(String d1,String d2) {
        try{
            // If you already have date objects then skip 1

            //1
            // Create 2 dates starts
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);

            System.out.println("Date1"+sdf.format(date1));
            System.out.println("Date2"+sdf.format(date2));
            System.out.println();

            // Create 2 dates ends
            //1

            // Date object is having 3 methods namely after,before and equals for comparing
            // after() will return true if and only if date1 is after date 2
            if(date1.after(date2)){
                return false;
            }
            // before() will return true if and only if date1 is before date2
            if(date1.before(date2)){
                return true;
            }

            //equals() returns true if both the dates are equal
            if(date1.equals(date2)){
                return true;
            }

        }
        catch(ParseException ex){
            ex.printStackTrace();
        }
        return false;
    }

    public static String convertToUstFormat(String dateTime){
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("UST"));
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateInUstFormat = null;
        try {
            dateInUstFormat = formatter.format(df.parse(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateInUstFormat;
    }

    public static String convertToTimeZone(String timeZone, String dateTime){
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("UST"));
        String dateFormat = null;
        try {
            dateFormat = formatter.format(df.parse(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateFormat;
    }

    public static Long addMinutesMillis(int minutes, Date date){
        Date dateVal = date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateVal);
        Log.e(TAG, "Time before "+String.valueOf(cal.getTimeInMillis()));
        cal.add(Calendar.MINUTE, minutes);
        Log.e(TAG, "Time after "+String.valueOf(cal.getTimeInMillis()));
        return cal.getTimeInMillis();
    }

    public static Long addHoursMillis(int hours, Date date){
        Date dateVal = date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateVal);
        cal.add(Calendar.HOUR, hours);
        return cal.getTimeInMillis();
    }

    public static Long addDaysMillis(int days, Date date){
        Date dateVal = date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateVal);
        cal.add(Calendar.DATE, days);
        return cal.getTimeInMillis();
    }

}
