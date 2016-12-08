package com.example.lrp.auto.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/12/7.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")

    public More more;


    public class More {

        @SerializedName("txt")

        public String info;

    }


}


