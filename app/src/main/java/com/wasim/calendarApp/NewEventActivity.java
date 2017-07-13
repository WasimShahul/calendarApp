package com.wasim.calendarApp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.wasim.calendarApp.utils.Constant;
import com.wasim.calendarApp.utils.DateUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class NewEventActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "NewEventActivity";
    private static final String REQUIRED = "Required";
    private static final String Invalid = "Time is less than from time";

    private static String uname, sdate, edate, ftime, ttime;
    private DatabaseReference mDatabase;
    final Context context = this;
    public static ArrayList<String> allCollidingEvents = new ArrayList<String>();
    ArrayList<String> usersEmail = new ArrayList<String>();
    ArrayList<String> selectedUsersIds = new ArrayList<String>();
    private HashMap<String, String> dataresult;

    private int year, month, day;
    private int hour, min;

    private EditText mStartDateField, mEndDateField, mFromTimeField, mToTimeField, mDescriptionField;
    private MultiAutoCompleteTextView mUsersField;
    private FloatingActionButton mSubmitButton;
    private static String event_type_selected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mStartDateField = (EditText) findViewById(R.id.field_from_date);
        mEndDateField = (EditText) findViewById(R.id.field_to_date);
        mFromTimeField = (EditText) findViewById(R.id.field_from_time);
        mToTimeField = (EditText) findViewById(R.id.field_to_time);
        mDescriptionField = (EditText) findViewById(R.id.field_event_description);
        mUsersField = (MultiAutoCompleteTextView) findViewById(R.id.field_users);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        Spinner event_type_spinner = (Spinner) findViewById(R.id.event_type_spinner);
        event_type_spinner.setOnItemSelectedListener(this);
        mSubmitButton = (FloatingActionButton) findViewById(R.id.fab_submit_event);

        List<String> event_type = new ArrayList<String>();
        event_type.add("public");
        event_type.add("private");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, event_type);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        event_type_spinner.setAdapter(dataAdapter);

        //Calender
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        setDate(year, month + 1, day);
        setEnddDate(year, month + 1, day);
        setFromTime(hour, min);
        setToTime(hour, min);

        getAllUserEmail();

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFromTimeField.setText("00:00");
                    mToTimeField.setText("23:59");
                    mFromTimeField.setEnabled(false);
                    mToTimeField.setEnabled(false);
                } else {
                    mFromTimeField.setEnabled(true);
                    mToTimeField.setEnabled(true);
                }
            }
        });

        mStartDateField.setFocusable(false);
        mStartDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });
        mEndDateField.setFocusable(false);
        mEndDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEndDate(v);
            }
        });

        mFromTimeField.setFocusable(false);
        mFromTimeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFTime(v);
            }
        });
        mToTimeField.setFocusable(false);
        mToTimeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTTime(v);
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
    public void setEndDate(View view) {
        showDialog(1000);
    }

    @SuppressWarnings("deprecation")
    public void setFTime(View view) {
        showDialog(1);
    }

    @SuppressWarnings("deprecation")
    public void setTTime(View view) {
        showDialog(2);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myStartDateListener, year, month, day);
        }
        if (id == 1000) {
            return new DatePickerDialog(this,
                    myEndDateListener, year, month, day);
        }
        boolean is24HourFormat = true;
        if (id == 1) {
            return new TimePickerDialog(this,
                    myFromTimeListener, hour, min, is24HourFormat);
        }
        if (id == 2) {
            return new TimePickerDialog(this,
                    myToTimeListener, hour, min, is24HourFormat);
        }
        return null;
    }

    private String getEmailFromId(String Id) {
        String result = "";
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

    private TimePickerDialog.OnTimeSetListener myFromTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            setFromTime(hourOfDay, minute);
        }
    };
    private TimePickerDialog.OnTimeSetListener myToTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            DecimalFormat mFormat = new DecimalFormat("00");
            mToTimeField.setText(new StringBuilder().append(mFormat.format(Double.valueOf(hourOfDay))).append(":")
                    .append(mFormat.format(Double.valueOf(minute))));
        }
    };

    private DatePickerDialog.OnDateSetListener myStartDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    setDate(arg1, arg2 + 1, arg3);
                }
            };

    private DatePickerDialog.OnDateSetListener myEndDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    DecimalFormat mFormat = new DecimalFormat("00");
                    mEndDateField.setText(new StringBuilder().append(mFormat.format(Double.valueOf(arg3))).append("/")
                            .append(mFormat.format(Double.valueOf(arg2 + 1))).append("/").append(mFormat.format(Double.valueOf(arg1))));
                }
            };

    private void setDate(int year, int month, int day) {
        DecimalFormat mFormat = new DecimalFormat("00");
        mStartDateField.setText(new StringBuilder().append(mFormat.format(Double.valueOf(day))).append("/")
                .append(mFormat.format(Double.valueOf(month))).append("/").append(mFormat.format(Double.valueOf(year))));
    }

    private void setEnddDate(int year, int month, int day) {
        DecimalFormat mFormat = new DecimalFormat("00");
        mEndDateField.setText(new StringBuilder().append(mFormat.format(Double.valueOf(day))).append("/")
                .append(mFormat.format(Double.valueOf(month))).append("/").append(mFormat.format(Double.valueOf(year))));
    }

    private void setFromTime(int hour, int min) {
        DecimalFormat mFormat = new DecimalFormat("00");
        mFromTimeField.setText(new StringBuilder().append(mFormat.format(Double.valueOf(hour))).append(":")
                .append(mFormat.format(Double.valueOf(min))));
    }

    private void setToTime(int hour, int min) {
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour

        Log.e(TAG, cal.getTime() + "");
        DecimalFormat mFormat = new DecimalFormat("00");
        mToTimeField.setText(new StringBuilder().append(mFormat.format(Double.valueOf(cal.getTime().getHours()))).append(":")
                .append(mFormat.format(Double.valueOf(cal.getTime().getMinutes()))));
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
            final String startDate;
            final String endDate;
            final String fromTime;
            final String toTime;

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            formatter.setTimeZone(TimeZone.getTimeZone("UST"));
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            String startDateString = mStartDateField.getText().toString() + " " + mFromTimeField.getText().toString();
            String endDateString = mEndDateField.getText().toString() + " " + mToTimeField.getText().toString();


            String startDateVal = formatter.format(df.parse(startDateString));
            String endDateVal = formatter.format(df.parse(endDateString));
            String[] splited1 = startDateVal.split("\\s+");
            String[] splited2 = endDateVal.split("\\s+");

            //Default Timezone Format
            startDate = mStartDateField.getText().toString();
            endDate = mEndDateField.getText().toString();
            fromTime = mFromTimeField.getText().toString();
            toTime = mToTimeField.getText().toString();

            if (DateUtils.compareDates(startDateString, endDateString)) {

            } else {
                Toast.makeText(getBaseContext(), "End Date is improper", Toast.LENGTH_LONG).show();
                return;
            }

            // Date is required
            if (TextUtils.isEmpty(startDate)) {
                mStartDateField.setError(REQUIRED);
                return;
            }
            if (TextUtils.isEmpty(endDate)) {
                mEndDateField.setError(REQUIRED);
                return;
            }
            // Time is required
            if (TextUtils.isEmpty(fromTime)) {
                mFromTimeField.setError(REQUIRED);
                return;
            }
            if (TextUtils.isEmpty(toTime)) {
                mToTimeField.setError(REQUIRED);
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
                                Toast.makeText(NewEventActivity.this,
                                        "Error: could not fetch user.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                checkIfAnyEventExists(startDate, fromTime, endDate, toTime);
                                uname = user.username;

                            }
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
        mStartDateField.setEnabled(enabled);
        mFromTimeField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewEvent(String userId, String username, String startDate, String endDate, String fromTime, String toTime, String type, String description, ArrayList<String> selectedUsersIds) {

        try{
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

            final String key = mDatabase.child("Events").push().getKey();
            final DatabaseReference InvitedUserReference = FirebaseDatabase.getInstance().getReference().child("Events").child(key).child("users");

            Event event = new Event(userId, username, startDate, endDate, fromTime, toTime, type, description);
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
        } catch (Exception e){

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        event_type_selected = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        event_type_selected = "public";
    }
    // [END write_fan_out]

    public void checkIfAnyEventExists(final String startDate, final String fromTime, final String endDate, final String toTime) {

        //Default Time Zone Format
        sdate = startDate;
        edate = endDate;
        ftime = fromTime;
        ttime = toTime;
        FirebaseDatabase.getInstance().getReference().child("user-events").child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long daysInBetween = DateUtils.daysInBetween(sdate + " " + ftime, edate + " " + ttime);
                for (long i = 0; i <= daysInBetween ; i++) {
                    sdate = DateUtils.addDays(startDate, (int) i);
                if (dataSnapshot.getValue() == null) {
                    Log.e(TAG, "null");
                    writeNewEvent(getUid(), uname, sdate, sdate, ftime, ttime, event_type_selected, mDescriptionField.getText().toString(), selectedUsersIds);
                    setEditingEnabled(true);
                    finish();
                } else {
                    Log.e(TAG, "not null");
                    detectCollidingEvents((Map<String, Object>) dataSnapshot.getValue(), sdate + " " + ftime, sdate + " " + ttime);
                }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void detectCollidingEvents(Map<String, Object> users, String startDateTime, String endDateTime) {
        Log.e(TAG, "Entering detectCollidingEvents...");
        Log.e(TAG, startDateTime + " " + endDateTime);
        allCollidingEvents.clear();
        SharedPreferences shared = getSharedPreferences(Constant.PREFERENCES, MODE_PRIVATE);
        String timeZone = (shared.getString(Constant.timeZone, ""));
        Long newMinutesInBetween = DateUtils.minutesInBetween(startDateTime, endDateTime);
        //iterate through each node
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            try{

            //Get map
            Map singleUser = (Map) entry.getValue();
            Log.e(TAG, "Before Conversion...");
            Log.e(TAG, "sdat: "+ singleUser.get("startDate") + " " + singleUser.get("fromTime"));
            Log.e(TAG, "edat: "+ singleUser.get("endDate") + " " + singleUser.get("toTime"));

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
            String startDateString = singleUser.get("startDate") + " " + singleUser.get("fromTime");
            String endDateString = singleUser.get("endDate") + " " + singleUser.get("toTime");
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            df.setTimeZone(TimeZone.getTimeZone("UST"));
            String sdat = formatter.format(df.parse(startDateString));
            String edat = formatter.format(df.parse(endDateString));
                Long existingMinutesInBetween = DateUtils.minutesInBetween(sdat, edat);
                Long availableMinutes = DateUtils.minutesInBetween(startDateTime, sdat);
                Log.e(TAG, newMinutesInBetween + " newMinutesInBetween");
                Log.e(TAG, existingMinutesInBetween + " existingMinutesInBetween");
                Log.e(TAG, availableMinutes + " available minutes");
                if (availableMinutes > 0) {
                    if (availableMinutes <= newMinutesInBetween) {
                        Log.e(TAG, "Collision occurs on" + entry.getKey());
                        allCollidingEvents.add(entry.getKey());
                    } else {
                        Log.e(TAG, "Collision does not occur");
                    }
                } else if (availableMinutes <= 0) {
                    if (DateUtils.parseDate(edat).after(DateUtils.parseDate(startDateTime))) {
                        Log.e(TAG, " colloision occurs" + entry.getKey());
                        allCollidingEvents.add(entry.getKey());
                    } else if (DateUtils.parseDate(edat).equals(DateUtils.parseDate(startDateTime))) {
                        Log.e(TAG, " colloision occurs" + entry.getKey());
                        allCollidingEvents.add(entry.getKey());
                    } else if (DateUtils.parseDate(edat).before(DateUtils.parseDate(startDateTime))) {
                        Log.e(TAG, "No Collision");
                    }
                }

            } catch (Exception e){

            }
        }
        if (allCollidingEvents.isEmpty()) {
            Toast.makeText(this, "Creating Event...", Toast.LENGTH_SHORT).show();
            writeNewEvent(getUid(), uname, sdate, sdate, ftime, ttime, event_type_selected, mDescriptionField.getText().toString(), selectedUsersIds);
            // Finish this Activity, back to the stream
            setEditingEnabled(true);
            finish();
        } else {
            setEditingEnabled(true);
            showAlert("You already have " + allCollidingEvents.size() + " event(s) on " + sdate + " during this period.");
//            Toast.makeText(getBaseContext(), "You already have " + allCollidingEvents.size() + " event(s) during this period.", Toast.LENGTH_LONG).show();
        }
    }

    public void showAlert(String Message) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.alert_dialog);
        dialog.setTitle("Event Alert");

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText(Message);

        Button dialogButtonOK = (Button) dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogButtonCANCEL);
        // if button is clicked, close the custom dialog
        dialogButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNewEvent(getUid(), uname, sdate, sdate, ftime, ttime, event_type_selected, mDescriptionField.getText().toString(), selectedUsersIds);
                // Finish this Activity, back to the stream
                setEditingEnabled(true);
                finish();
                dialog.dismiss();
            }
        });
        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}
