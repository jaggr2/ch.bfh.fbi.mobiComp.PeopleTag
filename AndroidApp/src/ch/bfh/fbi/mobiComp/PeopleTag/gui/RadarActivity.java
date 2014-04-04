/*
 * Copyright (C) 2008 Google Inc.
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

package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.app.Activity;
import android.content.*;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import ch.bfh.fbi.mobiComp.PeopleTag.R;
import ch.bfh.fbi.mobiComp.PeopleTag.model.UserData;

/**
 * Simple Activity wrapper that hosts a {@link RadarView}
 *
 */
public class RadarActivity extends Activity {

    private SensorManager mSensorManager;

    private RadarView mRadar;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getParcelableExtra("location") instanceof Location) {

                Location location = intent.getParcelableExtra("location");
                mRadar.setLocation(location);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.radar);
        mRadar = (RadarView) findViewById(R.id.radar);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        // Metric units
        mRadar.setUseMetric(true);


        IntentFilter updateRecive = new IntentFilter();
        updateRecive.addAction(MainActivity.LOCATION_UPDATE);
        registerReceiver(receiver, updateRecive);

        // Read the target from our intent
        //Intent i = getIntent();
        //int latE6 = (int)(i.getFloatExtra("latitude", 0) * GeoUtils.MILLION);
        //int lonE6 = (int)(i.getFloatExtra("longitude", 0) * GeoUtils.MILLION);

        //mySound = MediaPlayer.create(SonarPanelActivity.this, R.raw.sonar);



        // Clicklistener auf view adden...
        //sonarView = (SonarPanelView) findViewById(R.id.sonarview);
        //sonarView.setOnClickListener(this);


        // handler.postDelayed(serverUpdate, 30 * 1000);

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


        System.out.println("userid"+userId);
        UserData user = ((PeopleTagApplication)getApplication()).getUserById(userId);
        System.out.println("asfdasfs"+user);


        mRadar.setTarget((int)user.getLatitude()* GeoUtils.MILLION, (int)user.getLongitude()* GeoUtils.MILLION);
        mRadar.setDistanceView((TextView) findViewById(R.id.distance));
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();

        // Get initial Location....
        MainActivity mainActivity = MainActivity.getInstance();
        Location currentLocation = (mainActivity.getActualLocation() != null ? mainActivity.getActualLocation() : null);
        mRadar.setLocation(currentLocation);


        mSensorManager.registerListener(mRadar, SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_GAME);
 
        // Start animating the radar screen
        mRadar.startSweep();


        
        // Register for location updates
        //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
        //        LOCATION_UPDATE_INTERVAL_MILLIS, 1, mRadar);
        //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
        //        LOCATION_UPDATE_INTERVAL_MILLIS, 1, mRadar);
    }

    @Override
    protected void onPause()
    {
        mSensorManager.unregisterListener(mRadar);
        //mLocationManager.removeUpdates(mRadar);
        
        // Stop animating the radar screen
        mRadar.stopSweep();
        super.onStop();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_STANDARD, 0, R.string.menu_standard)
                .setIcon(R.drawable.ic_menu_standard)
                .setAlphabeticShortcut('A');
        menu.add(0, MENU_METRIC, 0, R.string.menu_metric)
                .setIcon(R.drawable.ic_menu_metric)
                .setAlphabeticShortcut('C');
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_STANDARD: {
            setUseMetric(false);
            return true;
        }
        case MENU_METRIC: {
            setUseMetric(true);
            return true;
        }
        }

        return super.onOptionsItemSelected(item);
    }
    */
}