package com.wasim.calendarApp.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String timeZone;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String timeZone) {
        this.username = username;
        this.email = email;
        this.timeZone = timeZone;
    }

}
// [END blog_user_class]
