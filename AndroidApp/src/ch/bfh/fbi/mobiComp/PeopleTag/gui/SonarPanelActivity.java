package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.*;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import ch.bfh.fbi.mobiComp.PeopleTag.R;
import android.content.Context;
import ch.bfh.fbi.mobiComp.PeopleTag.model.UserData;
import ch.bfh.fbi.mobiComp.PeopleTag.service.GeoTracker;
import ch.bfh.fbi.mobiComp.PeopleTag.tasks.UserInfoDownloader;

/**
 * Created by heroku on 27.03.14.
 */
public class SonarPanelActivity extends Activity implements View.OnClickListener,SensorEventListener{
    private boolean mInitialized;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private final float NOISE = (float) 2.0;
    MediaPlayer mySound;
    private Handler handler = new Handler();
    private Location currentLocation;
    private SonarPanelView sonarView;

    public UserData getLastUserData() {
        return lastUserData;
    }

    public void setLastUserData(UserData lastUserData) {
        this.lastUserData = lastUserData;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    private String userid;
    private UserData lastUserData;

    private static SonarPanelActivity singleton;

    private Runnable serverUpdate = new Runnable() {
        @Override
        public void run() {
            new UserInfoDownloader(SonarPanelActivity.getInstance()).execute();
            handler.postDelayed(serverUpdate, 30 * 1000);
        }
    };

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getParcelableExtra("location") instanceof Location) {
                currentLocation = intent.getParcelableExtra("location");
            }
            else if (intent.getParcelableExtra("location") == null)
            {
                Intent serviceIntent = new Intent(context, GeoTracker.class);
                stopService(serviceIntent);
                startService(serviceIntent);
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sonarpanel);
        mySound = MediaPlayer.create(SonarPanelActivity.this, R.raw.sonar);

        // TODO Lan Beschleunigungssensor verwenden...
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Clicklistener auf view adden...
        sonarView = (SonarPanelView) findViewById(R.id.sonarview);
        sonarView.setOnClickListener(this);

        IntentFilter updateRecive = new IntentFilter();
        updateRecive.addAction(MainActivity.LOCATION_UPDATE);
        registerReceiver(receiver, updateRecive);

        // Get initial Location....
        GeoTracker geoTracker = new GeoTracker();
        currentLocation = geoTracker.getLocation();
        sonarView.setCurrent(currentLocation);

        handler.postDelayed(serverUpdate, 30 * 1000);

        String userId;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userId= null;
            } else {
                userId= extras.getString("user");
            }
        } else {
            userId= (String) savedInstanceState.getSerializable("user");
        }
        this.userid = userId;

        UserInfoDownloader userInfoDownloader = new UserInfoDownloader(this);
        userInfoDownloader.execute();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    // TODO Lan beschleunigungssensor verwenden...
        // If we don't have a Location, we break out
      /*  if ( LocationObj == null ) return;

        float azimuth = event.values[0];
        float baseAzimuth = azimuth;

        GeomagneticField geoField = new GeomagneticField( Double
                .valueOf( LocationObj.getLatitude() ).floatValue(), Double
                .valueOf( LocationObj.getLongitude() ).floatValue(),
                Double.valueOf( LocationObj.getAltitude() ).floatValue(),
                System.currentTimeMillis() );

        azimuth -= geoField.getDeclination(); // converts magnetic north into true north

        // Store the bearingTo in the bearTo variable
        float bearTo = LocationObj.bearingTo( destinationObj );

        // If the bearTo is smaller than 0, add 360 to get the rotation clockwise.
        if (bearTo < 0) {
            bearTo = bearTo + 360;
        }

        //This is where we choose to point it
        float direction = bearTo - azimuth;

        // If the direction is smaller than 0, add 360 to get the rotation clockwise.
        if (direction < 0) {
            direction = direction + 360;
        }

        rotateImageView( arrow, R.drawable.arrow, direction );

        //Set the field
        String bearingText = "N";

        if ( (360 >= baseAzimuth && baseAzimuth >= 337.5) || (0 <= baseAzimuth && baseAzimuth <= 22.5) ) bearingText = "N";
        else if (baseAzimuth > 22.5 && baseAzimuth < 67.5) bearingText = "NE";
        else if (baseAzimuth >= 67.5 && baseAzimuth <= 112.5) bearingText = "E";
        else if (baseAzimuth > 112.5 && baseAzimuth < 157.5) bearingText = "SE";
        else if (baseAzimuth >= 157.5 && baseAzimuth <= 202.5) bearingText = "S";
        else if (baseAzimuth > 202.5 && baseAzimuth < 247.5) bearingText = "SW";
        else if (baseAzimuth >= 247.5 && baseAzimuth <= 292.5) bearingText = "W";
        else if (baseAzimuth > 292.5 && baseAzimuth < 337.5) bearingText = "NW";
        else bearingText = "?";

        fieldBearing.setText(bearingText);*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Lan beschleundigungssensor verwenden...
    }

    @Override
    public void onClick(View v) {
        if(!mySound.isPlaying()){
            mySound.start();
        }
        refresh(lastUserData);
    }

    @Override
    protected void onResume() {
        IntentFilter updateRecive = new IntentFilter();
        updateRecive.addAction(MainActivity.LOCATION_UPDATE);
        registerReceiver(receiver, updateRecive);
        handler.postDelayed(serverUpdate, 30 * 1000);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        handler.removeCallbacks(serverUpdate);
        super.onPause();
    }

    public void refresh(UserData data){
         sonarView.refreshUserLocation(currentLocation,data);
    }

    // Returns the application instance
    public static SonarPanelActivity getInstance() {
        return singleton;
    }



}