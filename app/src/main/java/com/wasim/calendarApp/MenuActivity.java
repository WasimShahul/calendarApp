package com.wasim.calendarApp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.firebase.auth.FirebaseAuth;
import com.wasim.calendarApp.utils.FontFaces;

public class MenuActivity extends AppCompatActivity {

    private TextView menu_all_events, menu_timely_sms, menu_timely_video_sms, menu_logout, menu_home, close_button, activity_event_title_behind_txtView, activity_event_menu_txtView, menu_create_events;
    private LinearLayout close_ll;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS};
    private TextView txtPermissions;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private static String menuSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        menu_all_events = (TextView) findViewById(R.id.menu_all_events);
        menu_logout = (TextView) findViewById(R.id.menu_logout);
        close_button = (TextView) findViewById(R.id.close_button);
        menu_home = (TextView) findViewById(R.id.menu_home);
        menu_timely_sms = (TextView) findViewById(R.id.menu_timely_sms);
        menu_timely_video_sms = (TextView) findViewById(R.id.menu_timely_video_sms);
        activity_event_title_behind_txtView = (TextView) findViewById(R.id.activity_event_title_behind_txtView);
        activity_event_menu_txtView = (TextView) findViewById(R.id.activity_event_menu_txtView);
        menu_create_events = (TextView) findViewById(R.id.menu_create_events);
        close_ll = (LinearLayout) findViewById(R.id.close_ll);

        menu_all_events.setTypeface(FontFaces.montserratBold(this));
        menu_logout.setTypeface(FontFaces.montserratBold(this));
        close_button.setTypeface(FontFaces.montserratBold(this));
        menu_home.setTypeface(FontFaces.montserratBold(this));
        menu_timely_sms.setTypeface(FontFaces.montserratBold(this));
        menu_timely_video_sms.setTypeface(FontFaces.montserratBold(this));
        activity_event_title_behind_txtView.setTypeface(FontFaces.montserratBold(this));
        activity_event_menu_txtView.setTypeface(FontFaces.montserratBold(this));
        menu_create_events.setTypeface(FontFaces.montserratBold(this));

        activity_event_menu_txtView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            menu_timely_sms.setVisibility(View.VISIBLE);
        } else {
            menu_timely_sms.setVisibility(View.GONE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            menu_timely_video_sms.setVisibility(View.VISIBLE);
        } else {
            menu_timely_video_sms.setVisibility(View.GONE);
        }

        menu_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, MyEventsActivity.class));
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                finish();
            }
        });
        menu_timely_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuSelected = "TimelySMS";
                if (ActivityCompat.checkSelfPermission(MenuActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MenuActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MenuActivity.this, permissionsRequired[0])
                            || ActivityCompat.shouldShowRequestPermissionRationale(MenuActivity.this, permissionsRequired[1])) {
                        //Show Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                        builder.setTitle("Need Multiple Permissions");
                        builder.setMessage("This feature needs READ_CONTACTS and SEND_SMS permissions.");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(MenuActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
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
                        ActivityCompat.requestPermissions(MenuActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
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
        menu_timely_video_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuSelected = "TimelyVideoSMS";
                if (ActivityCompat.checkSelfPermission(MenuActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(MenuActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MenuActivity.this, permissionsRequired[0])
                            || ActivityCompat.shouldShowRequestPermissionRationale(MenuActivity.this, permissionsRequired[1])) {
                        //Show Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                        builder.setTitle("Need Multiple Permissions");
                        builder.setMessage("This feature needs READ_CONTACTS and SEND_SMS permissions.");
                        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                ActivityCompat.requestPermissions(MenuActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
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
                        ActivityCompat.requestPermissions(MenuActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
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
        menu_all_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                finish();
            }
        });
        menu_create_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, NewEventActivity.class));
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                finish();
            }
        });
        menu_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MenuActivity.this, SignInActivity.class));
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                finish();
            }
        });
        close_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, MyEventsActivity.class));
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
                finish();
            }
        });
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
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(MenuActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MenuActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MenuActivity.this, permissionsRequired[2])) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This feature needs READ_CONTACTS and SEND_SMS permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MenuActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
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
            if (ActivityCompat.checkSelfPermission(MenuActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    private void proceedAfterPermission() {
        if (menuSelected.equals("TimelySMS")) {
            startActivity(new Intent(MenuActivity.this, PeriodicSMSActivity.class));
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            finish();
        } else if (menuSelected.equals("TimelyVideoSMS")) {
            startActivity(new Intent(MenuActivity.this, PeriodicVideoSMSActivity.class));
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            finish();
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(MenuActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

}
