package com.example.lrp.auto.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.lrp.auto.db.City;
import com.example.lrp.auto.db.County;
import com.example.lrp.auto.db.Province;
import com.example.lrp.auto.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/12/7.
 */

public class Utility {

    private static final String TAG = "Utility";

    public static boolean handleProvinceResponse(String response) {

        Log.d(TAG, "handleProvinceResponse:  is running");

        if (!TextUtils.isEmpty(response)) {


            try {
                JSONArray allProvinces = new JSONArray(response);

                for (int i = 0; i < allProvinces.length(); i++) {

                    JSONObject provinceObject = allProvinces.getJSONObject(i);

                    Province province = new Province();

                    province.setProvinceName(provinceObject.getString("name"));

                    province.setProvinceCod(provinceObject.getInt("id"));
                    province.save();

                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return false;
    }


    public static boolean handleCityResponse(String response, int provinceId) {

        Log.d(TAG, "handleCityResponse:  is running");
        if (!TextUtils.isEmpty(response)) {

            try {
                JSONArray allCities = new JSONArray(response);

                for (int i = 0; i < allCities.length(); i++) {

                    JSONObject cityObject = allCities.getJSONObject(i);

                    City city = new City();

                    city.setCityName(cityObject.getString("name"));

                    city.setCityCode(cityObject.getInt("id"));

                    city.setProvinceId(provinceId);

                    city.save();

                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;

    }


    public static boolean handleCountyResponse(String response, int cityId) {

        Log.d(TAG, "handleCountyResponse:  is runnning");
        if (!TextUtils.isEmpty(response)) {

            try {
                JSONArray allCounties = new JSONArray(response);

                for (int i = 0; i < allCounties.length(); i++) {

                    JSONObject countyObject = allCounties.getJSONObject(i);

                    County country = new County();

                    country.setcountyName(countyObject.getString("name"));

                    country.setWeatherId(countyObject.getString("weather_id"));

                    country.setCityId(cityId);

                    country.save();

                }
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;


    }


    public static Weather handlWeatherResponse(String response) {


        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
