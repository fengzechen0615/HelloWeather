package com.example.francisfeng.helloweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.francisfeng.helloweather.Utility.DealData;
import com.example.francisfeng.helloweather.Utility.RequestHttp;
import com.example.francisfeng.helloweather.WeatherInfo.Forecast;
import com.example.francisfeng.helloweather.WeatherInfo.Weather;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherInformation extends AppCompatActivity {

    private static final int msgKey1 = 1;

    private RelativeLayout relativeLayout;

    private String cityId;

    private TextView now_degreeText;
    private TextView now_condTxt;
    private TextView now_location;
    private ImageView now_condCode;
    private TextView time_text;

    private LinearLayout forecastLayout;

    private TextView wind_sc_text;
    private TextView wind_dir_text;
    private TextView hum_text;
    private TextView pcpn_text;
    private TextView pres_text;
    private TextView vis_text;

    private ImageButton update_bt;
    private TextView update_time;
    public DrawerLayout drawerLayout;

    private Weather weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);

        relativeLayout = (RelativeLayout) findViewById(R.id.main_layout);

        now_degreeText = (TextView) findViewById(R.id.now_degree);
        now_condTxt = (TextView) findViewById(R.id.now_cond_txt);
        now_location = (TextView) findViewById(R.id.location);
        now_condCode = (ImageView) findViewById(R.id.now_cond_img);
        time_text = (TextView) findViewById(R.id.time);

        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);

        wind_sc_text = (TextView) findViewById(R.id.wind_sc);
        wind_dir_text = (TextView) findViewById(R.id.wind_dir);
        hum_text = (TextView) findViewById(R.id.hum);
        pcpn_text = (TextView) findViewById(R.id.pcpn);
        pres_text = (TextView) findViewById(R.id.pres);
        vis_text = (TextView) findViewById(R.id.vis);

        update_bt = (ImageButton) findViewById(R.id.update);
        update_time = (TextView) findViewById(R.id.update_time);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

        // 读取
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = sharedPreferences.getString("weather", null);
        // 有缓存
        if (weatherString != null) {
            weather = DealData.handleWeatherResponse(weatherString);
            cityId = weather.basic.location;
//            new layoutThread().start();
            showWeatherInfo(weather);
        }
        // 无缓存
        else {
            cityId = getIntent().getStringExtra("countyId");
            relativeLayout.setVisibility(View.INVISIBLE);
            requestWeather(cityId);
        }

        // 手动更新天气
        update_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestWeather(cityId);
                Animation animation = AnimationUtils.loadAnimation(WeatherInformation.this, R.anim.rotate);
                update_bt.startAnimation(animation);
            }
        });
    }

    public void requestWeather(final String cityId) {
        String weatherUrl = "https://free-api.heweather.com/s6/weather?location=" + cityId + "&key=2c874b81ca9c48669cf592d5de94474a&lang=en";
        this.cityId = cityId;
        RequestHttp.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherInformation.this, "Failed to get weather information", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = DealData.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            // 存入缓存
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherInformation.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherInformation.this, "Failed to get weather information", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void showWeatherInfo(Weather weather) {
        // 地点
        String location = weather.basic.location;
        now_location.setText(location.toUpperCase());
        now_location.setTextColor(Color.WHITE);

        // 时间
        new TimeThread().start();

        // 实况天气图标
        String cond_code = weather.now.cond_code;
        int x = loadImage(cond_code);
        now_condCode.setImageResource(x);

        // 实况天气温度
        String degree = weather.now.tmp + "℃";
        now_degreeText.setText(degree);
        now_degreeText.setTextColor(Color.WHITE);

        // 实况天气状况
        String cond_txt = weather.now.cond_txt;
        now_condTxt.setText(cond_txt);
        now_condTxt.setTextColor(Color.WHITE);

        // 预告
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.daily_forecast) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            ImageView cond_code_d = (ImageView) view.findViewById(R.id.f_cond_img);
            TextView cond_txt_d = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_temp);
            TextView minText = (TextView) view.findViewById(R.id.min_temp);
            dateText.setText(forecast.date);
            dateText.setTextColor(Color.WHITE);
            int y = loadImage(forecast.cond_code_d);
            cond_code_d.setImageResource(y);
            cond_txt_d.setText(forecast.cond_txt_d);
            cond_txt_d.setTextColor(Color.WHITE);
            maxText.setText(forecast.tmp_max);
            maxText.setTextColor(Color.WHITE);
            minText.setText(forecast.tmp_min);
            minText.setTextColor(Color.WHITE);
            forecastLayout.addView(view);
        }

        // 风力
        String wind_sc = weather.now.wind_sc;
        wind_sc_text.setText(wind_sc + " level");
        wind_sc_text.setTextColor(Color.WHITE);

        // 风向
        String wind_dir = weather.now.wind_dir;
        wind_dir_text.setText(wind_dir);
        wind_dir_text.setTextColor(Color.WHITE);

        // 相对湿度
        String hum = weather.now.hum;
        hum_text.setText(hum + "%");
        hum_text.setTextColor(Color.WHITE);

        // 降雨量
        String pcpn = weather.now.pcpn;
        pcpn_text.setText(pcpn + " mm");
        pcpn_text.setTextColor(Color.WHITE);

        // 大气强压
        String pres = weather.now.pres;
        pres_text.setText(pres + " hPa");
        pres_text.setTextColor(Color.WHITE);

        // 能见度
        String vis = weather.now.vis;
        vis_text.setText(vis + " km");
        vis_text.setTextColor(Color.WHITE);

        // 更新时间
        String update_time_txt = weather.update.loc;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date date = simpleDateFormat.parse(update_time_txt);
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(("MM/dd HH:mm"));
            update_time.setText("Update: " + simpleDateFormat1.format(date));
            update_time.setTextColor(Color.WHITE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        relativeLayout.setVisibility(View.VISIBLE);

        // 开始服务
        Intent intent = new Intent(this, AutoUpdate.class);
        startService(intent);

        new layoutThread().start();
    }

    public int loadImage(String code) {
        if (code.equals("100"))
            return R.drawable.w100;
        else if (code.equals("101"))
            return R.drawable.w101;
        else if (code.equals("102"))
            return R.drawable.w102;
        else if (code.equals("103"))
            return R.drawable.w103;
        else if (code.equals("104"))
            return R.drawable.w104;
        else if (code.equals("300") || code.equals("301") || code.equals("302") || code.equals("303") ||
                code.equals("304") || code.equals("305") || code.equals("306") || code.equals("307") ||
                code.equals("308") || code.equals("309") || code.equals("310") || code.equals("311") ||
                code.equals("312") || code.equals("313"))
            return R.drawable.w306;
        else if (code.equals("400") || code.equals("401") || code.equals("402") || code.equals("403") ||
                code.equals("404") || code.equals("405") || code.equals("406") || code.equals("407") ||
                code.equals("300") || code.equals("300"))
            return R.drawable.w401;
        else
            return R.drawable.w999;
    }

    public class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }

        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case msgKey1:
                        long time = System.currentTimeMillis();
                        Date date = new Date(time);
                        SimpleDateFormat format = new SimpleDateFormat("MM/dd,EEEE HH:mm", Locale.US);
                        time_text.setText(format.format(date));
                        time_text.setTextColor(Color.WHITE);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public class layoutThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                try {
                    Thread.sleep(60 * 60 *1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }

        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case msgKey1:
                        showWeatherInfo(weather);
                        break;
                    default:
                        break;
                }
            }
        };
    }
}
