package ch.bfh.fbi.mobiComp.PeopleTag.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import ch.bfh.fbi.mobiComp.PeopleTag.gui.MainActivity;
import ch.bfh.fbi.mobiComp.PeopleTag.gui.PeopleTagApplication;
import ch.bfh.fbi.mobiComp.PeopleTag.model.UserData;
import ch.bfh.fbi.mobiComp.PeopleTag.tasks.UserUpdateTask;

public class GeoTracker extends Service implements LocationListener {

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // Actual active provider
    String actualActiveProvider = "";

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GeoTracker()
    {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLocation();
        return super.onStartCommand(intent, flags, startId);
    }


    public Location getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                showSettingsAlert();
                stopSelf();
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            updateLocationOnMain(location);
                        }
                        actualActiveProvider = LocationManager.NETWORK_PROVIDER;
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                updateLocationOnMain(location);
                            }
                        }
                        actualActiveProvider = LocationManager.GPS_PROVIDER;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GeoTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                getApplicationContext().startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void updateLocationOnMain (final Location location)
    {
        Intent IntentLocationUpdate = new Intent(MainActivity.LOCATION_UPDATE);
        IntentLocationUpdate.putExtra("location",location);
        getApplicationContext().sendBroadcast(IntentLocationUpdate);

        String userID = ((PeopleTagApplication)getApplication()).getUserID();
        String displayName = ((PeopleTagApplication)getApplication()).getDisplayName();

        if (userID != null && userID.length() > 0)
        {
            UserUpdateTask userUpdateTask = new UserUpdateTask(userID, displayName, location) {
                @Override
                public void onPostExecute(UserData result) {
                    Log.d("Update Task", "User position updated");
                }

                @Override
                public void onPreExecute() {

                }

                @Override
                public void onProgressUpdate(Integer... values) {

                }
            };
            userUpdateTask.execute();
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
        updateLocationOnMain(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equalsIgnoreCase(actualActiveProvider))
        {
            updateLocationOnMain(null);
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
