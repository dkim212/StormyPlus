package com.cooldog.stormyplus;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by Daniel on 6/6/2015.
 */
public class CurrentLocationByNetwork {
    final String gpsLocationProvider = LocationManager.GPS_PROVIDER;
    final String networkLocationProvider = LocationManager.NETWORK_PROVIDER;

    private double longitudeByNetwork;
    private double latitudeByNetwork;
    Context mContext;

    // Implements the Current Location feature, if the option is selected.
    // This function utilizes the in-built Network feature of the phone.
    public CurrentLocationByNetwork(Context inputContext){
        this.mContext = inputContext;
    }
    public void getLocation(){
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocationByNetwork = locationManager.getLastKnownLocation(networkLocationProvider);
        // Checks if lastKnownLocation are null, otherwise will send dud data.
        if (lastKnownLocationByNetwork == null) {
//            Log.v(TAG, "Location not available through Network.");
            longitudeByNetwork = 0.00;
            latitudeByNetwork = 0.00;
        } else {
            longitudeByNetwork = lastKnownLocationByNetwork.getLongitude();
            latitudeByNetwork = lastKnownLocationByNetwork.getLatitude();
        }
    }
    public double getLongitude(){
        return longitudeByNetwork;
    }
    public double getLatitude(){
        return latitudeByNetwork;
    }

}
