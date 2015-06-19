package com.cooldog.stormyplus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static java.lang.Math.round;

/**
 * Created by Daniel on 5/25/2015.
 */
public class CurrentWeather {
    private String mIcon;
    private String mTimeZone;
    private double mLongitude;
    private double mLatitude;
    private long mTime;
    private double mTemperature;
    private double mHumidity;
    private double mPrecipChance;
    private String mSummary;
    private String mWindSpeed;

    private double mTemperatureMax;
    private String mTemperatureMaxTime;
    private double mTemperatureMin;
    private String mTemperatureMinTime;

    public String getFormattedTime(long unixInput){ // Method to convert UNIX time to 24hr Date.
        Date milliInput = new Date(unixInput * 1000L); // Convert milliseconds to seconds.
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(mTimeZone));
        String formattedTime = formatter.format(milliInput);
        return formattedTime;
    }

    public String getDate(long unixInput) { // This method converts UNIX time to a more detailed Date.
        Date milliInput = new Date(unixInput * 1000L);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        formatter.setTimeZone(TimeZone.getTimeZone(mTimeZone));
        String formattedDate = formatter.format(milliInput);
        return formattedDate;
    }

    // -----
    // Getters and setters below.
    public String getIcon() {
        return mIcon;
    }

    // Generate a valid int-based Drawable icon depending on what Icon given from forecast.io
    //  Icons: clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night
    public int getIconId() {
        int defaultIcon = R.drawable.clear_day;
        int iconId = defaultIcon;
        switch (mIcon){
            case "clear-day": iconId = R.drawable.clear_day; break;
            case "clear-night": iconId = R.drawable.clear_night; break;
            case "rain": iconId = R.drawable.rain; break;
            case "snow": iconId = R.drawable.snow; break;
            case "sleet": iconId = R.drawable.sleet; break;
            case "wind": iconId = R.drawable.wind; break;
            case "fog": iconId = R.drawable.cloudy; break;
            case "partly-cloudy-day": iconId = R.drawable.partly_cloudy; break;
            case "partly-cloudy-night": iconId = R.drawable.cloudy_night; break;
        }

        return iconId;
    }
    // Changes background depending on mIcon.
    public int changeBackground(){
        int background = R.drawable.background_cloudy_day;
        switch (mIcon){
            case "clear-day": background = R.drawable.background_sunny; break;
            case "clear-night": background = R.drawable.background_clear_night; break;
            case "rain": background = R.drawable.background_rain; break;
            case "snow": background = R.drawable.background_snow; break;
            case "sleet": background = R.drawable.background_sleet; break;
            case "wind": background = R.drawable.background_windy; break;
            case "fog": background = R.drawable.background_fog; break;
            case "partly-cloudy-day": background = R.drawable.background_cloudy_day; break;
            case "partly-cloudy-night": background = R.drawable.background_cloudy_night; break;
        }
        return background;
    }

    public long getTime() {
        return mTime;
    }

    public int getTemperature() {
        return (int) round(mTemperature);
    }

    public double getHumidity() {
        return mHumidity;
    }

    public int getPrecipChance() {
        return (int) (round(mPrecipChance * 100));
    }

    public String getSummary() {
        return mSummary;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public String getWindSpeed() {return mWindSpeed; }

    public double getLongitude() {
        return mLongitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getTemperatureMax() {
        return mTemperatureMax;
    }

    public String getTemperatureMaxTime() {
        return mTemperatureMaxTime;
    }

    public double getTemperatureMin() {
        return mTemperatureMin;
    }

    public String getTemperatureMinTime() {
        return mTemperatureMinTime;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public void setWindSpeed(String windSpeed) {
        mWindSpeed = windSpeed;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public void setTemperatureMax(double temperatureMax) {
        mTemperatureMax = temperatureMax;
    }

    public void setTemperatureMaxTime(String temperatureMaxTime) {
        mTemperatureMaxTime = temperatureMaxTime;
    }

    public void setTemperatureMin(double temperatureMin) {
        mTemperatureMin = temperatureMin;
    }

    public void setTemperatureMinTime(String temperatureMinTime) {
        mTemperatureMinTime = temperatureMinTime;
    }
}
