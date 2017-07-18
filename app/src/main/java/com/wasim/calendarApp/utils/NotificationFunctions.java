package com.wasim.calendarApp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.util.Log;

import com.wasim.calendarApp.MyEventsActivity;
import com.wasim.calendarApp.receiver.SendSms;
import com.wasim.calendarApp.receiver.SmsBroadcastReceiver;
import com.wasim.calendarApp.receiver.TimeAlarm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by DELL5547 on 16-Jul-17.
 */

public class NotificationFunctions {
    private Context context;
    private AlarmManager am;
    SharedPreferences shared;
    String timeZone;
    private String TAG = "NotificationFunctions";

    public NotificationFunctions(Context context) {
        this.context = context;
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        shared = context.getSharedPreferences(Constant.PREFERENCES, MODE_PRIVATE);
    }

    public void setNotification(String event_type, String event_start_date, String event_from_time, String title, String key){

        String myDate = event_start_date + " " + event_from_time;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        timeZone = (shared.getString(Constant.timeZone, ""));
        Date date = null;
        try {
            date = sdf.parse(DateUtils.convertToTimeZone(timeZone, myDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();

        Log.e(TAG, event_type);

        if(event_type.equals("on")){
            setOneTimeAlarm(millis, title, key);
        } else if(event_type.equals("10 min before")){
            Log.e(TAG, "date val"+date);
            setOneTimeAlarm(DateUtils.addMinutesMillis(-10, date), title, key);
        } else if(event_type.equals("1 hour before")){
            setOneTimeAlarm(DateUtils.addHoursMillis(-1, date), title, key);
        } else if(event_type.equals("1 day before")){
            setOneTimeAlarm(DateUtils.addDaysMillis(-1, date), title, key);
        } else if(event_type.equals("5 hour before")){
            setOneTimeAlarm(DateUtils.addHoursMillis(-5, date), title, key);
        }
    }

    public void setOneTimeAlarm(long millis, String title, String key) {
        Intent intent = new Intent(context, TimeAlarm.class);
        intent.putExtra("title", title);
        intent.putExtra("key", key);
        Random r = new Random();
        int id = r.nextInt(99 - 1 + 1) + 1;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_ONE_SHOT);
        am.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
    }

    public void setSmsSchedule(long millis, String number, String message) {
        Intent intent = new Intent(context, SendSms.class);
        Log.e(TAG, number+" : "+message+" : "+millis);
        intent.putExtra("number",number);
        intent.putExtra("message",message);
        Random r = new Random();
        int id = r.nextInt(99 - 1 + 1) + 1;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_ONE_SHOT);
        am.set(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
    }
}
