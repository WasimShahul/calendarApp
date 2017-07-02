package com.wasim.calendarApp.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Event {

    public String uid;
    public String host;
    public String date;
    public String time;
    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String uid, String host, String date, String time) {
        this.uid = uid;
        this.host = host;
        this.date = date;
        this.time = time;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("host", host);
        result.put("date", date);
        result.put("time", time);
        return result;
    }
    // [END post_to_map]

}
// [END post_class]
