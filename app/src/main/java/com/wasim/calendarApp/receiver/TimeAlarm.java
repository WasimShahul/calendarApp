package com.wasim.calendarApp.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;

import com.wasim.calendarApp.EventDetailActivity;
import com.wasim.calendarApp.MainActivity;
import com.wasim.calendarApp.R;

/**
 * Created by DELL5547 on 16-Jul-17.
 */

public class TimeAlarm extends BroadcastReceiver {

    NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.appicon)
                        .setContentTitle(intent.getStringExtra("title"));

        Intent notificationIntent = new Intent(context, EventDetailActivity.class);
        intent.putExtra(EventDetailActivity.EXTRA_EVENT_KEY, intent.getStringExtra("key"));

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        builder.setSound(Uri.parse("android.resource://"
                + context.getPackageName() + "/" + R.raw.demonstrative));
        builder.setLights(Color.BLUE, 500, 500);
        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};
        builder.setVibrate(pattern);
        builder.setStyle(new NotificationCompat.InboxStyle());
// Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());

//        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        CharSequence from = "Nithin";
//        CharSequence message = "Crazy About Android...";
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
//        Notification notif = new Notification(R.drawable.appicon, "Crazy About Android...", System.currentTimeMillis());
//        notif.setLatestEventInfo(context, from, message, contentIntent);
//        nm.notify(1, notif);


    }


}