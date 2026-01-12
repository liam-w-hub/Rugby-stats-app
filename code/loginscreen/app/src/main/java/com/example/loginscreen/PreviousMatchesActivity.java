package com.example.loginscreen;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PreviousMatchesActivity extends AppCompatActivity {

    private static final String TAG = "PreviousMatchesActivity";

    private ListView matchesListView;
    private ArrayAdapter<String> matchesAdapter;
    private ArrayList<String> matchesList;
    private RugbyDataClass rugbyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_matches);

        matchesListView = findViewById(R.id.matchesListView);
        matchesList = new ArrayList<>();
        matchesAdapter = new ArrayAdapter<>(this, R.layout.stat, matchesList);
        matchesListView.setAdapter(matchesAdapter);

        rugbyData = RugbyDataClass.getInstance(this);

        // display match events
        loadMatches();
    }

   // get data from sqlite
    private void loadMatches() {
        SQLiteDatabase db = rugbyData.getReadableDatabase();

        // Defining  columns to retrieve.
        String[] projection = {
                "matchDate",
                "opponentName",
                "eventType",
                "playerNumber",
                "pitchLocation"
        };

        Cursor cursor = db.query(
                "Event",
                projection,
                null,
                null,
                null,
                null,
                null
        );


        matchesList.clear();
        if (cursor.moveToFirst()) { // read results returned by database query to sqlite db
            do {
                String matchDate = cursor.getString(cursor.getColumnIndexOrThrow("matchDate"));
                String opponentName = cursor.getString(cursor.getColumnIndexOrThrow("opponentName"));
                String eventType = cursor.getString(cursor.getColumnIndexOrThrow("eventType"));
                int playerNumber = cursor.getInt(cursor.getColumnIndexOrThrow("playerNumber"));
                String pitchLocation = cursor.getString(cursor.getColumnIndexOrThrow("pitchLocation"));

                String displayText = "Date: " + matchDate +
                        "\nOpponent: " + opponentName +
                        "\nEvent: " + eventType +
                        "\nPlayer Number: " + playerNumber +
                        "\nLocation: " + pitchLocation;
                matchesList.add(displayText);
                Log.d(TAG, "Loaded match: " + displayText);
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "No matches found!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "No match records found in the database.");
        }
        cursor.close();

        matchesAdapter.notifyDataSetChanged();
    }
}



