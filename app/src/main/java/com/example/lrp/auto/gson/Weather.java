package com.example.lrp.auto.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2016/12/7.
 */

public class Weather {

    public String status;

    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;


    @SerializedName("daily_forecast")

    public List<ForeCast> foreCastList;

}
