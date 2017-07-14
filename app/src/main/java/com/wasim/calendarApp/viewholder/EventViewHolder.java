package com.wasim.calendarApp.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.wasim.calendarApp.R;
import com.wasim.calendarApp.models.Event;
import com.wasim.calendarApp.utils.FontFaces;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class EventViewHolder extends RecyclerView.ViewHolder {

    public TextView dateTxtView;
    public TextView authorView;
    public TextView timeTxtView;
    public TextView hostTxtView;
    public TextView titleTxtView;

    public EventViewHolder(View itemView) {
        super(itemView);

        dateTxtView = (TextView) itemView.findViewById(R.id.event_date);
        authorView = (TextView) itemView.findViewById(R.id.event_host);
        timeTxtView = (TextView) itemView.findViewById(R.id.event_time);
        hostTxtView = (TextView) itemView.findViewById(R.id.host_title);
        titleTxtView = (TextView) itemView.findViewById(R.id.event_title);

    }

    public void bindToEvent(Context context, String timeZone, Event event, View.OnClickListener starClickListener) {
        try {

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
            String startDateString = event.startDate + " " + event.fromTime;
            String endDateString = event.endDate + " " + event.toTime;
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            df.setTimeZone(TimeZone.getTimeZone("UST"));
            String dateVal = formatter.format(df.parse(startDateString));
            String endDateVal = formatter.format(df.parse(endDateString));
            String[] splited = dateVal.split("\\s+");
            String[] splited1 = endDateVal.split("\\s+");
            String sdate = splited[0];
            String stime = splited[1];
            String edate = splited1[0];
            String etime = splited1[1];

            dateTxtView.setTypeface(FontFaces.montserratRegular(context));
            authorView.setTypeface(FontFaces.montserratBold(context));
            timeTxtView.setTypeface(FontFaces.montserratRegular(context));
            dateTxtView.setTypeface(FontFaces.montserratRegular(context));
            titleTxtView.setTypeface(FontFaces.montserratBold(context));

            timeTxtView.setText(stime+ " - "+etime+ " Hrs");
            authorView.setText(event.host);
            dateTxtView.setText(sdate);
            titleTxtView.setText(event.title);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
