package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ch.bfh.fbi.mobiComp.PeopleTag.R;
import ch.bfh.fbi.mobiComp.PeopleTag.tasks.UserRegisterTask;

public class SetupActivity extends Activity {

    public static final String PREFS_NAME = "PeopleTagUser";

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

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String id = settings.getString("ID", "");

        if(id != null && id.length() > 0) {
            TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
            textViewTitle.setText("ID" + id);
        }
    }

    public void registerUser(View view) {

        EditText editText = (EditText) findViewById(R.id.editTextDisplayName);
        //String value = editText.getText().toString();

        if (editText.getText() == null || editText.getText().length() < 1) {
            Toast.makeText(this, getString(R.string.ui_setup_noname),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        MainActivity mainActivity = MainActivity.getInstance();

        Location loc = (mainActivity.getActualLocation() != null ? mainActivity.getActualLocation() : null);

        UserRegisterTask userRegisterTask = new UserRegisterTask(this, loc);
        userRegisterTask.execute();
        // start Task
    }

}
