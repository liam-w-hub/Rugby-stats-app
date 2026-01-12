package com.example.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Add button click listeners for navigation
        Button createMatchButton = findViewById(R.id.createMatchButton);
        Button previousMatchesButton = findViewById(R.id.previousMatchesButton);
        // Button viewStatisticsButton = findViewById(R.id.viewStatisticsButton);

        createMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, CreateMatchActivity.class);
                startActivity(intent);
            }
        });

        previousMatchesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, PreviousMatchesActivity.class);
                startActivity(intent);
            }
        });
    }
}
