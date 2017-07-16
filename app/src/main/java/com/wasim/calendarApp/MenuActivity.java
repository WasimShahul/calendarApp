package com.wasim.calendarApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.wasim.calendarApp.utils.FontFaces;

public class MenuActivity extends AppCompatActivity {

    private TextView menu_all_events, menu_logout, close_button, activity_event_title_behind_txtView, activity_event_menu_txtView, menu_create_events;
    private LinearLayout close_ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menu_all_events = (TextView) findViewById(R.id.menu_all_events);
        menu_logout = (TextView) findViewById(R.id.menu_logout);
        close_button = (TextView) findViewById(R.id.close_button);
        activity_event_title_behind_txtView = (TextView) findViewById(R.id.activity_event_title_behind_txtView);
        activity_event_menu_txtView = (TextView) findViewById(R.id.activity_event_menu_txtView);
        menu_create_events = (TextView) findViewById(R.id.menu_create_events);
        close_ll = (LinearLayout) findViewById(R.id.close_ll);

        menu_all_events.setTypeface(FontFaces.montserratBold(this));
        menu_logout.setTypeface(FontFaces.montserratBold(this));
        close_button.setTypeface(FontFaces.montserratBold(this));
        activity_event_title_behind_txtView.setTypeface(FontFaces.montserratBold(this));
        activity_event_menu_txtView.setTypeface(FontFaces.montserratBold(this));
        menu_create_events.setTypeface(FontFaces.montserratBold(this));

        activity_event_menu_txtView.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

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
}
