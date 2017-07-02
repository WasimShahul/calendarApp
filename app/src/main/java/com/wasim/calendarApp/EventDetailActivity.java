package com.wasim.calendarApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wasim.calendarApp.models.Event;
import com.wasim.calendarApp.models.InvitedUser;
import com.wasim.calendarApp.utils.Constant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class EventDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "EventDetailActivity";

    public static final String EXTRA_EVENT_KEY = "event_key";

    private DatabaseReference mEventReference;
    private DatabaseReference mEventUsersReference;
    private ValueEventListener mEventListener;
    private String mEventKey;
    private InvitedUserAdapter mAdapter;

    private static String CurrentEventHost;

    private TextView mHostView;
    private LinearLayout InviteUsersForm;
    private TextView mDateView;
    private TextView mTimeView;
    private Button mInviteButton;
    private ImageView mViewMoreButton;
    private RecyclerView mUsersRecycler;

    //User Selection
    ArrayList<String> usersEmail = new ArrayList<String>();
    ArrayList<String> selectedUsersIds = new ArrayList<String>();
    private HashMap<String, String> dataresult;
    private MultiAutoCompleteTextView mUsersField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Get event key from intent
        mEventKey = getIntent().getStringExtra(EXTRA_EVENT_KEY);
        if (mEventKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_EVENT_KEY");
        }

        // Initialize Database
        mEventReference = FirebaseDatabase.getInstance().getReference()
                .child("Events").child(mEventKey);
        mEventUsersReference = FirebaseDatabase.getInstance().getReference()
                .child("Events").child(mEventKey).child("users");

        // Initialize Views
        mHostView = (TextView) findViewById(R.id.event_host);
        mDateView = (TextView) findViewById(R.id.event_date);
        mTimeView = (TextView) findViewById(R.id.event_time);
        mUsersField = (MultiAutoCompleteTextView) findViewById(R.id.field_users_text);
        mInviteButton = (Button) findViewById(R.id.button_event_invite);
        mViewMoreButton = (ImageView) findViewById(R.id.viewmorebtn);
        InviteUsersForm = (LinearLayout) findViewById(R.id.invite_users_form);
        mUsersRecycler = (RecyclerView) findViewById(R.id.recycler_invited_users);

        mViewMoreButton.setVisibility(View.INVISIBLE);
        mInviteButton.setOnClickListener(this);
        mUsersRecycler.setLayoutManager(new LinearLayoutManager(this));

        getAllUserEmail();


    }

    private Map<String, String> getAllUserEmail() {
        HashMap<String, String> result = new HashMap<>();
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//               Log.e("NewEventActivity: value", String.valueOf(dataSnapshot.getValue()));
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
            //Get phone field and append to list
            if (!singleUser.get("email").toString().trim().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().trim())) {
                usersEmail.add(String.valueOf(singleUser.get("email")));
            }
            dataresult.put(String.valueOf(singleUser.get("email")), entry.getKey());
        }

        ArrayAdapter<String> emailIds = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, usersEmail);
        mUsersField.setAdapter(emailIds);
        mUsersField.setThreshold(3);
        mUsersField.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the event
        // [START event_value_event_listener]
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                // Get Event object and use the values to update the UI
                Event event = dataSnapshot.getValue(Event.class);
                // [START_EXCLUDE]
                mHostView.setText(event.host);

                SharedPreferences shared = getSharedPreferences(Constant.PREFERENCES, MODE_PRIVATE);
                String timeZone = (shared.getString(Constant.timeZone, ""));
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
                String startDateString = event.date + " " + event.time;
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                df.setTimeZone(TimeZone.getTimeZone("UST"));
                String dateVal = null;

                    dateVal = formatter.format(df.parse(startDateString));

                Log.e("EventViewHolder 2", dateVal);
                String[] splited = dateVal.split("\\s+");
                String date = splited[0];
                String time = splited[1];

                mDateView.setText("On: "+date);
                mTimeView.setText(time+" Hrs");
                CurrentEventHost = event.uid;

                if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(CurrentEventHost)){
                    InviteUsersForm.setVisibility(View.VISIBLE);
                } else{
                    InviteUsersForm.setVisibility(View.GONE);
                }
                // [END_EXCLUDE]
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Event failed, log a message
                Log.w(TAG, "loadevent:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(EventDetailActivity.this, "Failed to load event.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mEventReference.addValueEventListener(eventListener);
        // [END event_value_event_listener]

        // Keep copy of event listener so we can remove it when app stops
        mEventListener = eventListener;

        mAdapter = new InvitedUserAdapter(this, mEventUsersReference);
        mUsersRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove event value event listener
        if (mEventListener != null) {
            mEventReference.removeEventListener(mEventListener);
        }

        // Clean up listener
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_event_invite) {
            eventInvites();
        }
    }

    private void eventInvites() {
        String str = mUsersField.getText().toString();
        List<String> emailIds = Arrays.asList(str.split(","));
        for (String email :
                emailIds) {
            email = email.toLowerCase().trim();
            if (usersEmail.contains(email)) {
                selectedUsersIds.add(getIdFromEmail(email));
            }
        }
        final DatabaseReference InvitedUserReference = FirebaseDatabase.getInstance().getReference().child("Events").child(mEventKey).child("users");
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

    private static class InvitedUserViewHolder extends RecyclerView.ViewHolder {

        public TextView hostTxtView;
        public TextView dateTimeTxtView;
        public Button acceptBtn;
        public Button rejectBtn;
        public Button deleteBtn;

        public InvitedUserViewHolder(View itemView) {
            super(itemView);

            hostTxtView = (TextView) itemView.findViewById(R.id.host_user);
            dateTimeTxtView = (TextView) itemView.findViewById(R.id.event_date_time);
            acceptBtn = (Button) itemView.findViewById(R.id.acceptBtn);
            rejectBtn = (Button) itemView.findViewById(R.id.rejectBtn);
            deleteBtn = (Button) itemView.findViewById(R.id.deleteBtn);
        }
    }

    private static class InvitedUserAdapter extends RecyclerView.Adapter<InvitedUserViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mInvitedUserIds = new ArrayList<>();
        private List<InvitedUser> mInvitedUser = new ArrayList<>();

        public InvitedUserAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new user has been added, add it to the displayed list
                    InvitedUser invitedUser = dataSnapshot.getValue(InvitedUser.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mInvitedUserIds.add(dataSnapshot.getKey());
                    mInvitedUser.add(invitedUser);
                    notifyItemInserted(mInvitedUser.size() - 1);
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    InvitedUser newUser = dataSnapshot.getValue(InvitedUser.class);
                    String newUserKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int userIndex = mInvitedUserIds.indexOf(newUserKey);
                    if (userIndex > -1) {
                        // Replace with the new data
                        mInvitedUser.set(userIndex, newUser);

                        // Update the RecyclerView
                        notifyItemChanged(userIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + newUserKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    String newUserKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int userIndex = mInvitedUserIds.indexOf(newUserKey);
                    if (userIndex > -1) {
                        // Remove data from the list
                        mInvitedUserIds.remove(userIndex);
                        mInvitedUser.remove(userIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(userIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + newUserKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "eventusers:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load users.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
            // [END child_event_listener_recycler]
            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        @Override
        public InvitedUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_invited_users, parent, false);
            return new InvitedUserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(InvitedUserViewHolder holder, int position) {
            final InvitedUser invitedUser = mInvitedUser.get(position);
            holder.hostTxtView.setText(invitedUser.email);
            holder.dateTimeTxtView.setText("Current status: "+invitedUser.status);
            if(invitedUser.uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    holder.acceptBtn.setVisibility(View.VISIBLE);
                    holder.rejectBtn.setVisibility(View.VISIBLE);
            }

            holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabaseReference.child(invitedUser.uid).child("status").setValue("Accepted");
                }
            });

            holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabaseReference.child(invitedUser.uid).child("status").setValue("Rejected");
                }
            });
            if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(CurrentEventHost)){
                holder.deleteBtn.setVisibility(View.VISIBLE);
                Log.e(TAG,"Success");
            } else{
                Log.e(TAG,"Not success");
                holder.deleteBtn.setVisibility(View.GONE);
            }
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabaseReference.child(invitedUser.uid).removeValue();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mInvitedUser.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }

}
