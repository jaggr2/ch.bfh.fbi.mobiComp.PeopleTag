package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ch.bfh.fbi.mobiComp.PeopleTag.R;
import ch.bfh.fbi.mobiComp.PeopleTag.service.GeoTracker;
import ch.bfh.fbi.mobiComp.PeopleTag.tasks.UserInfoDownloader;

public class SetupActivity extends Activity {


    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);

        final Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registerUser(v);
            }
        });
    }

    public void registerUser(View view) {

        EditText editText = (EditText) findViewById(R.id.editTextDisplayName);
        //String value = editText.getText().toString();

        if( editText.getText().length() < 1 ) {
            Toast.makeText(this, getString(R.string.ui_setup_noname),
                    Toast.LENGTH_SHORT).show();
            return;
        }



        // close the activity
        finish();
    }

}
