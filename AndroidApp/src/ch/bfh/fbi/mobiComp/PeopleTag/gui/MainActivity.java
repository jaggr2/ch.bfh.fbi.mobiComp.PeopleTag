package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import ch.bfh.fbi.mobiComp.PeopleTag.R;
import ch.bfh.fbi.mobiComp.PeopleTag.model.UserData;
import ch.bfh.fbi.mobiComp.PeopleTag.service.GeoTracker;
import ch.bfh.fbi.mobiComp.PeopleTag.tasks.UserInfoDownloader;
import ch.bfh.fbi.mobiComp.PeopleTag.tasks.UserPairTask;
import ch.bfh.fbi.mobiComp.PeopleTag.tasks.UserUpdateTask;

import java.util.ArrayList;

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
            reloadUsers();
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

    public void reloadUsers() {
        final MainActivity mHostActivity = this;

        UserInfoDownloader userInfoDownloader = new UserInfoDownloader((PeopleTagApplication) getApplication()) {
            @Override
            public void onPostExecute(Boolean result) {

                if(result != true) {
                    return;
                }

                //Update List in MainActivity

                final ListView listView = (ListView) mHostActivity.findViewById(R.id.user_list);

                final ArrayList<UserData> dataList = new ArrayList<UserData>();
                for(UserData userData : peopleTagApplication.getUsers()) {
                    dataList.add(userData);
                }

                listView.setAdapter(new UserDataAdapter(mHostActivity, R.layout.listitem, dataList));
                listView.setClickable(true);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        UserData o = (UserData) listView.getItemAtPosition(position);
                        Intent intent = new Intent(mHostActivity, SonarPanelActivity.class);
                        intent.putExtra("user", o.getId());
                        mHostActivity.startActivity(intent);
                    }
                });

                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                        final UserData userData = (UserData) listView.getItemAtPosition(arg2);

                        final String currentUserID = ((PeopleTagApplication)mHostActivity.getApplication()).getUserID();
                        if(currentUserID == null || currentUserID.length() <= 0) {
                            Toast.makeText(mHostActivity, "Please do setup first", Toast.LENGTH_LONG).show();
                            return false;
                        }

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == DialogInterface.BUTTON_POSITIVE) {

                                    UserPairTask userPairTask = new UserPairTask( currentUserID, userData.getId(), false) {
                                        @Override
                                        public void onPostExecute(Boolean result) {
                                            if(result) {
                                                Toast.makeText(mHostActivity, "Successfully removed paring with " + userData.getDisplayName() + " :)", Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                Toast.makeText(mHostActivity, "Error on removed paring with " + userData.getDisplayName() + " :(", Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onPreExecute() {

                                        }

                                        @Override
                                        public void onProgressUpdate(Integer... values) {

                                        }
                                    };
                                    userPairTask.execute();
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(mHostActivity);
                        builder.setMessage("Delete Paring with " + userData.getDisplayName() + "?").setPositiveButton("Delete", dialogClickListener)
                                .setNegativeButton("Cancel", dialogClickListener).show();
                        return false;
                    }
                });
            }

            @Override
            public void onPreExecute() {

            }

            @Override
            public void onProgressUpdate(Void... values) {

            }
        };

        userInfoDownloader.execute();
    }


    @Override
    protected void onResume() {
        IntentFilter updateRecive = new IntentFilter();
        updateRecive.addAction(LOCATION_UPDATE);
        registerReceiver(receiver, updateRecive);
        handler.postDelayed(serverUpdate, 30 * 1000);

        reloadUsers();

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
                  reloadUsers();

                  return true;

            case R.id.menuitem_add:
                Intent launchAddUser = new Intent(MainActivity.this,AddUserActivity.class);

                startActivityForResult(launchAddUser, 0);
                return true;

            case R.id.menuitem_setup:
                Intent launchNewIntent = new Intent(MainActivity.this,SetupActivity.class);
                startActivityForResult(launchNewIntent, 0);
                return true;

/*            case R.id.menuitem_quit:
                finish(); // close the activity
                return true; */
        }
        return false;
    }

    public Location getActualLocation()
    {
        return this.actualLoc;
    }
}
