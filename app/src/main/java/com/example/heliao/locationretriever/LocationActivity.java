package com.example.heliao.locationretriever;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by heliao on 10/11/17.
 */

public class LocationActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = LocationActivity.class.getSimpleName();

    LocationManager mLocationManager;
    boolean mIsGPSEnabled;
    boolean mIsNetworkEnabled;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 mill
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);

        mContext = this;

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // getting GPS status
        mIsGPSEnabled = mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        mIsNetworkEnabled = mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    @OnClick(R.id.start_button)
    void startLocationUpdate() {
        Log.i(TAG, "startLocationUpdate: ");
        try {
            if (mIsGPSEnabled) {
                Log.i(TAG, "GPS Enabled");
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            } else if (mIsNetworkEnabled) {
                Log.i(TAG, "Network Enabled");
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }
        } catch (SecurityException e) {

        }
    }

    @OnClick(R.id.stop_button)
    void stopLocationUpdate() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged");
        Log.i(TAG, "Latitude " + location.getLatitude());
        Log.i(TAG, "Longitude " + location.getLongitude());

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
