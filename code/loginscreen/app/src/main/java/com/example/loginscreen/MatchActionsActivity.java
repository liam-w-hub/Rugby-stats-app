package com.example.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MatchActionsActivity extends AppCompatActivity {

    private Button positiveTurnoverButton, negativeTurnoverButton, lineoutLossButton, lineoutWinButton, knockOnButton;
    private Button buttonQ1, buttonQ2, buttonQ3, buttonQ4, buttonQ5, buttonQ6, buttonQ7, buttonQ8;
    private Button finishButton;
    private EditText playerNumberEditText;

    private DatabaseReference matchRef;
    private RugbyDataClass databaseHelper;
    private String matchId, teamName, opponentName, matchDate;
    private String selectedLocation = "", selectedEvent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_actions);

        // Retrieve passed data from creatematchactivity
        Intent intent = getIntent();
        matchId = intent.getStringExtra("MATCH_ID");
        teamName = intent.getStringExtra("TEAM_NAME");
        opponentName = intent.getStringExtra("OPPONENT_NAME");
        matchDate = intent.getStringExtra("MATCH_DATE");

        // Initialize Firebase database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://rugby-stats-40be8-default-rtdb.europe-west1.firebasedatabase.app");
        matchRef = database.getReference("matches").child(matchId).child("events");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // get user id from firebase
        String userId = (user != null) ? user.getUid() : "Unknown";

        DatabaseReference matchMetaRef = database.getReference("matches").child(matchId);
        matchMetaRef.child("userId").setValue(userId);

        // Initialize SQLite database
        databaseHelper = RugbyDataClass.getInstance(this);

        // Initialize UI elements
        playerNumberEditText = findViewById(R.id.playerNumberEditText);
        finishButton = findViewById(R.id.finishButton);


        positiveTurnoverButton = findViewById(R.id.positiveTurnoverButton);
        negativeTurnoverButton = findViewById(R.id.negativeTurnoverButton);
        lineoutLossButton = findViewById(R.id.lineoutLossButton);
        lineoutWinButton = findViewById(R.id.lineoutWinButton);
        knockOnButton = findViewById(R.id.knockOnButton);

        buttonQ1 = findViewById(R.id.buttonQ1);
        buttonQ2 = findViewById(R.id.buttonQ2);
        buttonQ3 = findViewById(R.id.buttonQ3);
        buttonQ4 = findViewById(R.id.buttonQ4);
        buttonQ5 = findViewById(R.id.buttonQ5);
        buttonQ6 = findViewById(R.id.buttonQ6);
        buttonQ7 = findViewById(R.id.buttonQ7);
        buttonQ8 = findViewById(R.id.buttonQ8);

        // Event selection listeners
        positiveTurnoverButton.setOnClickListener(v -> selectEvent("Positive Turnover"));
        negativeTurnoverButton.setOnClickListener(v -> selectEvent("Negative Turnover"));
        lineoutLossButton.setOnClickListener(v -> selectEvent("Lineout Loss"));
        lineoutWinButton.setOnClickListener(v -> selectEvent("Lineout Win"));
        knockOnButton.setOnClickListener(v -> selectEvent("Knock-On"));

        // Location selection listeners
        buttonQ1.setOnClickListener(v -> recordLocation("q1"));
        buttonQ2.setOnClickListener(v -> recordLocation("q2"));
        buttonQ3.setOnClickListener(v -> recordLocation("q3"));
        buttonQ4.setOnClickListener(v -> recordLocation("q4"));
        buttonQ5.setOnClickListener(v -> recordLocation("q5"));
        buttonQ6.setOnClickListener(v -> recordLocation("q6"));
        buttonQ7.setOnClickListener(v -> recordLocation("q7"));
        buttonQ8.setOnClickListener(v -> recordLocation("q8"));

        // Finish button to save event
        finishButton.setOnClickListener(v -> {
            String playerNumber = playerNumberEditText.getText().toString().trim();
            if (!playerNumber.isEmpty() && !selectedLocation.isEmpty() && !selectedEvent.isEmpty()) {
                int playerNumberInt = Integer.parseInt(playerNumber);
                recordEvent(selectedEvent, selectedLocation, playerNumberInt);
            } else {
                showToast("Please select an event, location, and enter a player number.");
            }
        });
    }

    // show user selected elements
    private void selectEvent(String eventType) {
        selectedEvent = eventType;
        showToast("Event selected: " + eventType);
    }

    private void recordLocation(String location) {
        selectedLocation = location;
        showToast("Location selected: " + location);
    }

    private void recordEvent(String eventType, String location, int playerNumber) {
        String timeStamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // Save to Firebase
        Event event = new Event(eventType, location, playerNumber, timeStamp);
        matchRef.push().setValue(event)
                .addOnSuccessListener(aVoid -> showToast("Event recorded in Firebase"))
                .addOnFailureListener(e -> showToast("Failed to record event in Firebase"));

        // Save to SQLite
        boolean success = databaseHelper.addEvent(teamName, opponentName, matchDate, eventType, location, playerNumber, timeStamp, FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (success) {
            showToast("Event recorded in local database");
        } else {
            showToast("Failed to record event in local database");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public class Event {
        public String event, location, timeStamp;
        public int playerNumber;

        public Event() {}

        public Event(String event, String location, int playerNumber, String timeStamp) {
            this.event = event;
            this.location = location;
            this.playerNumber = playerNumber;
            this.timeStamp = timeStamp;
        }
    }
}



