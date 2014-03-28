package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import ch.bfh.fbi.mobiComp.PeopleTag.R;
import ch.bfh.fbi.mobiComp.PeopleTag.model.UserData;
import ch.bfh.fbi.mobiComp.PeopleTag.tasks.UserUpdateTask;

public class SetupActivity extends Activity {

    private static SetupActivity instance;

    public static SetupActivity getInstance() {
        if(instance == null) {
            instance = new SetupActivity();
        }
        return instance;
    }

    public static final String PREFS_NAME = "PeopleTagUser";
    public static final String PREF_ID = "ID";
    public static final String PREF_DISPLAYNAME = "displayName";
    private SharedPreferences settings = null;

    public String getUserID() {
        return (settings != null ? this.settings.getString(PREF_ID, "") : "");
    }

    public String getDisplayName() {
        return (settings != null ? this.settings.getString(PREF_DISPLAYNAME, "") : "");
    }

    /**
     * Called when the activity is first created.
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);

        settings = getSharedPreferences(PREFS_NAME, 0);

        final Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                registerUser(v);
            }
        });
        button.setText((getUserID().length() > 0 ? "Update" : "Register"));

        final ProgressBar progressBarSetup = (ProgressBar) findViewById(R.id.progressBarSetup);
        progressBarSetup.setVisibility(View.INVISIBLE);

        final EditText editText = (EditText)this.findViewById(R.id.editTextDisplayName);
        editText.setText(getDisplayName());

        final TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText((getUserID().length() > 0 ? "Update your user" : "Register as new User"));

        final TextView textViewSubTitle = (TextView) findViewById(R.id.textViewSubTitle);
        textViewSubTitle.setText((getUserID().length() > 0 ? "Your ID: " + getUserID() : "Your ID: none"));
        textViewSubTitle.setVisibility((getUserID().length() > 0 ? View.VISIBLE : View.INVISIBLE));
    }

    @Override
    protected void onResume() {
        super.onResume();

        MainActivity mainActivity = MainActivity.getInstance();
        Location loc = (mainActivity.getActualLocation() != null ? mainActivity.getActualLocation() : null);

        if(loc != null) {
            final TextView textViewLongitudeValue = (TextView) findViewById(R.id.textViewLongitudeValue);
            textViewLongitudeValue.setText(String.valueOf(loc.getLongitude()));

            final TextView textViewLatitudeValue = (TextView) findViewById(R.id.textViewLatitudeValue);
            textViewLatitudeValue.setText(String.valueOf(loc.getLatitude()));

            final TextView textViewAccuracyValue = (TextView) findViewById(R.id.textViewAccuracyValue);
            textViewAccuracyValue.setText(String.valueOf(loc.getAccuracy()) + "m");

            final TextView textViewProviderValue = (TextView) findViewById(R.id.textViewProviderValue);
            textViewProviderValue.setText(loc.getProvider());
        }
    }

    public void registerUser(View view) {

        final EditText editText = (EditText)this.findViewById(R.id.editTextDisplayName);

        if (editText.getText() == null || editText.getText().length() < 1) {
            Toast.makeText(this, getString(R.string.ui_setup_noname),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        MainActivity mainActivity = MainActivity.getInstance();

        Location loc = (mainActivity.getActualLocation() != null ? mainActivity.getActualLocation() : null);

        final String originalId = getUserID();

        UserUpdateTask userUpdateTask = new UserUpdateTask(originalId, editText.getText().toString(), loc) {
            @Override
            public void onPostExecute(UserData result) {
                if(result != null) {
                    // We need an Editor object to make preference changes.
                    // All objects are from android.context.Context
                    SharedPreferences.Editor editor = settings.edit();

                    editor.putString(PREF_ID, result.getId());
                    editor.putString(PREF_DISPLAYNAME, result.getDisplayName());

                    // Commit the edits!
                    editor.commit();

                    finish();
                }

                final Button button = (Button) findViewById(R.id.button2);
                button.setVisibility(View.VISIBLE);

                final ProgressBar progressBarSetup = (ProgressBar) findViewById(R.id.progressBarSetup);
                progressBarSetup.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPreExecute() {
                final Button button = (Button) findViewById(R.id.button2);
                button.setVisibility(View.GONE);

                final ProgressBar progressBarSetup = (ProgressBar) findViewById(R.id.progressBarSetup);
                progressBarSetup.setVisibility(View.VISIBLE);
                progressBarSetup.setMax(100);
                progressBarSetup.setProgress(0);
            }

            @Override
            public void onProgressUpdate(Integer... values) {
                final ProgressBar progressBarSetup = (ProgressBar) findViewById(R.id.progressBarSetup);
                progressBarSetup.setProgress(values[0]);
            }
        };

        // start Task
        userUpdateTask.execute();
    }
}
