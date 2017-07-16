package com.wasim.calendarApp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wasim.calendarApp.R;
import com.wasim.calendarApp.models.Event;
import com.wasim.calendarApp.utils.FontFaces;

import java.util.ArrayList;

/**
 * Created by DELL5547 on 15-Jul-17.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private ArrayList<Event> event_list;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView event_title, host_title, event_host, event_date, event_time;

        public MyViewHolder(View view) {
            super(view);
            event_title = (TextView) view.findViewById(R.id.event_title);
            host_title = (TextView) view.findViewById(R.id.host_title);
            event_host = (TextView) view.findViewById(R.id.event_host);
            event_date = (TextView) view.findViewById(R.id.event_date);
            event_time = (TextView) view.findViewById(R.id.event_time);
        }
    }


    public EventAdapter(Context context, ArrayList<Event> event_list) {
        this.event_list = event_list;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Event event = event_list.get(position);

        holder.event_title.setTypeface(FontFaces.montserratBold(context));
        holder.host_title.setTypeface(FontFaces.montserratRegular(context));
        holder.event_date.setTypeface(FontFaces.montserratRegular(context));
        holder.event_time.setTypeface(FontFaces.montserratRegular(context));
        holder.event_host.setTypeface(FontFaces.montserratBold(context));

        holder.event_title.setText(event.title);
        holder.event_host.setText(event.host);
        holder.event_date.setText(event.startDate);
        holder.event_time.setText(event.fromTime+" - "+event.toTime);
    }

    @Override
    public int getItemCount() {
        return event_list.size();
    }
}