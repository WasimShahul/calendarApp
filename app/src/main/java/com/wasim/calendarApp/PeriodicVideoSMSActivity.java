package com.wasim.calendarApp;

import android.*;
import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wasim.calendarApp.models.Event;
import com.wasim.calendarApp.utils.DateUtils;
import com.wasim.calendarApp.utils.FontFaces;
import com.wasim.calendarApp.utils.NotificationFunctions;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class PeriodicVideoSMSActivity extends AppCompatActivity {

    TextView app_name_txtView, title_audiotxtView, activity_title_txtView, from_txtView, start_time_txtView, title_txtView, add_users_txtView, fab_submit_event;
    EditText field_from_date, field_audio_title, field_from_time, field_title;
    MultiAutoCompleteTextView field_users;
    LinearLayout create_ll;
    HashMap<String, String> contactList;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private static final int SEND_SMS_PERMISSIONS_REQUEST = 1;
    private ArrayList<String> contactNames;
    private int year, month, day;
    private int hour, min;
    private static String TAG = "PeriodicVideoSMSActivity";
    private ArrayList<String> selectedPhoneNos;
    NotificationFunctions notificationFunctions;
    private static final int REQUEST_RECORD_AUDIO = 0;
    private static String AUDIO_FILE_PATH =
            Environment.getExternalStorageDirectory().getPath();
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private TextView txtPermissions;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private StorageReference mStorageRef;
    Random r;
    private static String recordedAudioUrl = "";
    private static boolean isAudioRecorded = false;
    private static String filePathName = "";
    private final static int CAMERA_RQ = 6969;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_periodic_video_sms);

        r = new Random();
        int i1 = r.nextInt(80 - 65) + 65;
        filePathName = i1 + "recorded_audio.wav";
        AUDIO_FILE_PATH = AUDIO_FILE_PATH + "/" + filePathName;
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

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
        title_audiotxtView = (TextView) findViewById(R.id.title_audiotxtView);
        field_from_date = (EditText) findViewById(R.id.field_from_date);
        field_from_time = (EditText) findViewById(R.id.field_from_time);
        field_audio_title = (EditText) findViewById(R.id.field_audio_title);
        field_title = (EditText) findViewById(R.id.field_title);
        field_users = (MultiAutoCompleteTextView) findViewById(R.id.field_users);
        create_ll = (LinearLayout) findViewById(R.id.create_ll);

        activity_title_txtView.setTypeface(FontFaces.montserratBold(this));
        fab_submit_event.setTypeface(FontFaces.montserratBold(this));
        app_name_txtView.setTypeface(FontFaces.montserratBold(this));
        from_txtView.setTypeface(FontFaces.montserratRegular(this));
        start_time_txtView.setTypeface(FontFaces.montserratRegular(this));
        title_audiotxtView.setTypeface(FontFaces.montserratRegular(this));
        add_users_txtView.setTypeface(FontFaces.montserratRegular(this));
        field_from_date.setTypeface(FontFaces.montserratRegular(this));
        field_from_time.setTypeface(FontFaces.montserratRegular(this));
        field_audio_title.setTypeface(FontFaces.montserratRegular(this));
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
                if (str.isEmpty()) {
                    return;
                } else {
                    str = str.substring(0, str.length() - 1);
                }
                List<String> contactsSelected = Arrays.asList(str.split(","));

                for (String contacts :
                        contactsSelected) {
                    String[] splitted = contacts.split(" :: ");
                    if (splitted[1] != null) {
                        try {
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
        field_audio_title.setFocusable(false);

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

        field_audio_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestAudioRecordPermission();

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void submit() {
        create_ll.setFocusable(false);
        for (String phoneNo :
                selectedPhoneNos) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                getPermissionToSMS();
            } else {
                String message = "Sent from calendarApp";
                if (recordedAudioUrl.equals("")) {
                    message = field_title.getText().toString();
                } else {
                    message = field_title.getText().toString() + "\n" + "View this video message\n" + recordedAudioUrl + "\n\n-" + FirebaseAuth.getInstance().getCurrentUser().getEmail();
                }
                if (field_audio_title.getText().toString().equals("Uploading...Please Wait...")) {
                    Toast.makeText(getBaseContext(), "Video record uploading please wait...", Toast.LENGTH_LONG).show();
                } else {
                    if (message.equals("")) {
                        message = "Sent from calendarApp\n-" + FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    }
                    if (field_users.getText().toString().equals("")) {
                        Toast.makeText(getBaseContext(), "Please add users...", Toast.LENGTH_LONG).show();
                    } else {
                        writeNewEvent(FirebaseAuth.getInstance().getCurrentUser().getUid(), FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Scheduled Message", "", field_from_date.getText().toString(), field_from_date.getText().toString(), field_from_time.getText().toString(), field_from_time.getText().toString(), "private", message + "\n\nSent to:\n" + field_users.getText().toString() + "\n\n", null);
                        notificationFunctions.setSmsSchedule(DateUtils.convertDateToMillis(field_from_date.getText().toString() + " " + field_from_time.getText().toString()), phoneNo, message);
                        Toast.makeText(getBaseContext(), "Message scheduled..", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(PeriodicVideoSMSActivity.this, MyEventsActivity.class));
                        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                        finish();
                    }

                }

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

    public void recordAudio() {
        File saveFolder = new File(Environment.getExternalStorageDirectory().getPath(), "CalendarApp");
//                if (!saveFolder.mkdirs())
//                    throw new RuntimeException("Unable to create save directory, make sure WRITE_EXTERNAL_STORAGE permission is granted.");

        new MaterialCamera(PeriodicVideoSMSActivity.this)                               // Constructor takes an Activity
                .allowRetry(true)                                  // Whether or not 'Retry' is visible during playback
                .autoSubmit(false)                                 // Whether or not user is allowed to playback videos after recording. This can affect other things, discussed in the next section.
                .saveDir(saveFolder)                               // The folder recorded videos are saved to
                .primaryColorAttr(R.attr.colorPrimary)             // The theme color used for the camera, defaults to colorPrimary of Activity in the constructor
                .showPortraitWarning(true)                         // Whether or not a warning is displayed if the user presses record in portrait orientation
                .defaultToFrontFacing(false)                         // Allows the user to change cameras.
                .retryExits(false)                                 // If true, the 'Retry' button in the playback screen will exit the camera instead of going back to the recorder
                .restartTimerOnRetry(false)                        // If true, the countdown timer is reset to 0 when the user taps 'Retry' in playback
                .continueTimerInPlayback(false)                    // If true, the countdown timer will continue to go down during playback, rather than pausing.
                .videoEncodingBitRate(1024000)                     // Sets a custom bit rate for video recording.
                .audioEncodingBitRate(50000)                       // Sets a custom bit rate for audio recording.
                .videoFrameRate(24)                                // Sets a custom frame rate (FPS) for video recording.
                .qualityProfile(MaterialCamera.QUALITY_HIGH)       // Sets a quality profile, manually setting bit rates or frame rates with other settings will overwrite individual quality profile settings
                .videoPreferredHeight(720)                         // Sets a preferred height for the recorded video output.
                .videoPreferredAspect(4f / 3f)                     // Sets a preferred aspect ratio for the recorded video output.
                .maxAllowedFileSize(1024 * 1024 * 5)               // Sets a max file size of 5MB, recording will stop if file reaches this limit. Keep in mind, the FAT file system has a file size limit of 4GB.
                .iconRecord(R.drawable.mcam_action_capture)        // Sets a custom icon for the button used to start recording
                .iconStop(R.drawable.mcam_action_stop)             // Sets a custom icon for the button used to stop recording
                .iconFrontCamera(R.drawable.mcam_camera_front)     // Sets a custom icon for the button used to switch to the front camera
                .iconRearCamera(R.drawable.mcam_camera_rear)       // Sets a custom icon for the button used to switch to the rear camera
                .iconPlay(R.drawable.evp_action_play)              // Sets a custom icon used to start playback
                .iconPause(R.drawable.evp_action_pause)            // Sets a custom icon used to pause playback
                .iconRestart(R.drawable.evp_action_restart)        // Sets a custom icon used to restart playback
                .labelRetry(R.string.mcam_retry)                   // Sets a custom button label for the button used to retry recording, when available
                .labelConfirm(R.string.mcam_use_video)             // Sets a custom button label for the button used to confirm/submit a recording
                .audioDisabled(false)                              // Set to true to record video without any audio.
                .start(CAMERA_RQ);

    }

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
        startActivity(new Intent(PeriodicVideoSMSActivity.this, MyEventsActivity.class));
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getContacts() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            getAllContacts();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToSMS() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
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
                recordAudio();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(PeriodicVideoSMSActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(PeriodicVideoSMSActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(PeriodicVideoSMSActivity.this, permissionsRequired[2])) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PeriodicVideoSMSActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This feature needs some permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(PeriodicVideoSMSActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Received recording or error from MaterialCamera
        if (requestCode == CAMERA_RQ) {

            if (resultCode == RESULT_OK) {
                field_audio_title.setText("Uploading...Please Wait...");
                Toast.makeText(PeriodicVideoSMSActivity.this, "Uploading...", Toast.LENGTH_LONG).show();
                mStorageRef = FirebaseStorage.getInstance().getReference();
                Uri file = data.getData();
                StorageReference riversRef = mStorageRef.child("video/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + file.getLastPathSegment());

                riversRef.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                field_audio_title.setText("Upload successful");
                                recordedAudioUrl = downloadUrl + "";
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                                field_audio_title.setText("Upload Failed");
                            }
                        });
                Toast.makeText(this, "Uploading...", Toast.LENGTH_LONG).show();
            } else if (data != null) {
                field_audio_title.setText("");
                Exception e = (Exception) data.getSerializableExtra(MaterialCamera.ERROR_EXTRA);
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(PeriodicVideoSMSActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                recordAudio();
            }
        }
    }

    private void requestAudioRecordPermission() {
        if (ActivityCompat.checkSelfPermission(PeriodicVideoSMSActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(PeriodicVideoSMSActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(PeriodicVideoSMSActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(PeriodicVideoSMSActivity.this, permissionsRequired[1])) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(PeriodicVideoSMSActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This feature needs READ_CONTACTS and SEND_SMS permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(PeriodicVideoSMSActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(PeriodicVideoSMSActivity.this);
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
                ActivityCompat.requestPermissions(PeriodicVideoSMSActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            recordAudio();
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(PeriodicVideoSMSActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                recordAudio();
            }
        }
    }

    private void writeNewEvent(String userId, String username, String title, String location, String startDate, String endDate, String fromTime, String toTime, String type, String description, ArrayList<String> selectedUsersIds) {

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

            final String key = FirebaseDatabase.getInstance().getReference().child("Events").push().getKey();

            final Event event = new Event(userId, username, title, location, startDate, endDate, fromTime, toTime, type, description);
            Map<String, Object> eventValues = event.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/Events/" + key, eventValues);
            childUpdates.put("/user-events/" + userId + "/" + key, eventValues);

            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            }

        } catch (Exception e) {

        }

    }
}
