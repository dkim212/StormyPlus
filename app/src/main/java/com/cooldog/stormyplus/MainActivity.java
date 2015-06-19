package com.cooldog.stormyplus;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

import static java.lang.Math.round;

// What was done in this project
// 1. Create a direct network connection to Forecast.io's API service using OkHttp
// 2. Create a try/catch error handling system, utilizing AlertDialogFragment to alert the user.
// 3. Collect data and put them into appropriate variables stated at the beginning of MainActivity.
// * I have also incorporated a plugin called ButterKnife, which eases matching between variables in Java and Res.
// 4. Used in-built Network Location Services to detect user's current location weather by last known location.
// 5. Created a somewhat appealing UI with an interchangable baackground.
// 6. Main App features completed on 6/8/2015. Estimated Time: 2 weeks, I think
// 7. Update #1 on 6/19/2015 - Implemented weather for the chosen location's next 24 hours. Activate using the slider bar upon clicking the time.
//    Some minor UI updates as well, including a min/max temperature reading.

public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /*
        The following variables are important and used in the UI layout!
        Stuff like temperature, humidity, time, location, etc...
        These variables incorporate Butter Knife plugin, an easier cleaner way of implementing Views.
     */

    // Instantiate string TAG for use in logs.
    public static final String TAG = MainActivity.class.getSimpleName();

    // Instantiate FragmentPagerAdapter and ViewPager for swapping between views.
//    MyPageAdapter mPageAdapter;
//    ViewPager mViewPager;

    // Instantiate CurrentWeather classes here.
    private CurrentWeather mCurrentWeather; // Stores current weather data (like, weather right now)
    private CurrentWeather mHourWeather; // Stores weather in the next 24 hours.

    // Instantiate last known longitude and latitude. Defaulted to New York for now.
    private double lastLongitude = 40.71;
    private double lastLatitude = -74.00;

    // Instantiate default animation time.
    public int mShortAnimationDuration;
    // Instantiates boolean for visibility of Max/Min temperatures. Starts false always.
    boolean mMaxMinVisible = false;
    // Instantiate a list that holds all JSON weather data for the next 24 hours.
    private JSONObject [] jsonArray = new JSONObject[25]; // 25, but only needs 24

    // UI to MainActivity initialization.
    // With Butterknife, we can directly change the UI with these values.
    @InjectView(R.id.timeLabel) TextView mTimeLabel;                // Shows Time
    @InjectView(R.id.temperatureLabel) TextView mTemperatureLabel;  // Shows Temperature
    @InjectView(R.id.humidityValue) TextView mHumidityValue;        // Shows Humidity
    @InjectView(R.id.precipValue) TextView mPrecipValue;            // Shows Precipitation value
    @InjectView(R.id.summaryValue) TextView mSummaryLabel;          // Shows Summary at bottom
    @InjectView(R.id.iconImageView) ImageView mIconImageView;       // Shows Weather Icon
    @InjectView(R.id.locationLabel) TextView mLocationLabel;        // Shows Current Location
    @InjectView(R.id.windSpeedValue) TextView mWindSpeedValue;      // Shows Wind Speed
    @InjectView(R.id.longitudeValue) TextView mLongitudeValue;      // Shows Longitude
    @InjectView(R.id.latitudeValue) TextView mLatitudeValue;        // Shows Latitude
    @InjectView(R.id.refreshImageView) ImageView mRefreshImageView; // Refresh icon
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;         // Progress bar of Refresh icon
    @InjectView(R.id.background) RelativeLayout mBackground;        // Relative Layout Background
    @InjectView(R.id.aboutImageView) ImageView mAbout;               // Shows About Icon/Information
    @InjectView(R.id.pager) ViewPager mPager;                       // Pager
    @InjectView(R.id.temperatureMaxValue) TextView mTemperatureMax; // Max Temperature
    @InjectView(R.id.temperatureMinValue) TextView mTemperatureMin; // Min Temperature
    @InjectView(R.id.temperatureMaxLabel) TextView mTemperatureMaxLabel;
    @InjectView(R.id.temperatureMinLabel) TextView mTemperatureMinLabel;
    @InjectView(R.id.degreeLowImageView) ImageView mDegreeLowView;
    @InjectView(R.id.degreeMaxImageView) ImageView mDegreeHighView;
    @InjectView(R.id.weeklyImageView) ImageView mWeeklyImageView;

    // =================================
    // ==== End of Initialization. =====
    // =================================

    @Override
    // Used when the program starts.
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this); // This must follow setContentView!
        // Note that doing this allows us to use Butterknife in this activity.
        // Here, the inject argument is an activity, marked by "this"

        mProgressBar.setVisibility(View.INVISIBLE); // Hide progress bar.
