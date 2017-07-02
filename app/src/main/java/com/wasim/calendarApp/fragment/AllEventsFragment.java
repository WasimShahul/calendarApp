package com.wasim.calendarApp.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class AllEventsFragment extends EventListFragment {

    public AllEventsFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // [START recent_posts_query]
        // Last 100 posts, these are automatically the 100 most recent
        // due to sorting by push() keys


        Query recentPostsQuery = databaseReference.child("Events")
                .limitToFirst(100);
        // [END recent_posts_query]

        return recentPostsQuery;
    }
}
