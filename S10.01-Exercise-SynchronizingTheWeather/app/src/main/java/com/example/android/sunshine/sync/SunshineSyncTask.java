package com.example.android.sunshine.sync;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

//  TODO (1) Create a class called SunshineSyncTask
public class SunshineSyncTask {
//  TODO (2) Within SunshineSyncTask, create a synchronized public static void method called syncWeather
    synchronized public static void syncWeather(Context context) {
//      TODO (3) Within syncWeather, fetch new weather data
        URL weatherRequestUrl = NetworkUtils.getUrl(context);
        try {
            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
//      TODO (4) If we have valid results, delete the old data and insert the new
            if (!TextUtils.isEmpty(jsonWeatherResponse)) {
                context.getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI, null, null);
                ContentValues[] contentValues = OpenWeatherJsonUtils.getWeatherContentValuesFromJson(context, jsonWeatherResponse);
                context.getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, contentValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}