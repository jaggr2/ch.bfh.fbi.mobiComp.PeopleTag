package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import ch.bfh.fbi.mobiComp.PeopleTag.R;
import ch.bfh.fbi.mobiComp.PeopleTag.service.GeoTracker;
import ch.bfh.fbi.mobiComp.PeopleTag.tasks.UserInfoDownloader;

public class MainActivity extends Activity {

    public final static String LOCATION_UPDATE = "LocationUpdate";
    private Location actualLoc = null;
    private Handler handler = new Handler();
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getParcelableExtra("location") instanceof Location) {

                actualLoc = intent.getParcelableExtra("location");
                final ListView listView = (ListView) findViewById(R.id.user_list);

                listView.invalidate();

                Toast.makeText(context, actualLoc.toString(),
                        Toast.LENGTH_SHORT).show();
            }
            else if (intent.getParcelableExtra("location") == null)
            {
                Intent serviceIntent = new Intent(context, GeoTracker.class);
                stopService(serviceIntent);
                startService(serviceIntent);
            }
        }
    };

    private Runnable serverUpdate = new Runnable() {
        @Override
        public void run() {
            new UserInfoDownloader(MainActivity.getInstance()).execute();
            handler.postDelayed(serverUpdate, 30 * 1000);
        }
    };

    MediaPlayer mySound;
    GeoTracker gps;

    private static MainActivity singleton;

    // Returns the application instance
    public static MainActivity getInstance() {
        return singleton;
    }

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        singleton = this;

        mySound = MediaPlayer.create(MainActivity.this,R.raw.sonar);

        setContentView(R.layout.main);
        // TODO LAN Need to Refresh the User Info from time to time -> is move to
        // GeoPositionListener onPositionChange a good idea???
        // Maybe tooMuch dataTransfer between Client/Server because the data often changes
        // but it would be the best Solution when our position changes we need to be sure
        // the others are at the current locations to display valid data...
        // Sure the app should have a refresh button to updates myLocation and the friendslocation...
        UserInfoDownloader userInfoDownloader = new UserInfoDownloader(this);
        userInfoDownloader.execute();

        IntentFilter updateRecive = new IntentFilter();
        updateRecive.addAction(LOCATION_UPDATE);
        registerReceiver(receiver, updateRecive);

        // Current Location should be update everytime...
        //gps = new GeoTracker(MainActivity.this);
        startService(new Intent(this, GeoTracker.class));


        handler.postDelayed(serverUpdate, 30 * 1000);
        //if(!gps.canGetLocation()){
        //    gps.showSettingsAlert();
        //}
    }

    // Should only used to force a location Update...
    public void registerPosition()
    {
        if (actualLoc != null) {
            Toast.makeText(this, "Position Registered: Longitude: " + actualLoc.getLongitude() + " Latitude: " + actualLoc.getLatitude(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        IntentFilter updateRecive = new IntentFilter();
        updateRecive.addAction(LOCATION_UPDATE);
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

    @Override
    protected void onDestroy() {

        stopService(new Intent(this, GeoTracker.class));
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_search:


                  UserInfoDownloader userInfoDownloader = new UserInfoDownloader(this);
                  userInfoDownloader.execute();

                  return true;
//            case R.id.menuitem_send:
            //                if(!mySound.isPlaying()) {
//                    mySound.start();
//                }
//                registerPosition();
//                return true;
            case R.id.menuitem_add:
                Intent launchAddUser = new Intent(MainActivity.this,AddUserActivity.class);

                startActivityForResult(launchAddUser, 0);
                return true;

            case R.id.menuitem_setup:
                Intent launchNewIntent = new Intent(MainActivity.this,SetupActivity.class);

                startActivityForResult(launchNewIntent, 0);
                return true;

            case R.id.menuitem_quit:
//                Toast.makeText(this, getString(R.string.ui_menu_quit),
//                        Toast.LENGTH_SHORT).show();
                finish(); // close the activity
                return true;
        }
        return false;
    }

    public Location getActualLocation()
    {
        return this.actualLoc;
    }

}
