package com.wasim.calendarApp.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyEventsFragment extends EventListFragment {

    public MyEventsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // User Events
        return databaseReference.child("user-events")
                .child(getUid());
    }
}
