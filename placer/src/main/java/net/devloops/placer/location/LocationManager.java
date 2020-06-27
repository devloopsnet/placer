package net.devloops.placer.location;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.model.LatLng;

import net.devloops.placer.util.Logger;
import net.devloops.placer.util.PermissionUtil;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.SocketTimeoutException;

/**
 * @author Odey M. Khalaf <odey@devloops.net>
 */
public class LocationManager implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 111;
    private static final int REQUEST_CHECK_PERMISSION = 222;
    // Location updates intervals in sec
    private static final int UPDATE_INTERVAL = 10000; // 10 sec
    private static final int FATEST_INTERVAL = 5000; // 5 sec
    private static final int DISPLACEMENT = 10; // 10 meters

    private GoogleApiClient mGoogleApiClient;
    private AppCompatActivity activity;
    private LocationRequest mLocationRequest;

    private LocationListener locationListener;
    private final String TAG = "LocationManager";

    public LocationManager(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void triggerLocation(LocationListener locationListener) {
        this.locationListener = locationListener;
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK_PERMISSION);
            } else if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CHECK_PERMISSION);
            } else if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK_PERMISSION);
            } else {
                init();
            }

        } else
            init();
    }

    private void init() {
        if (checkPlayServices()) {

            buildGoogleApiClient();
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        } else {

            if (locationListener != null)
                locationListener.onFail(LocationListener.Status.NO_PLAY_SERVICE);
        }
    }

    /**
     * Creating google api client object
     */
    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApiIfAvailable(LocationServices.API).build();
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }

    /**
     * Starting the location updates
     */
    public void startLocationUpdates() {
        if (!checkPermission()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Method to display the location on UI
     */

    public void getLocation() throws SocketTimeoutException, ConnectTimeoutException {
        boolean isFreshLocation = true;
        if (isFreshLocation) {
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            startLocationUpdates();
        } else {
            if (!checkPermission()) {
                Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    double latitude = mLastLocation.getLatitude();
                    double longitude = mLastLocation.getLongitude();
                    Logger.e(TAG, "LAST Location " + latitude + " : " + longitude);
                    if (locationListener != null)
                        locationListener.onLocationAvailable(new LatLng(latitude, longitude));
                } else {
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    startLocationUpdates();
                }
            }
        }
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(activity);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(activity, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }
        return true;
    }

    public void checkLocationEnable() {
        LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        locationSettingsRequestBuilder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequestBuilder.build());
        result.setResultCallback(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Logger.e(TAG, "onConnected");
        createLocationRequest();
        checkLocationEnable();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.e(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.e(TAG, "onConnectionFailed ");
    }

    @Override
    public void onLocationChanged(Location location) {
        stopLocationUpdates();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        Logger.e(TAG, "onLocationChanged " + location.getLatitude() + " : " + location.getLongitude());
        if (locationListener != null)
            try {
                locationListener.onLocationAvailable(new LatLng(location.getLatitude(), location.getLongitude()));
            } catch (ConnectTimeoutException | SocketTimeoutException e) {
                e.printStackTrace();
            }
    }

    public void stop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        Status status = locationSettingsResult.getStatus();
        Logger.e(TAG, "Location Setting " + status.hasResolution());
        if (status.hasResolution()) {
            Toast.makeText(activity, "Please Enable the Location service ", Toast.LENGTH_SHORT).show();
            try {
                status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }

        } else {
            try {
                getLocation();
            } catch (SocketTimeoutException | ConnectTimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    getLocation();
                } catch (SocketTimeoutException | ConnectTimeoutException e) {
                    e.printStackTrace();
                }
            } else {
                if (locationListener != null)
                    locationListener.onFail(LocationListener.Status.DENIED_LOCATION_SETTING);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CHECK_PERMISSION) {
            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                init();
            } else {

                if (locationListener != null)
                    locationListener.onFail(LocationListener.Status.PERMISSION_DENIED);
            }
        }
    }

    public interface LocationListener {
        void onLocationAvailable(LatLng latLng) throws ConnectTimeoutException, SocketTimeoutException;

        void onFail(Status status);

        enum Status {
            PERMISSION_DENIED, NO_PLAY_SERVICE, DENIED_LOCATION_SETTING
        }
    }
}
