package com.wasim.calendarApp;

import android.*;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wasim.calendarApp.adapters.EventAdapter;
import com.wasim.calendarApp.models.Event;
import com.wasim.calendarApp.receiver.TimeAlarm;
import com.wasim.calendarApp.utils.Constant;
import com.wasim.calendarApp.utils.DateUtils;
import com.wasim.calendarApp.utils.FontFaces;
import com.wasim.calendarApp.utils.NotificationFunctions;
import com.wasim.calendarApp.viewholder.EventViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class MyEventsActivity extends AppCompatActivity {

    ImageView logout_imgView;
    TextView app_name_txtView, activity_changedate_txtView, menu_btn, activity_title_txtView, activity_now_event_txtView, event_title, event_date, event_time, activity_next_event_txtView;
    LinearLayout next_event_ll, now_event_ll, bottom_menu;
    RecyclerView today_event_list;
    EventAdapter eventAdapter;
    Calendar myCalendar = Calendar.getInstance();
    FloatingActionButton fab_button, fab_periodic_button, fab_create_event_button;

    ArrayList<Event> event_list = new ArrayList<>();
    ArrayList<String> event_ids = new ArrayList<>();
    SharedPreferences shared;
    String timeZone;

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_CONTACTS};
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private static String menuSelected = "";
    private String TAG = "MyEventsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        shared = getSharedPreferences(Constant.PREFERENCES, MODE_PRIVATE);
        timeZone = (shared.getString(Constant.timeZone, ""));

        logout_imgView = (ImageView) findViewById(R.id.menu_imgView);
        activity_title_txtView = (TextView) findViewById(R.id.activity_title_txtView);
        app_name_txtView = (TextView) findViewById(R.id.app_name_txtView);
        activity_now_event_txtView = (TextView) findViewById(R.id.activity_now_event_txtView);
        event_title = (TextView) findViewById(R.id.event_now_title);
        event_date = (TextView) findViewById(R.id.event_now_date);
        event_time = (TextView) findViewById(R.id.event_now_time);
        activity_changedate_txtView = (TextView) findViewById(R.id.activity_changedate_txtView);
        activity_next_event_txtView = (TextView) findViewById(R.id.activity_next_event_txtView);
        menu_btn = (TextView) findViewById(R.id.menu_btn);
        next_event_ll = (LinearLayout) findViewById(R.id.next_event_ll);
        now_event_ll = (LinearLayout) findViewById(R.id.now_event_ll);
        bottom_menu = (LinearLayout) findViewById(R.id.bottom_menu);
        today_event_list = (RecyclerView) findViewById(R.id.today_event_list);

        activity_title_txtView.setTypeface(FontFaces.montserratBold(this));
        app_name_txtView.setTypeface(FontFaces.montserratBold(this));
        activity_changedate_txtView.setTypeface(FontFaces.montserratRegular(this));
        activity_now_event_txtView.setTypeface(FontFaces.montserratRegular(this));
        event_title.setTypeface(FontFaces.montserratBold(this));
        event_date.setTypeface(FontFaces.montserratRegular(this));
        event_time.setTypeface(FontFaces.montserratRegular(this));
        activity_next_event_txtView.setTypeface(FontFaces.montserratRegular(this));
        menu_btn.setTypeface(FontFaces.montserratBold(this));

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        logout_imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MyEventsActivity.this, SignInActivity.class));
                finish();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            findViewById(R.id.fab_periodic_button).setVisibility(View.VISIBLE);
            findViewById(R.id.fab_periodic_video_button).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.fab_periodic_button).setVisibility(View.GONE);
            findViewById(R.id.fab_periodic_video_button).setVisibility(View.GONE);
        }
        // Button launches NewEventActivity
        findViewById(R.id.fab_create_event_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyEventsActivity.this, NewEventActivity.class));
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                finish();
            }
        });
        // Button launches NewEventActivity
        findViewById(R.id.fab_periodic_video_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuSelected = "TimelyVideoSMS";
                if (ActivityCompat.checkSelfPermission(MyEventsActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MyEventsActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MyEventsActivity.this, permissionsRequired[0])
                            || ActivityCompat.shouldShowRequestPermissionRationale(MyEventsActivity.this, permissionsRequired[1])) {
                        //Show Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyEventsActivity.this);
                        builder.setTitle("Need Multiple Permissions");
                        builder.setMessage("This feature needs READ_CONTACTS and SEND_SMS permissions.");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(MyEventsActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                        //Previously Permission Request was cancelled with 'Dont Ask Again',
                        // Redirect to Settings after showing Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyEventsActivity.this);
                        builder.setTitle("Need Multiple Permissions");
                        builder.setMessage("This feature needs READ_CONTACTS and SEND_SMS permissions.");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                sentToSettings = true;
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                Toast.makeText(getBaseContext(), "Go to Permissions to Grant  SMS and calendar permission", Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        //just request the permission
                        ActivityCompat.requestPermissions(MyEventsActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }

                    SharedPreferences.Editor editor = permissionStatus.edit();
                    editor.putBoolean(permissionsRequired[0], true);
                    editor.commit();
                } else {
                    //You already have the permission, just go ahead.
                    proceedAfterPermission();
                }
            }
        });
        // Button launches NewEventActivity
        findViewById(R.id.fab_periodic_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuSelected = "TimelySMS";
                if (ActivityCompat.checkSelfPermission(MyEventsActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MyEventsActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MyEventsActivity.this, permissionsRequired[0])
                            || ActivityCompat.shouldShowRequestPermissionRationale(MyEventsActivity.this, permissionsRequired[1])) {
                        //Show Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyEventsActivity.this);
                        builder.setTitle("Need Multiple Permissions");
                        builder.setMessage("This feature needs READ_CONTACTS and SEND_SMS permissions.");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(MyEventsActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                        //Previously Permission Request was cancelled with 'Dont Ask Again',
                        // Redirect to Settings after showing Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyEventsActivity.this);
                        builder.setTitle("Need Multiple Permissions");
                        builder.setMessage("This feature needs READ_CONTACTS and SEND_SMS permissions.");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                sentToSettings = true;
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                                Toast.makeText(getBaseContext(), "Go to Permissions to Grant  SMS and calendar permission", Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        //just request the permission
                        ActivityCompat.requestPermissions(MyEventsActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }

                    SharedPreferences.Editor editor = permissionStatus.edit();
                    editor.putBoolean(permissionsRequired[0], true);
                    editor.commit();
                } else {
                    //You already have the permission, just go ahead.
                    proceedAfterPermission();
                }
            }
        });

        bottom_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyEventsActivity.this, MenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                finish();
            }
        });

        activity_changedate_txtView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MyEventsActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy HH:MM";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

