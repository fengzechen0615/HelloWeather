package com.example.francisfeng.helloweather.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by francisfeng on 06/12/2017.
 */

public class WeatherDataHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "hello_weather.db";
    private static final int DATABASE_VERSION = 1;

    /** Create a helper object for the Events database */
    public WeatherDataHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}

