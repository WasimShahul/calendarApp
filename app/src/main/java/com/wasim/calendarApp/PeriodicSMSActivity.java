package com.wasim.calendarApp;

import android.*;
import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wasim.calendarApp.utils.DateUtils;
import com.wasim.calendarApp.utils.FontFaces;
import com.wasim.calendarApp.utils.NotificationFunctions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class PeriodicSMSActivity extends AppCompatActivity {

    TextView app_name_txtView, activity_title_txtView, from_txtView, start_time_txtView, title_txtView, add_users_txtView, fab_submit_event;
    EditText field_from_date, field_from_time, field_title;
    MultiAutoCompleteTextView field_users;
    LinearLayout create_ll;
    HashMap<String, String> contactList;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private static final int SEND_SMS_PERMISSIONS_REQUEST = 1;
    private ArrayList<String> contactNames;
    private int year, month, day;
    private int hour, min;
    private static String TAG = "PeriodicSMSActivity";
    private ArrayList<String> selectedPhoneNos;
    NotificationFunctions notificationFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_periodic_sms);

        contactList = new HashMap<>();
        selectedPhoneNos = new ArrayList<>();

        notificationFunctions = new NotificationFunctions(this);

        app_name_txtView = (TextView) findViewById(R.id.app_name_txtView);
        activity_title_txtView = (TextView) findViewById(R.id.activity_title_txtView);
        from_txtView = (TextView) findViewById(R.id.from_txtView);
        start_time_txtView = (TextView) findViewById(R.id.start_time_txtView);
        title_txtView = (TextView) findViewById(R.id.title_txtView);
        add_users_txtView = (TextView) findViewById(R.id.add_users_txtView);
        fab_submit_event = (TextView) findViewById(R.id.fab_submit_event);
        field_from_date = (EditText) findViewById(R.id.field_from_date);
        field_from_time = (EditText) findViewById(R.id.field_from_time);
        field_title = (EditText) findViewById(R.id.field_title);
        field_users = (MultiAutoCompleteTextView) findViewById(R.id.field_users);
        create_ll = (LinearLayout) findViewById(R.id.create_ll);

        activity_title_txtView.setTypeface(FontFaces.montserratBold(this));
        fab_submit_event.setTypeface(FontFaces.montserratBold(this));
        app_name_txtView.setTypeface(FontFaces.montserratBold(this));
        from_txtView.setTypeface(FontFaces.montserratRegular(this));
        start_time_txtView.setTypeface(FontFaces.montserratRegular(this));
        add_users_txtView.setTypeface(FontFaces.montserratRegular(this));
        field_from_date.setTypeface(FontFaces.montserratRegular(this));
        field_from_time.setTypeface(FontFaces.montserratRegular(this));
        field_users.setTypeface(FontFaces.montserratRegular(this));
        field_title.setTypeface(FontFaces.montserratRegular(this));
        title_txtView.setTypeface(FontFaces.montserratRegular(this));

        //Calender
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);
        setDate(year, month + 1, day);
        setFromTime(hour, min);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getContacts();
        }

        create_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = field_users.getText().toString();
                str = str.substring(0, str.length() - 1);
                List<String> contactsSelected = Arrays.asList(str.split(","));

                for (String contacts :
                        contactsSelected) {
                    String[] splitted = contacts.split(" :: ");
                    if (splitted[1] != null) {
                        try {
                            Log.e(TAG, splitted[1]);
                            selectedPhoneNos.add(splitted[1].trim());
                        } catch (ArrayIndexOutOfBoundsException e) {

                        }

                    }

                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    submit();
                }
            }
        });

        field_from_time.setFocusable(false);
        field_from_date.setFocusable(false);

        field_from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });

        field_from_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFTime(v);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void submit() {
        for (String phoneNo :
                selectedPhoneNos) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                getPermissionToSMS();
            } else {
                Log.e(TAG, phoneNo);
                Log.e(TAG, field_from_date.getText().toString() + " " + field_from_time.getText().toString());
                notificationFunctions.setSmsSchedule(DateUtils.convertDateToMillis(field_from_date.getText().toString() + " " + field_from_time.getText().toString()), phoneNo, field_title.getText().toString());
                Toast.makeText(getBaseContext(), "Message scheduled..", Toast.LENGTH_LONG).show();
                startActivity(new Intent(PeriodicSMSActivity.this, MyEventsActivity.class));
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                finish();
            }

        }
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    public void setFTime(View view) {
        showDialog(1);
    }

    public void getAllContacts() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        contactNames = new ArrayList<String>();
        contactNames.clear();
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactList.put(name, phoneNumber);
            contactNames.add(name + " :: " + phoneNumber);
            Log.e(TAG, name + " " + phoneNumber);
        }


        ArrayAdapter<String> emailIds = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactNames);
        field_users.setAdapter(emailIds);
        field_users.setThreshold(3);
        field_users.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        phones.close();
    }

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


    private TimePickerDialog.OnTimeSetListener myFromTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            setFromTime(hourOfDay, minute);
        }
    };

    private void setFromTime(int hour, int min) {
        DecimalFormat mFormat = new DecimalFormat("00");
        field_from_time.setText(new StringBuilder().append(mFormat.format(Double.valueOf(hour))).append(":")
                .append(mFormat.format(Double.valueOf(min))));
    }

    private void setDate(int year, int month, int day) {
        DecimalFormat mFormat = new DecimalFormat("00");
        field_from_date.setText(new StringBuilder().append(mFormat.format(Double.valueOf(day))).append("/")
                .append(mFormat.format(Double.valueOf(month))).append("/").append(mFormat.format(Double.valueOf(year))));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PeriodicSMSActivity.this, MyEventsActivity.class));
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            getAllContacts();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.SEND_SMS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{android.Manifest.permission.SEND_SMS},
                    SEND_SMS_PERMISSIONS_REQUEST);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(
                    android.Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();
                getAllContacts();
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == SEND_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Send SMS permission granted", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    submit();
                }
            } else {
                Toast.makeText(this, "Send SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myStartDateListener, year, month, day);
        }
        boolean is24HourFormat = true;
        if (id == 1) {
            return new TimePickerDialog(this,
                    myFromTimeListener, hour, min, is24HourFormat);
        }
        return null;
    }
}
