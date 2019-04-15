/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

import static com.example.android.sunshine.data.WeatherContract.WeatherEntry.*;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
//      TODO (21) Implement LoaderManager.LoaderCallbacks<Cursor>

    /*
     * In this Activity, you can share the selected day's forecast. No social sharing is complete
     * without using a hashtag. #BeTogetherNotTheSame
     */
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

//  TODO (18) Create a String array containing the names of the desired data columns from our ContentProvider
    private String[] dataColumns = {COLUMN_DATE, COLUMN_WEATHER_ID, COLUMN_MIN_TEMP, COLUMN_MAX_TEMP, COLUMN_HUMIDITY, COLUMN_PRESSURE, COLUMN_WIND_SPEED};
//  TODO (19) Create constant int values representing each column name's position above
    private int COLUMN_DATE_POSITION = 0;
    private int COLUMN_WEATHER_ID_POSITION = 1;
    private int COLUMN_MIN_TEMP_POSITION = 2;
    private int COLUMN_MAX_TEMP_POSITION = 3;
    private int COLUMN_HUMIDITY_POSITION = 4;
    private int COLUMN_PRESSURE_POSITION = 5;
    private int COLUMN_WIND_SPEED_POSITION = 6;
//  TODO (20) Create a constant int to identify our loader used in DetailActivity
    private int detailLoader = 100;

    /* A summary of the forecast that can be shared by clicking the share button in the ActionBar */
    private String mForecastSummary;

//  TODO (15) Declare a private Uri field called mUri
    private Uri mUri;
    // Another solution
//    long[] dateBundleArray = new long[1];

//  TODO (10) Remove the mWeatherDisplay TextView declaration

//  TODO (11) Declare TextViews for the date, description, high, low, humidity, wind, and pressure
    private TextView dateTV, descriptionTV, highTV, lowTV, humidityTV, windTV, pressureTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//      TODO (12) Remove mWeatherDisplay TextView
//      TODO (13) Find each of the TextViews by ID
        dateTV = (TextView) findViewById(R.id.tv_date);
        descriptionTV = (TextView) findViewById(R.id.tv_description);
        highTV = (TextView) findViewById(R.id.tv_high_temp);
        lowTV = (TextView) findViewById(R.id.tv_low_temp);
        humidityTV = (TextView) findViewById(R.id.tv_humidity);
        windTV = (TextView) findViewById(R.id.tv_wind);
        pressureTV = (TextView) findViewById(R.id.tv_pressure);

//      TODO (14) Remove the code that checks for extra text
//      TODO (16) Use getData to get a reference to the URI passed with this Activity's Intent
        mUri = getIntent().getData();
        // Another solution
//        Bundle bundle = getIntent().getExtras();
//        dateBundleArray[0] = bundle.getLong(Intent.EXTRA_TEXT);
//      TODO (17) Throw a NullPointerException if that URI is null
        if (mUri == null) {
            throw new NullPointerException("URI is null");
        }
//      TODO (35) Initialize the loader for DetailActivity
        getSupportLoaderManager().initLoader(detailLoader, null, this);
        // Another solution
//        getSupportLoaderManager().initLoader(detailLoader, bundle, this);
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     *
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * DetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Settings menu item clicked */
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        /* Share menu item clicked */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

//  TODO (22) Override onCreateLoader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//          TODO (23) If the loader requested is our detail loader, return the appropriate CursorLoader
        if (id == detailLoader) {
            Log.e("zzzzz", "mUri " + mUri);
            return new CursorLoader(this, mUri, dataColumns, null, null, null);
            // Another solution
//            long dateBundle = args.getLong(Intent.EXTRA_TEXT);
//            Log.e("zzzzz", "dateBundle " + dateBundle);
//            return new CursorLoader(this, CONTENT_URI, dataColumns, WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ", new String[] {String.valueOf(dateBundle)}, null);
//            Log.e("zzzzz", "dateBundleArray[0] " + dateBundleArray[0]);
//            return new CursorLoader(this, CONTENT_URI, dataColumns, WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ", new String[] {String.valueOf(dateBundleArray[0])}, null);
        }
        else return null;
    }

//  TODO (24) Override onLoadFinished
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//      TODO (25) Check before doing anything that the Cursor has valid data
        if (data != null) {
            data.moveToPosition(0);
//      TODO (26) Display a readable data string
            long dateLong = data.getLong(COLUMN_DATE_POSITION);
            String dateString = SunshineDateUtils.getFriendlyDateString(getApplicationContext(), dateLong, false);
            dateTV.setText(dateString);
//      TODO (27) Display the weather description (using SunshineWeatherUtils)
            int weatherId = data.getInt(COLUMN_WEATHER_ID_POSITION);
            String description = SunshineWeatherUtils.getStringForWeatherCondition(getApplicationContext(), weatherId);
            descriptionTV.setText(description);
//      TODO (28) Display the high temperature
            String high = data.getString(COLUMN_MAX_TEMP_POSITION);
            highTV.setText(high);
//      TODO (29) Display the low temperature
            String low = data.getString(COLUMN_MIN_TEMP_POSITION);
            lowTV.setText(low);
//      TODO (30) Display the humidity
            String humidity = data.getString(COLUMN_HUMIDITY_POSITION);
            humidityTV.setText(humidity);
//      TODO (31) Display the wind speed and direction
            String wind = data.getString(COLUMN_WIND_SPEED_POSITION);
            windTV.setText(wind);
//      TODO (32) Display the pressure
            String pressure = data.getString(COLUMN_PRESSURE_POSITION);
            pressureTV.setText(pressure);
//      TODO (33) Store a forecast summary in mForecastSummary
            mForecastSummary = dateString + " " + description + " " + high + " " + low + " " + humidity + " " + wind + " " + pressure;
        }
    }

//  TODO (34) Override onLoaderReset, but don't do anything in it yet
    @Override
    public void onLoaderReset(Loader loader) {

    }

}