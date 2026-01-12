package com.example.loginscreen;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class FirebaseDBTest {

    private DatabaseReference testDB;

    @Before
    public void setTestDB() {
        testDB = FirebaseDatabase.getInstance().getReference();
    }

    @Test
    public void createMatchTest() {
        String matchID = "match12345";
        String teamName = "DCU";
        String opponentName = "Trinity";

        testDB.child("matches").child(matchID).setValue(new Match(matchID, teamName, opponentName))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        testDB.child("matches").child(matchID).get().addOnCompleteListener(dataTask -> {
                            if (dataTask.isSuccessful()) {
                                Match retrievedMatch = dataTask.getResult().getValue(Match.class);
                                assertNotNull("Retrieved match data should not be null", retrievedMatch);
                                assertEquals("Team name should match", teamName, retrievedMatch.getTeamName());
                            } else {
                                fail("Failed to retrieve match data: " + dataTask.getException().getMessage());
                            }
                        });
                    } else {
                        fail("Failed to save match data: " + task.getException().getMessage());
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

        public String getTeamName() {
            return teamName;
        }
    }
}
