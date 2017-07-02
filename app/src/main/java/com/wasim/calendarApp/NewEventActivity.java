package com.wasim.calendarApp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wasim.calendarApp.models.Event;
import com.wasim.calendarApp.models.InvitedUser;
import com.wasim.calendarApp.models.User;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class NewEventActivity extends BaseActivity {

    private static final String TAG = "NewEventActivity";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]
    ArrayList<String> usersEmail = new ArrayList<String>();
    ArrayList<String> selectedUsersIds = new ArrayList<String>();
    private HashMap<String, String> dataresult;

    private Calendar calendar;
    private int year, month, day;
    private int hour, min;
    private boolean is24HourFormat = true;

    private EditText mDateField, mTimeField;
    private MultiAutoCompleteTextView mUsersField;
    private FloatingActionButton mSubmitButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        mDateField = (EditText) findViewById(R.id.field_date);
        mTimeField = (EditText) findViewById(R.id.field_time);
        mUsersField = (MultiAutoCompleteTextView) findViewById(R.id.field_users);
        mSubmitButton = (FloatingActionButton) findViewById(R.id.fab_submit_event);

        //Calender
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        setDate(year, month + 1, day);
        setTime(hour, min);

        getAllUserEmail();

        mDateField.setFocusable(false);
        mDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });

        mTimeField.setFocusable(false);
        mTimeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(v);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEvent();
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    public void setTime(View view) {
        showDialog(1);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        if (id == 1) {
            return new TimePickerDialog(this,
                    myTimeListener, hour, min, is24HourFormat);
        }
        return null;
    }

    private String getEmailFromId(String Id) {
        String result="";
        for (Map.Entry entry : dataresult.entrySet()) {
            if (Id.equals(entry.getValue())) {
                result = String.valueOf(entry.getKey());
                break; //breaking because its one to one map
            }
        }
        Log.e(TAG, result);
        return result;
    }

    private String getIdFromEmail(String email) {
        String result = dataresult.get(email);
        Log.e("NewEventActivity:id", email);
        return result;
    }

    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            setTime(hourOfDay, minute);
        }
    };

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    setDate(arg1, arg2 + 1, arg3);
                }
            };

    private void setDate(int year, int month, int day) {
        DecimalFormat mFormat = new DecimalFormat("00");
        mDateField.setText(new StringBuilder().append(mFormat.format(Double.valueOf(day))).append("/")
                .append(mFormat.format(Double.valueOf(month))).append("/").append(mFormat.format(Double.valueOf(year))));
    }

    private void setTime(int hour, int min) {
        DecimalFormat mFormat = new DecimalFormat("00");
        mTimeField.setText(new StringBuilder().append(mFormat.format(Double.valueOf(hour))).append(":")
                .append(mFormat.format(Double.valueOf(min))));
    }

    private Map<String, String> getAllUserEmail() {
        HashMap<String, String> result = new HashMap<>();
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                collectEmailId((Map<String, Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return result;
    }

    private void collectEmailId(Map<String, Object> users) {

        dataresult = new HashMap<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get email field and append to list
            if (!singleUser.get("email").toString().trim().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().trim())) {
                usersEmail.add(String.valueOf(singleUser.get("email")));
            }
            dataresult.put(String.valueOf(singleUser.get("email")), entry.getKey());
        }

        ArrayAdapter<String> emailIds = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, usersEmail);
        mUsersField.setAdapter(emailIds);
        mUsersField.setThreshold(3);
        mUsersField.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        Log.e("NewEventActivity: value", String.valueOf(dataresult));
    }

    private void submitEvent() {
        try {
        final String date;
        final String time;

            DateFormat formatter= new SimpleDateFormat("dd/MM/yyyy HH:mm");
            formatter.setTimeZone(TimeZone.getTimeZone("UST"));
            String startDateString = mDateField.getText().toString()+" "+mTimeField.getText().toString();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                Log.e("NewEventActivity",formatter.format(df.parse(startDateString)));

            String dateVal = formatter.format(df.parse(startDateString));
            String[] splited = dateVal.split("\\s+");

                date=splited[0];
                time=splited[1];

            Log.e("NewEventActivity",date);
            Log.e("NewEventActivity",time);
        // Date is required
        if (TextUtils.isEmpty(date)) {
            mDateField.setError(REQUIRED);
            return;
        }

        // Time is required
        if (TextUtils.isEmpty(time)) {
            mTimeField.setError(REQUIRED);
            return;
        }

        String str = mUsersField.getText().toString();
        List<String> emailIds = Arrays.asList(str.split(","));

        for (String email :
                emailIds) {
            email = email.toLowerCase().trim();
            if (usersEmail.contains(email)) {
                selectedUsersIds.add(getIdFromEmail(email));
            }
        }

        // Disable button so there are no multi-Events
        setEditingEnabled(false);
        Toast.makeText(this, "Creating Event...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewEventActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new Event
                            writeNewEvent(userId, user.username, date, time, selectedUsersIds);
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // [END single_value_read]
    }

    private void setEditingEnabled(boolean enabled) {
        mDateField.setEnabled(enabled);
        mTimeField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewEvent(String userId, String username, String title, String body, ArrayList<String> selectedUsersIds) {

        final String key = mDatabase.child("Events").push().getKey();
        final DatabaseReference InvitedUserReference = FirebaseDatabase.getInstance().getReference().child("Events").child(key).child("users");

        Event event = new Event(userId, username, title, body);
        Map<String, Object> eventValues = event.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Events/" + key, eventValues);
        childUpdates.put("/user-events/" + userId + "/" + key, eventValues);

        mDatabase.updateChildren(childUpdates);

        for (final String id :
                selectedUsersIds) {

            InvitedUserReference.child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user information
                            InvitedUser invitedUser = new InvitedUser(id, getEmailFromId(id), "pending");
                            InvitedUserReference.child(id).setValue(invitedUser);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }
    // [END write_fan_out]
}
