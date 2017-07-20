package com.wasim.calendarApp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wasim.calendarApp.models.Event;
import com.wasim.calendarApp.models.InvitedUser;
import com.wasim.calendarApp.utils.Constant;
import com.wasim.calendarApp.utils.DateUtils;
import com.wasim.calendarApp.utils.FontFaces;
import com.wasim.calendarApp.utils.NotificationFunctions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static android.R.attr.key;

public class EditEventActivity extends AppCompatActivity {

    private EditText mStartDateField, mEndDateField, field_title, mFromTimeField, mToTimeField, mDescriptionField, field_event_location;
    private TextView mSubmitButton;
    private CheckBox checkBox;
    private Spinner event_type_spinner, event_notification_spinner;
    private TextView activity_title_txtView, location_heading_txtView, title_txtView, app_name_txtView, description_heading_txtView, from_txtView, to_txtView, start_time_txtView, end_time_txtView, event_type_begin_txtView, event_type_end_txtView, event_notification_begin_txtView, event_notification_end_txtView;
    private List<String> event_notification;
    private List<String> event_type;
    private static String EVENT_KEY;
    private static String event_type_selected, event_notification_selected;
    private String TAG = "EditEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        EVENT_KEY = getIntent().getStringExtra("event_key");

        mStartDateField = (EditText) findViewById(R.id.field_from_date);
        mEndDateField = (EditText) findViewById(R.id.field_to_date);
        mFromTimeField = (EditText) findViewById(R.id.field_from_time);
        mToTimeField = (EditText) findViewById(R.id.field_to_time);
        mDescriptionField = (EditText) findViewById(R.id.field_event_description);
        field_event_location = (EditText) findViewById(R.id.field_event_location);
        field_title = (EditText) findViewById(R.id.field_title);
        activity_title_txtView = (TextView) findViewById(R.id.activity_title_txtView);
        app_name_txtView = (TextView) findViewById(R.id.app_name_txtView);
        from_txtView = (TextView) findViewById(R.id.from_txtView);
        to_txtView = (TextView) findViewById(R.id.to_txtView);
        start_time_txtView = (TextView) findViewById(R.id.start_time_txtView);
        end_time_txtView = (TextView) findViewById(R.id.end_time_txtView);
        title_txtView = (TextView) findViewById(R.id.title_txtView);
        description_heading_txtView = (TextView) findViewById(R.id.description_heading_txtView);
        event_type_begin_txtView = (TextView) findViewById(R.id.event_type_begin_txtView);
        event_type_end_txtView = (TextView) findViewById(R.id.event_type_end_txtView);
        location_heading_txtView = (TextView) findViewById(R.id.location_heading_txtView);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        mSubmitButton = (TextView) findViewById(R.id.fab_submit_event);
        event_notification_begin_txtView = (TextView) findViewById(R.id.event_notification_begin_txtView);
        event_notification_end_txtView = (TextView) findViewById(R.id.event_notification_end_txtView);
        event_type_spinner = (Spinner) findViewById(R.id.event_type_spinner);
        event_notification_spinner = (Spinner) findViewById(R.id.event_notification_spinner);


        activity_title_txtView.setTypeface(FontFaces.montserratBold(this));
        app_name_txtView.setTypeface(FontFaces.montserratRegular(this));
        from_txtView.setTypeface(FontFaces.montserratRegular(this));
        to_txtView.setTypeface(FontFaces.montserratRegular(this));
        start_time_txtView.setTypeface(FontFaces.montserratRegular(this));
        end_time_txtView.setTypeface(FontFaces.montserratRegular(this));
        description_heading_txtView.setTypeface(FontFaces.montserratRegular(this));
        event_type_begin_txtView.setTypeface(FontFaces.montserratRegular(this));
        event_notification_begin_txtView.setTypeface(FontFaces.montserratRegular(this));
        event_type_end_txtView.setTypeface(FontFaces.montserratRegular(this));
        event_notification_end_txtView.setTypeface(FontFaces.montserratRegular(this));
        field_title.setTypeface(FontFaces.montserratRegular(this));
        title_txtView.setTypeface(FontFaces.montserratRegular(this));
        mDescriptionField.setTypeface(FontFaces.montserratRegular(this));
        location_heading_txtView.setTypeface(FontFaces.montserratRegular(this));
        field_event_location.setTypeface(FontFaces.montserratRegular(this));
        checkBox.setTypeface(FontFaces.montserratRegular(this));
        mFromTimeField.setTypeface(FontFaces.montserratBold(this));
        mToTimeField.setTypeface(FontFaces.montserratBold(this));
        mStartDateField.setTypeface(FontFaces.montserratBold(this));
        mEndDateField.setTypeface(FontFaces.montserratBold(this));
        mSubmitButton.setTypeface(FontFaces.montserratBold(this));

