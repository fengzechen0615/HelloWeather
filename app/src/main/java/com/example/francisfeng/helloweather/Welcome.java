package com.example.francisfeng.helloweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.francisfeng.helloweather.Databases.CopyDatabase;
import com.example.francisfeng.helloweather.Databases.WeatherDataHelper;

public class Welcome extends AppCompatActivity {

    private WeatherDataHelper dataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // 运行copy数据库 和 创建数据库指令
        CopyDatabase.packDataBase(this);
        dataHelper = new WeatherDataHelper(this);
        dataHelper.getWritableDatabase();
        CopyDatabase.packDataBase(this);

        // 读缓存
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getString( "weather", null) != null) {
            Intent intent = new Intent(this, WeatherInformation.class);
            startActivity(intent);
            finish();
        }
    }
}
