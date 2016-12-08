package com.example.lrp.auto;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lrp.auto.gson.ForeCast;
import com.example.lrp.auto.gson.Weather;
import com.example.lrp.auto.util.HttpUtil;
import com.example.lrp.auto.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherlayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqitext;
    private TextView pm25text;
    private TextView comfortText;
    private TextView carWshText;
    private TextView sportText;
    private ImageView bingPicimg;
    public DrawerLayout drawerLayout;
    private Button Navbutton;
    public SwipeRefreshLayout swipeRefresh;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {


            View dectorView = getWindow().getDecorView();
            dectorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        weatherlayout = (ScrollView) findViewById(R.id.weather_layout);

        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqitext = (TextView) findViewById(R.id.aqi_text);
        pm25text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);

        carWshText = (TextView) findViewById(R.id.car_wash_text);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Navbutton = (Button) findViewById(R.id.nav_button);
        sportText = (TextView) findViewById(R.id.sport_text);
        bingPicimg = (ImageView) findViewById(R.id.bing_pic_img);

        Navbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawerLayout.openDrawer(GravityCompat.START);


            }
        });
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicimg);

        } else {
            loadBingPic();
        }

        String weatherString = prefs.getString("weather", null);
        final String weatherId;
        if (weatherString != null) {
            Weather weather = Utility.handlWeatherResponse(weatherString);
            weatherId = weather.basic.weatherid;
            showWeatherInfo(weather);


        } else {


            weatherId = getIntent().getStringExtra("weather_id");
            weatherlayout.setVisibility(View.INVISIBLE);

            requestWeather(weatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                requestWeather(weatherId);
            }
        });


    }

    private void loadBingPic() {


        String requestBingPic = "http://guolin.tech/api/bing_pic";

        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingpic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();


                editor.putString("bing_pic", bingpic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        Glide.with(WeatherActivity.this).load(bingpic).into(bingPicimg);

                    }
                });


            }
        });


    }

    public void requestWeather(String weatherId) {

        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String responseText = response.body().string();
                final Weather weather = Utility.handlWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (weather != null && "ok".equals(weather.status)) {

//                            Intent intent = new Intent(WeatherActivity.this, AutoUpdateVice.class);
//                            startService(intent);

                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);

                            editor.apply();
                            showWeatherInfo(weather);

                        } else {

                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();

                        }
                        swipeRefresh.setRefreshing(false);

                    }
                });
                loadBingPic();

            }

            @Override
            public void onFailure(Call call, IOException e) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

    }

    private void showWeatherInfo(Weather weather) {


        String cityName = weather.basic.cityName;

        String updateTime = weather.basic.update.updateTime.split(" ")[1];


        String degree = weather.now.temperature + "度";
        String weatherInfo = weather.now.more.info;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);

        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();

        for (ForeCast foreCast : weather.foreCastList) {


            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);

            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);

            dateText.setText(foreCast.date);
            infoText.setText(foreCast.more.info);
            maxText.setText(foreCast.temperature.max);
            minText.setText(foreCast.temperature.min);
            forecastLayout.addView(view);
        }


        if (weather.aqi != null) {


            aqitext.setText(weather.aqi.city.aqi);

            pm25text.setText(weather.aqi.city.pm25);

        }


        String comfort = "舒适度" + weather.suggestion.comfort.info;
        String carWash = "洗车指数" + weather.suggestion.carWash.info;
        String sport = "运动指数" + weather.suggestion.sport.info;

        comfortText.setText(comfort);
        carWshText.setText(carWash);
        sportText.setText(sport);
        weatherlayout.setVisibility(View.VISIBLE);
    }


}
