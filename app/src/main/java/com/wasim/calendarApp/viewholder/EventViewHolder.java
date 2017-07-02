package com.wasim.calendarApp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.wasim.calendarApp.R;
import com.wasim.calendarApp.models.Event;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class EventViewHolder extends RecyclerView.ViewHolder {

    public TextView dateTxtView;
    public TextView authorView;
    public TextView timeTxtView;

    public EventViewHolder(View itemView) {
        super(itemView);

        dateTxtView = (TextView) itemView.findViewById(R.id.event_date);
        authorView = (TextView) itemView.findViewById(R.id.event_host);
        timeTxtView = (TextView) itemView.findViewById(R.id.event_time);

    }

    public void bindToEvent(String timeZone, Event event, View.OnClickListener starClickListener) {
        try {

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
            String startDateString = event.date + " " + event.time;
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            df.setTimeZone(TimeZone.getTimeZone("UST"));
            String dateVal = formatter.format(df.parse(startDateString));
            String[] splited = dateVal.split("\\s+");
            String date = splited[0];
            String time = splited[1];

            dateTxtView.setText("On: "+date);
            authorView.setText(event.host);
            timeTxtView.setText(time+" Hrs");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


}
