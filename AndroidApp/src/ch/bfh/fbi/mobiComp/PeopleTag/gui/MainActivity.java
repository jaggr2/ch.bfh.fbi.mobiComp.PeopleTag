package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
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
    private UserInfoDownloader userInfoDownloader = new UserInfoDownloader(this);
    private Location actualLoc = null;
    private BroadcastReceiver receiver;

    /**
     * Called when the activity is first created.
     */

    GeoTracker gps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // TODO LAN Need to Refresh the User Info from time to time -> is move to
        // GeoPositionListener onPositionChange a good idea???
        // Maybe tooMuch dataTransfer between Client/Server because the data often changes
        // but it would be the best Solution when our position changes we need to be sure
        // the others are at the current locations to display valid data...
        // Sure the app should have a refresh button to updates myLocation and the friendslocation...
        userInfoDownloader.execute();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                 if (intent.getParcelableExtra("location") instanceof Location) {

                     actualLoc = intent.getParcelableExtra("location");
                     final ListView listView = (ListView) findViewById(R.id.user_list);

                     Toast.makeText(context, actualLoc.toString(),
                             Toast.LENGTH_SHORT).show();
                 }
            }
        };

        // Current Location should be update everytime...
        //gps = new GeoTracker(MainActivity.this);
        startService(new Intent(this, GeoTracker.class));
        //if(!gps.canGetLocation()){
        //    gps.showSettingsAlert();
        //}
    }


    // Should only used to force a location Update...
    public void registerPosition()
    {
        //Toast.makeText(this, "Position Registered: Longitude: " + gps.getLongitude() + " Latitude: " + gps.getLatitude(), Toast.LENGTH_SHORT).show();
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
                //PeopleSearchListFragment peopleSearchFragment = new PeopleSearchListFragment();//(PeopleSearch) getFragmentManager().findFragmentById(R.id.people_search_fragment);

                MediaPlayer mySound = MediaPlayer.create(MainActivity.this,R.raw.sonar);
                mySound.start();
                
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace(R.id.fragment_container, peopleSearchFragment);
//                // standard transition animation
//                ft.setTransition(FragmentTransaction.
//                        TRANSIT_FRAGMENT_FADE);
//                // enable reverting the Fragment change via the back button
//                ft.addToBackStack(null); // conserve previous old details fragment
//                ft.commit(); // schedule transaction
                return true;
            case R.id.menuitem_send:
                registerPosition();
                return true;
            case R.id.menuitem_add:
                Toast.makeText(this, getString(R.string.ui_menu_add),
                        Toast.LENGTH_SHORT).show();

            case R.id.menuitem_setup:
                Intent launchNewIntent = new Intent(MainActivity.this,SetupActivity.class);
                startActivityForResult(launchNewIntent, 0);
                return true;

            case R.id.menuitem_quit:
                Toast.makeText(this, getString(R.string.ui_menu_quit),
                        Toast.LENGTH_SHORT).show();
                finish(); // close the activity
                return true;
        }
        return false;
    }

    public GeoTracker getGeoTracker()
    {
        return this.gps;
    }

}
