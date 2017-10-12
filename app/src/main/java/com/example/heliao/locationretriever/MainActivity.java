package com.example.heliao.locationretriever;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int PERMISSION_REQUEST = 1000;

    private LocationCallback mLocationCallback;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        checkPermissions(this, PERMISSION_REQUEST);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.i(TAG, "location latitude: " + location.getLatitude());
                    Log.i(TAG, "location longitude: " + location.getLongitude());
                }
            }
        };


        Button startButton = findViewById(R.id.start_button);
        Button stopButton = findViewById(R.id.stop_button);
        Button nextButton = findViewById(R.id.next);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: start button");
                startRetrieveLocation();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: stop button");
                stopRetrieveLocation();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, LocationActivity.class));
            }
        });

    }

    public void startRetrieveLocation() {
        Log.i(TAG, "startRetrieveLocation: ");
        try {
            mFusedLocationClient.requestLocationUpdates(createLocationRequest(),
                    mLocationCallback,
                    null /* Looper */);

        } catch (SecurityException e) {

        }
    }

    public void stopRetrieveLocation() {
        Log.i(TAG, "stopRetrieveLocation: ");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    class LocationTask implements Runnable {
        OnSuccessListener mListener;
        FusedLocationProviderClient mClient;

        LocationTask(FusedLocationProviderClient client, OnSuccessListener listener) {
            mListener = listener;
            mClient = client;
        }

        @Override
        public void run() {
            try {
                Log.i(TAG, "run");
                mClient.getLastLocation().addOnSuccessListener(mListener);
            } catch (SecurityException e) {

            }
        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    public boolean checkPermissions(Activity context, int permissionRequestId) {
        Log.i(TAG, "checkPermissions()");
        List<String> listPermissionNeeded = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context,
                    listPermissionNeeded.toArray(new String[listPermissionNeeded.size()]), permissionRequestId);
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult()");
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // permissions were previously denied and never ask again is checked
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                        new AlertDialog.Builder(this)
                                .setMessage("Location Access is critical for Sfara's functionality. Please give the permission in the system's settings page")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                                    }
                                }).show();

                    } else {
                        // show permission rationale
                        new AlertDialog.Builder(this)
                                .setMessage("Location Access is critical for Sfara's functionality. Please click button below to give Sfara Location Access")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                }
            }
            return;
        }
    }
}
