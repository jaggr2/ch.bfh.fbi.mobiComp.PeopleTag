package ch.bfh.fbi.mobiComp.PeopleTag;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        new UserInfoDownloader(this).execute();

    }


    public void registerPosition()
    {
        Toast.makeText(this, "Position Registered", Toast.LENGTH_SHORT).show();
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
                PeopleSearch peopleSearchFragment = new PeopleSearch();//(PeopleSearch) getFragmentManager().findFragmentById(R.id.people_search_fragment);

                MediaPlayer mySound = MediaPlayer.create(MainActivity.this,R.raw.sonar);
                mySound.start();
                
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, peopleSearchFragment);
                // standard transition animation
                ft.setTransition(FragmentTransaction.
                        TRANSIT_FRAGMENT_FADE);
                // enable reverting the Fragment change via the back button
                ft.addToBackStack(null); // conserve previous old details fragment
                ft.commit(); // schedule transaction
                return true;
            case R.id.menuitem_send:
                registerPosition();
                return true;
            case R.id.menuitem_add:
                Toast.makeText(this, getString(R.string.ui_menu_add),
                        Toast.LENGTH_SHORT).show();
                return true;

            case R.id.menuitem_quit:
                Toast.makeText(this, getString(R.string.ui_menu_quit),
                        Toast.LENGTH_SHORT).show();
                finish(); // close the activity
                return true;
        }
        return false;
    }

}
