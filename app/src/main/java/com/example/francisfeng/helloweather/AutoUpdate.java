package com.example.francisfeng.helloweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.francisfeng.helloweather.Utility.RequestHttp;
import com.example.francisfeng.helloweather.Utility.DealData;
import com.example.francisfeng.helloweather.WeatherInfo.Weather;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdate extends Service {

    public AutoUpdate() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        autoUpdateWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int deleteHour = 6 * 60 * 60 * 1000; // 这是6小时的毫秒数
//        int anHour = 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + deleteHour;
        Intent intent1 = new Intent(this, AutoUpdate.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent1, 0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息。
     */
    private void autoUpdateWeather(){
        // 读缓存
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sharedPreferences.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = DealData.handleWeatherResponse(weatherString);
            String cityId = weather.basic.location;
            String weatherUrl = "https://free-api.heweather.com/s6/weather?location=" + cityId + "&key=2c874b81ca9c48669cf592d5de94474a&lang=en";
            System.out.println(cityId);
            RequestHttp.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = DealData.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        // 存缓存
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdate.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