//        activity_title_txtView.setText(sdf.format(myCalendar.getTime()));
        setEvents(sdf.format(myCalendar.getTime()));
    }

    private void proceedAfterPermission() {
        if (menuSelected.equals("TimelySMS")) {
            startActivity(new Intent(MyEventsActivity.this, PeriodicSMSActivity.class));
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            finish();
        } else if (menuSelected.equals("TimelyVideoSMS")) {
            startActivity(new Intent(MyEventsActivity.this, PeriodicVideoSMSActivity.class));
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            finish();
        }

    }

    private void setEvents(final String dateTime) {
        //dateTime in Cirrent timezone format
//        final String USTDateTime = DateUtils.convertToUstFormat(dateTime);
        String[] splited = dateTime.split("\\s+");
        final String receivedDate = splited[0];
        String receivedTime = splited[1];

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        final String formattedDate = df.format(c.getTime());
        String[] splited2 = formattedDate.split("\\s+");
        final String currentDate = splited2[0];
        if (receivedDate.equals(currentDate)) {
            activity_title_txtView.setText("Today");
        } else {
            activity_title_txtView.setText(receivedDate);
        }


        Log.e(TAG, receivedDate);
        FirebaseDatabase.getInstance().getReference().child("user-events").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Clear array list
                event_list.clear();
                event_ids.clear();
                for (DataSnapshot datasnapshot1 :
                        dataSnapshot.getChildren()) {
                    Event event = datasnapshot1.getValue(Event.class);

                    String startDateTime = DateUtils.convertToTimeZone(timeZone, event.startDate + " " + event.fromTime);
                    String endDateTime = DateUtils.convertToTimeZone(timeZone, event.endDate + " " + event.toTime);
                    String[] splited = startDateTime.split("\\s+");
                    String[] splited1 = endDateTime.split("\\s+");
                    String sdate = splited[0];
                    String stime = splited[1];
                    String edate = splited1[0];
                    String etime = splited1[1];
                    Event event1 = new Event(event.uid, event.host, event.title, event.location, sdate, edate, stime, etime, event.type, event.description);
                    Log.e(TAG, dateTime + " : " + startDateTime + " " + endDateTime);
                    Log.e(TAG, String.valueOf(DateUtils.isDateInBetween(dateTime, startDateTime, endDateTime)));

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    final String formattedDate = df.format(c.getTime());

                    if (DateUtils.isDateInBetween(formattedDate, startDateTime, endDateTime)) {
                        now_event_ll.setVisibility(View.VISIBLE);
                        Log.e(TAG, "Inside" + event.title);
                        event_title.setText(event.title);
                        event_date.setText(sdate);
                        event_time.setText(stime + " - " + etime);
                    } else {
                        if (sdate.equals(receivedDate)) {
                            Log.e(TAG, event.title);
                            event_list.add(event1);
                            event_ids.add(datasnapshot1.getKey());
                        }
                    }
                }

                eventAdapter = new EventAdapter(MyEventsActivity.this, event_list, event_ids);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                today_event_list.setLayoutManager(mLayoutManager);
                today_event_list.setItemAnimator(new DefaultItemAnimator());
                today_event_list.setAdapter(eventAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        final String formattedDate = df.format(c.getTime());

        setEvents(formattedDate);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(MyEventsActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MyEventsActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MyEventsActivity.this, permissionsRequired[2])) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyEventsActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This feature needs READ_CONTACTS and SEND_SMS permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MyEventsActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(MyEventsActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(MyEventsActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

}
