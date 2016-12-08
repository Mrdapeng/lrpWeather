package com.example.lrp.auto.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/7.
 */

public class Basic {


    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherid;

    public Update update;

    public class Update {


        @SerializedName("loc")
        public String updateTime;

    }


}
