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
        button.setText(((PeopleTagApplication)getApplication()).getUserID().length() > 0 ? "Update" : "Register");

        final ProgressBar progressBarSetup = (ProgressBar) findViewById(R.id.progressBarSetup);
        progressBarSetup.setVisibility(View.INVISIBLE);

        final EditText editText = (EditText)this.findViewById(R.id.editTextDisplayName);
        editText.setText(((PeopleTagApplication)getApplication()).getDisplayName());

        final TextView textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setText(((PeopleTagApplication)getApplication()).getUserID().length() > 0 ? "Update your user" : "Register as new User");

        final TextView textViewSubTitle = (TextView) findViewById(R.id.textViewSubTitle);
        textViewSubTitle.setText(((PeopleTagApplication)getApplication()).getUserID().length() > 0 ? "Your ID: " + ((PeopleTagApplication)getApplication()).getUserID() : "Your ID: none");
        textViewSubTitle.setVisibility(((PeopleTagApplication)getApplication()).getUserID().length() > 0 ? View.VISIBLE : View.INVISIBLE);
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

        final String originalId = ((PeopleTagApplication)getApplication()).getUserID();

        UserUpdateTask userUpdateTask = new UserUpdateTask(originalId, editText.getText().toString(), loc) {
            @Override
            public void onPostExecute(UserData result) {
                if(result != null) {

                    ((PeopleTagApplication)getApplication()).setUserData(result);

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