        event_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                event_type_selected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                event_type_selected = "public";
            }
        });

        event_notification_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                event_notification_selected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                event_notification_selected = "10 min before";
            }
        });

        event_notification = new ArrayList<String>();
        event_notification.add("on");
        event_notification.add("10 min before");
        event_notification.add("1 hour before");
        event_notification.add("5 hour before");
        event_notification.add("1 day before");

        event_type = new ArrayList<String>();
        event_type.add("public");
        event_type.add("private");

        getEventDetail();

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEvent(mStartDateField.getText().toString(),mStartDateField.getText().toString(),mFromTimeField.getText().toString(),mToTimeField.getText().toString(),field_title.getText().toString(),mDescriptionField.getText().toString(),field_event_location.getText().toString(),event_type_selected);
            }
        });
    }

    private void getEventDetail() {
        FirebaseDatabase.getInstance().getReference().child("Events").child(EVENT_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                SharedPreferences shared = getSharedPreferences(Constant.PREFERENCES, MODE_PRIVATE);
                String timeZone = (shared.getString(Constant.timeZone, ""));

                String startDateVal = DateUtils.convertToTimeZone(timeZone, event.startDate + " " + event.fromTime);
                String endDateVal = DateUtils.convertToTimeZone(timeZone, event.endDate + " " + event.toTime);
                String[] splited1 = startDateVal.split("\\s+");
                String[] splited2 = endDateVal.split("\\s+");

                mStartDateField.setText(splited1[0]);
                mEndDateField.setText(splited2[0]);
                mFromTimeField.setText(splited1[1]);
                mToTimeField.setText(splited2[1]);
                field_title.setText(event.title);
                field_event_location.setText(event.location);
                mDescriptionField.setText(event.description);

                ArrayAdapter<String> dataAdapterNotificaiton = new ArrayAdapter<String>(EditEventActivity.this, android.R.layout.simple_spinner_item, event_notification);
                dataAdapterNotificaiton.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                event_notification_spinner.setAdapter(dataAdapterNotificaiton);

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EditEventActivity.this, android.R.layout.simple_spinner_item, event_type);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                event_type_spinner.setAdapter(dataAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateEvent(String startDate, String endDate, String fromTime, String toTime, String title, String description, String location, String type) {

        try {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            formatter.setTimeZone(TimeZone.getTimeZone("UST"));
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            String startDateString = startDate + " " + fromTime;
            String endDateString = endDate + " " + toTime;

            String startDateVal = formatter.format(df.parse(startDateString));
            String endDateVal = formatter.format(df.parse(endDateString));
            String[] splited1 = startDateVal.split("\\s+");
            String[] splited2 = endDateVal.split("\\s+");

            //UST Format
            startDate = splited1[0];
            endDate = splited2[0];
            fromTime = splited1[1];
            toTime = splited2[1];

            DatabaseReference EventReference = FirebaseDatabase.getInstance().getReference().child("Events").child(EVENT_KEY);
            EventReference.child("endDate").setValue(endDate);
            EventReference.child("fromTime").setValue(fromTime);
            EventReference.child("location").setValue(location);
            EventReference.child("startDate").setValue(startDate);
            EventReference.child("title").setValue(title);
            EventReference.child("toTime").setValue(toTime);
            EventReference.child("type").setValue(type);
            EventReference.child("description").setValue(description);

            DatabaseReference UserEventReference = FirebaseDatabase.getInstance().getReference().child("user-events").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(EVENT_KEY);
            UserEventReference.child("endDate").setValue(endDate);
            UserEventReference.child("fromTime").setValue(fromTime);
            UserEventReference.child("location").setValue(location);
            UserEventReference.child("startDate").setValue(startDate);
            UserEventReference.child("title").setValue(title);
            UserEventReference.child("toTime").setValue(toTime);
            UserEventReference.child("type").setValue(type);
            UserEventReference.child("description").setValue(description);

            Log.e(TAG, "key:" + key);
            NotificationFunctions notificationFunctions = new NotificationFunctions(EditEventActivity.this);
            notificationFunctions.setNotification(event_notification_selected, startDate, fromTime, title, EVENT_KEY);

        } catch (Exception e) {

        }

    }

//    private void removeAllInvitedUsers(final String eventId) {
//        FirebaseDatabase.getInstance().getReference().child("Events").child(eventId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot datasnapshot1 :
//                        dataSnapshot.getChildren()) {
//                    InvitedUser invitedUser = datasnapshot1.getValue(InvitedUser.class);
//                    FirebaseDatabase.getInstance().getReference().child("Invites").child(invitedUser.uid).child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        FirebaseDatabase.getInstance().getReference().child("Events").child(eventId).child("users").removeValue();
//    }

}
