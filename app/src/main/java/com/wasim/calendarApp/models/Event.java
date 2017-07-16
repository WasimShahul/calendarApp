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
    public String startDate;
    public String endDate;
    public String fromTime;
    public String toTime;
    public String type;
    public String description;
    public String title;
    public String location;
    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String uid, String host, String title, String location,  String startDate, String endDate, String fromTime,String toTime,String type,String description) {
        this.uid = uid;
        this.host = host;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.type = type;
        this.description = description;
        this.title = title;
        this.location = location;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("host", host);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("fromTime", fromTime);
        result.put("toTime", toTime);
        result.put("type", type);
        result.put("description", description);
        result.put("title", title);
        result.put("location", location);
        return result;
    }

}
