package com.example.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateMatchActivity extends AppCompatActivity {


    private FirebaseDatabase database;
    private DatabaseReference matchRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_match);

        // initialisng database by link to realtime database
        database = FirebaseDatabase.getInstance("https://rugby-stats-40be8-default-rtdb.europe-west1.firebasedatabase.app/");


        // Reference UI elements
        EditText teamNameEditText = findViewById(R.id.teamNameEditText);
        EditText opponentNameEditText = findViewById(R.id.opponentNameEditText);
        Button createMatchButton = findViewById(R.id.createMatchButton);


        createMatchButton.setOnClickListener(v -> {
            String teamName = teamNameEditText.getText().toString().trim();
            String opponentName = opponentNameEditText.getText().toString().trim();
            String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

            if (teamName.isEmpty() || opponentName.isEmpty() || date.isEmpty()) {
                Toast.makeText(CreateMatchActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            } else {

                String matchId = String.valueOf(System.currentTimeMillis());

                Match match = new Match(teamName, opponentName, date);

                matchRef = database.getReference("matches").child(matchId);
                matchRef.setValue(match).addOnSuccessListener(aVoid -> {
                            Intent intent = new Intent(CreateMatchActivity.this, MatchActionsActivity.class);
                            intent.putExtra("MATCH_ID", matchId); // sending all data to matchactionactivity
                            intent.putExtra("TEAM_NAME", teamName);
                            intent.putExtra("OPPONENT_NAME", opponentName);
                            intent.putExtra("MATCH_DATE", date); // pass to matchactionsactivity
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            // error handling
                            Toast.makeText(CreateMatchActivity.this, "Failed to create match.", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    public static class Match {
        public String teamName, opponentName, date;

        public Match() {}

        public Match(String teamName, String opponentName, String date) {
            this.teamName = teamName;
            this.opponentName = opponentName;
            this.date = date;
        }
    }
}


