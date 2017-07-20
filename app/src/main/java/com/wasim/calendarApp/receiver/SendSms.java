package com.wasim.calendarApp.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.wasim.calendarApp.EventDetailActivity;
import com.wasim.calendarApp.R;

import java.util.ArrayList;

/**
 * Created by DELL5547 on 18-Jul-17.
 */

public class SendSms extends BroadcastReceiver {

    SmsManager smsManager = SmsManager.getDefault();
    private String TAG = "SendSms";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG, "gfh" + intent.getStringExtra("number") + " " + intent.getStringExtra("message"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ArrayList<String> parts = smsManager.divideMessage(intent.getStringExtra("message"));
            smsManager.sendMultipartTextMessage(intent.getStringExtra("number"), null, parts, null, null);
        } else {
            smsManager.sendTextMessage(intent.getStringExtra("number"), null, intent.getStringExtra("message"), null, null);
        }
    }


}