//        setUpPageAdapter(); // Set up Page Adapter for Horizontal Swipes.

        // Implement Refresher button.
        mRefreshImageView.setOnClickListener(new View.OnClickListener() { // OnClick for Refresh button
            @Override
            public void onClick(View view) { // Triggers when we click the Refresh button
                toastDisplayText("Refreshed.");
                getForecast(lastLongitude, lastLatitude);
            }
        });
        // Implements the location selector at the top.
        mLocationLabel.setOnClickListener(new View.OnClickListener() { // OnClick for LocationView
            @Override
            public void onClick(View view) {
                locationSelector();
            }
        });

        // Implements the About screen;
        mAbout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                AboutDialogFragment aboutDialog = new AboutDialogFragment();
                aboutDialog.show(getFragmentManager(), "Hello World!");
            }
        });

        // Implements Min/Max temperature visibility toggling.
        mTemperatureLabel.setOnClickListener(new View.OnClickListener(){
            // Pressing the Main Temperature at the center of the screen will show the min/max temps.
            @Override
            public void onClick(View view) {
                setMaxMinVisibility();
            }
        });

        mTimeLabel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                hourSeekBar();
            }
        });

        // Initialzes default location settings.
        mLocationLabel.setText("New York, NY");
        mLongitudeValue.setText("(" + lastLongitude);
        mLatitudeValue.setText("" + lastLatitude + ")");

        // Set Min/Max Temperature views to gone.
        // These will appear when you touch the main Temperature view.
        mTemperatureMax.setVisibility(View.GONE);
        mTemperatureMin.setVisibility(View.GONE);
        mTemperatureMaxLabel.setVisibility(View.GONE);
        mTemperatureMinLabel.setVisibility(View.GONE);
        mDegreeHighView.setVisibility(View.GONE);
        mDegreeLowView.setVisibility(View.GONE);

        // Hide weeklyImageView (will be implemented later! I hope...)
        mWeeklyImageView.setVisibility(View.INVISIBLE);

        // Set Default short animation time here.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        // Execute getForecast and get the weather!
        getForecast(lastLongitude, lastLatitude);
    }

    private void setMaxMinVisibility() {
        Context context = getApplication();
        // Set alphas of these texts to 0 (completely transparent)
        mTemperatureMax.setAlpha(0f);
        mTemperatureMin.setAlpha(0f);
        mTemperatureMaxLabel.setAlpha(0f);
        mTemperatureMinLabel.setAlpha(0f);
        // Set visibility to Visible.
        mTemperatureMax.setVisibility(View.VISIBLE);
        mTemperatureMaxLabel.setVisibility(View.VISIBLE);
        mTemperatureMin.setVisibility(View.VISIBLE);
        mTemperatureMinLabel.setVisibility(View.VISIBLE);
        // Play animation.
        if (mMaxMinVisible == false) {
            mTemperatureMax.animate()
                    .alpha(1f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);
            mTemperatureMaxLabel.animate()
                    .alpha(1f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);
            mTemperatureMin.animate()
                    .alpha(1f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);
            mTemperatureMinLabel.animate()
                    .alpha(1f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);
            mDegreeHighView.animate()
                    .alpha(1f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);
            mDegreeLowView.animate()
                    .alpha(1f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);
            mMaxMinVisible = true;
        }
        else{
            mTemperatureMax.animate()
                    .alpha(0f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);
            mTemperatureMaxLabel.animate()
                    .alpha(0f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);
            mTemperatureMin.animate()
                    .alpha(0f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);
            mTemperatureMinLabel.animate()
                    .alpha(0f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);
            mDegreeHighView.animate()
                    .alpha(0f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);
            mDegreeLowView.animate()
                    .alpha(0f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(null);
            mMaxMinVisible = false;
        }
    }

    private void setUpPageAdapter() {
        // OnPager stuff.
//        mPageAdapter = new MyPageAdapter(this);
        MyPagerAdapter adapter = new MyPagerAdapter();
        ViewPager myPager = (ViewPager) findViewById(R.id.pager);
        myPager.setAdapter(adapter);
        myPager.setCurrentItem(0);
    }

    @Override
    public void onConnected(Bundle savedInstanceState) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "Connection suspeneded.");
    }

    public void locationSelector() {
        final CharSequence locations[] = {"New York, NY", "Boston, MA", "San Francisco, SF", "Current Location"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select location:");

        builder.setSingleChoiceItems(locations, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int location) {
                switch (location) {
                    case 0: // New York
                        lastLongitude = 40.71;
                        lastLatitude = -74.00;
                        getForecast(40.71, -74.00);
                        mLocationLabel.setText("New York, NY");
                        mLongitudeValue.setText("(" + lastLongitude);
                        mLatitudeValue.setText("" + lastLatitude + ")");
                        toastDisplayText("Location set: New York, NY");
                        break;
                    case 1: // Boston
                        lastLongitude = 42.36;
                        lastLatitude = -71.06;
                        getForecast(42.36, -71.06);
                        mLocationLabel.setText("Boston, MA");
                        mLongitudeValue.setText("(" + lastLongitude);
                        mLatitudeValue.setText("" + lastLatitude + ")");
                        toastDisplayText("Location set: Boston, MA");
                        break;
                    case 2: // SF
                        lastLongitude = 37.78;
                        lastLatitude = -122.42;
                        getForecast(37.78, -122.42);
                        mLocationLabel.setText("San Francisco, SA");
                        mLongitudeValue.setText("(" + lastLongitude);
                        mLatitudeValue.setText("" + lastLatitude + ")");
                        toastDisplayText("Location set: San Francisco, CA");
                        break;
                    case 3: // Current location
                        lastLongitude = 0.00; // Default Longitude.
                        lastLatitude = 0.00; // Default Latitude.
                        Context mContext = getApplicationContext();
                        CurrentLocationByNetwork currentLocation = new CurrentLocationByNetwork(mContext);
                        currentLocation.getLocation();
                        lastLongitude = currentLocation.getLongitude();
                        lastLatitude = currentLocation.getLatitude();
                        getForecast(lastLatitude, lastLongitude);
                        mLocationLabel.setText("Current Location");
                        mLongitudeValue.setText("(" + lastLatitude);
                        mLatitudeValue.setText("" + lastLongitude + ")");
                        toastDisplayText("Location set: HERE");
                        break;
                }
                dialog.dismiss();
            }


        });
        builder.show();
    }

    private void toastDisplayText(CharSequence textInput) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, textInput, duration);
        toast.show();
    }

    public void getForecast(double longitude, double latitude) {
        // Instantiate APIkey and forecastURL.
        String APIkey = "980a803aa6537ec2b457d6743e165936";
        String forecastURL = "https://api.forecast.io/forecast/" + APIkey + "/" + longitude + "," + latitude;

        // Is network available? Attempt a connection . . .
        if (isNetworkAvailable()) {
            toggleRefresh(); // Toggles visibility of Progress to visible

            Log.v(TAG, "Network available, establishing connection.");
            //Client built here.
            OkHttpClient client = new OkHttpClient();
            //Request built here.
            Request request = new Request.Builder()
                    .url(forecastURL)
                    .build();
            //Call built here
            Call call = client.newCall(request);
            // Asynchronous threading built here.
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.v(TAG, "onFailure triggered.");
                    alertUserAboutError();
                    runOnUiThread(new Runnable() { // Note that any and all changes to UI in this
                        @Override                  // code must be handled by runOnUiThread!
                        public void run() {
                            toggleRefresh();
                        }
                    });}

                // JSON data from forecast.io is called here.
                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            // It worked!
                            Log.v(TAG, "Successful connection!");
                            mCurrentWeather = getCurrentWeatherData(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    toggleRefresh();
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                            Log.v(TAG, "Connection not successful - sending alert.");
                        }
                        // Catch handling done here.
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e){
                        Log.e(TAG, "JSON exception caught: ", e);
                    }
                }


            });

            Log.v(TAG, "Main UI code is running!");
        }

        // Network is not available.
        else{
            Log.v(TAG, "Network not available.");
            alertUserAboutError();
            Toast.makeText(this, getString(R.string.toast_network_unavailable), Toast.LENGTH_LONG);
        }
    }

    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == (View.INVISIBLE)) {
            mProgressBar.setVisibility(View.VISIBLE); // Allow progress bar to show.
            mRefreshImageView.setVisibility(View.INVISIBLE);
        }
        else { // Not visible
            mProgressBar.setVisibility(View.INVISIBLE); // Allow progress bar to show.
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateDisplay() {
        // This method updates the interface with actual values.
        mTemperatureLabel.setText("" + round(mCurrentWeather.getTemperature()));
        mHumidityValue.setText("" + mCurrentWeather.getHumidity());
        mPrecipValue.setText("" + mCurrentWeather.getPrecipChance() + "%");
        mTimeLabel.setText("At " + mCurrentWeather.getDate(mCurrentWeather.getTime()) + " it will be");
        mWindSpeedValue.setText(mCurrentWeather.getWindSpeed());
        mSummaryLabel.setText(mCurrentWeather.getSummary());
        mTemperatureMin.setText("" + round(mCurrentWeather.getTemperatureMin()));
        mTemperatureMax.setText("" + round(mCurrentWeather.getTemperatureMax()));
        // Maybe something needs to be put here.

        // Change icon on the top left.
        Drawable icon = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(icon);

        // Changes Background.
        Drawable background = getResources().getDrawable(mCurrentWeather.changeBackground());
        mBackground.setBackground(background);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateDisplayHourly() {
        mTemperatureLabel.setText("" + round(mHourWeather.getTemperature()));
        mHumidityValue.setText("" + mHourWeather.getHumidity());
        mPrecipValue.setText("" + mHourWeather.getPrecipChance() + "%");
        mTimeLabel.setText("At " + mHourWeather.getDate(mHourWeather.getTime()) + " it will be");
        mWindSpeedValue.setText(mHourWeather.getWindSpeed());
        mSummaryLabel.setText(mHourWeather.getSummary());
        mTemperatureMin.setText("" + round(mHourWeather.getTemperatureMin()));
        mTemperatureMax.setText("" + round(mHourWeather.getTemperatureMax()));

        // Change icon on the top left.
        Drawable icon = getResources().getDrawable(mHourWeather.getIconId());
        mIconImageView.setImageDrawable(icon);

        // Changes Background.
        Drawable background = getResources().getDrawable(mHourWeather.changeBackground());
        mBackground.setBackground(background);
    }

    private CurrentWeather getCurrentWeatherData(String jsonData) throws JSONException {
        // Create new CurrentWeather object. We'll be returning this.
        CurrentWeather currentWeather = new CurrentWeather();
        // Create the JSONobject from developer.forecast.io as forecast object.
        JSONObject forecast = new JSONObject(jsonData);
        // If you check the JSON, you'll find multiple data tuple pairs with first elements as quotes and second element as data.
        // We can extract all data by calling it directly by the name printed on the JSON site.
        // See example below for details.

        // A new JSONObject made from a JSONObject that was in JSONObject forecast.
        // JSONobjects used are: currently, daily, data
        JSONObject currently = forecast.getJSONObject("currently");
        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray dataDaily = daily.getJSONArray("data");

        // Compile all data.
        long currentlyTime = currently.getLong("time");
        double currentlyHumidity = currently.getDouble("humidity");
        double currentlyPrecip = currently.getDouble("precipProbability");
        double currentlyTemperature = currently.getDouble("temperature");
        String currentlySummary = currently.getString("summary");
        String currentlyIcon = currently.getString("icon");
        String currentlyTimezone = forecast.getString("timezone");
        String currentlyWindSpeed = currently.getString("windSpeed");

        JSONObject day0 = dataDaily.getJSONObject(0);
        double currentlyTemperatureMin = day0.getDouble("temperatureMin");
        double currentlyTemperatureMax = day0.getDouble("temperatureMax");

        // Store all of weather data from JSONArray Hourly.
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray dataHourly = hourly.getJSONArray("data");
        for (int hour = 0; hour < 25; hour++){
            JSONObject dayInput = dataHourly.getJSONObject(hour);
            jsonArray[hour] = dayInput;
        }

        // Now take everything from currently and put it into currentWeather.
        currentWeather.setTime(currentlyTime);
        currentWeather.setHumidity(currentlyHumidity);
        currentWeather.setPrecipChance(currentlyPrecip);
        currentWeather.setTemperature(currentlyTemperature);
        currentWeather.setSummary(currentlySummary);
        currentWeather.setIcon(currentlyIcon);
        currentWeather.setTimeZone(currentlyTimezone);
        currentWeather.setWindSpeed(currentlyWindSpeed);
        currentWeather.setTemperatureMin(currentlyTemperatureMin);
        currentWeather.setTemperatureMax(currentlyTemperatureMax);
        return currentWeather;
    }

    // For requesting hourly weather data
    // Pulls JSONObjects from jsonArray, which is instantiated in getCurrentWeatherData(), which
    // is always initialized at every refresh request / beginning of app.
    // Utilizes CurrentWeather class.
    // Used in the Hourly Seekbar.
    private CurrentWeather getHourWeatherData(int selectedHour) throws JSONException {
        CurrentWeather hourWeather = new CurrentWeather();
        //Take JSONObjects from jsonArray and insert it into hourWeather.
        JSONObject jsonHour = jsonArray[selectedHour];
        // Compile all data.
        long jsonHourTime = jsonHour.getLong("time");
        double jsonHourHumidity = jsonHour.getDouble("humidity");
        double jsonHourPrecip = jsonHour.getDouble("precipProbability");
        double jsonHourTemperature = jsonHour.getDouble("temperature");
        String jsonHourSummary = jsonHour.getString("summary");
        String jsonHourIcon = jsonHour.getString("icon");
//        String jsonHourTimezone = forecast.getString("timezone"); // Timezone will be taken from getWeatherData() instead.
        String jsonHourWindSpeed = jsonHour.getString("windSpeed");

        hourWeather.setTime(jsonHourTime);
        hourWeather.setHumidity(jsonHourHumidity);
        hourWeather.setPrecipChance(jsonHourPrecip);
        hourWeather.setTemperature(jsonHourTemperature);
        hourWeather.setSummary(jsonHourSummary);
        hourWeather.setIcon(jsonHourIcon);
        hourWeather.setTimeZone(mCurrentWeather.getTimeZone()); // Timezone taken from getWeatherData().
        hourWeather.setWindSpeed(jsonHourWindSpeed);

        hourWeather.setTemperatureMin(mCurrentWeather.getTemperatureMin()); // Min temperature remains the same for the day in question.
        hourWeather.setTemperatureMax(mCurrentWeather.getTemperatureMax()); // Max temperature remains the same.
        return hourWeather;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    private void hourSeekBar(){
        Log.v(TAG, "Open Dialogue for Hour Seek Bar.");
        final Dialog hourSeekBarDialog = new Dialog(this);
        hourSeekBarDialog.setTitle("Set hour of your choice:");
        hourSeekBarDialog.setContentView(R.layout.hourseekbar_dialog);
        final TextView hour = (TextView) hourSeekBarDialog.findViewById(R.id.hour);
        final SeekBar hourSeekBar = (SeekBar)hourSeekBarDialog.findViewById(R.id.hour_seek);
        hourSeekBar.setMax(24);
        hourSeekBar.setProgress(0);
        hourSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String displayedHour = "In the next " + Integer.toString(i) + " hour(s)";
                hour.setText("" + displayedHour);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        Button button = (Button) hourSeekBarDialog.findViewById(R.id.hour_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedHour = hourSeekBar.getProgress();
                try {
                    mHourWeather = getHourWeatherData(selectedHour);
                    updateDisplayHourly();
                    toastDisplayText("Now displaying selected hour.");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hourSeekBarDialog.dismiss();
            }
        });

        Button buttonCancel = (Button) hourSeekBarDialog.findViewById(R.id.hour_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                hourSeekBarDialog.dismiss();
            }
        });
        hourSeekBarDialog.show();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        boolean networkAvailable = false;
        // Check if it is present.
        if (networkinfo != null && networkinfo.isConnected()){
            networkAvailable = true;
        }
        return networkAvailable;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        toastDisplayText("Network Error - unable to connect to GPS.");
    }
}
