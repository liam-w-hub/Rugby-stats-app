package com.example.loginscreen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RugbyDataClass extends SQLiteOpenHelper {

    private static RugbyDataClass instance;
    private static final String DATABASE_NAME = "localDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Event";

    private static final String COUNTER = "Counter";
    private static final String TEAM_NAME = "teamName";
    private static final String OPPONENT_NAME = "opponentName";
    private static final String DATE = "matchDate";
    private static final String EVENT = "eventType";
    private static final String LOCATION = "pitchLocation";
    private static final String PLAYER_NUMBER = "playerNumber";
    private static final String TIME = "time";
    private static final String USER_ID = "userId";

    public RugbyDataClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized RugbyDataClass getInstance(Context context) {
        if (instance == null) {
            instance = new RugbyDataClass(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COUNTER + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TEAM_NAME + " TEXT, " +
                OPPONENT_NAME + " TEXT, " +
                DATE + " TEXT, " +
                EVENT + " TEXT, " +
                LOCATION + " TEXT, " +
                PLAYER_NUMBER + " INTEGER, " +
                TIME + " TEXT, " +
                USER_ID + " TEXT" +
                ");";

        db.execSQL(sql);
    }

    @Override // update changes in DB
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addEvent(String teamName, String opponentName, String matchDate, String eventType, String pitchLocation, int playerNumber, String time, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TEAM_NAME, teamName);
        values.put(OPPONENT_NAME, opponentName);
        values.put(DATE, matchDate);
        values.put(EVENT, eventType);
        values.put(LOCATION, pitchLocation);
        values.put(PLAYER_NUMBER, playerNumber);
        values.put(TIME, time);
        values.put(USER_ID, userId);

        long result = db.insert(TABLE_NAME, null, values);
        db.close();

        return result != -1;
    }


    public Cursor getAllEvents() { // query and read all rows in local db
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}

