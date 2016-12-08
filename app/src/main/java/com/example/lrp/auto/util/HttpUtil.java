package com.example.lrp.auto.util;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2016/12/7.
 */

public class HttpUtil {
    private static final String TAG = "HttpUtil";

    public static void sendOkHttpRequest(String adress, okhttp3.Callback callback) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(adress).build();

        client.newCall(request).enqueue(callback);

        Log.d(TAG, "sendOkHttpRequest:  is running");
    }

}
