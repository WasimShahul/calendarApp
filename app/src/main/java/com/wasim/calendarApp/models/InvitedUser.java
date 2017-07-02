package com.wasim.calendarApp.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START comment_class]
@IgnoreExtraProperties
public class InvitedUser {

    public String uid;
    public String email;
    public String status;

    public InvitedUser() {
    }

    public InvitedUser(String uid, String email, String status) {
        this.uid = uid;
        this.email = email;
        this.status = status;
    }

}
// [END comment_class]
