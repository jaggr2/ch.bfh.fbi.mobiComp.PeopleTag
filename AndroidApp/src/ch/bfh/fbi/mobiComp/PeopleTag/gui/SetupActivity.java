package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ch.bfh.fbi.mobiComp.PeopleTag.R;
import ch.bfh.fbi.mobiComp.PeopleTag.model.UserData;
import ch.bfh.fbi.mobiComp.PeopleTag.tasks.UserInfoDownloader;
import ch.bfh.fbi.mobiComp.PeopleTag.tasks.UserRegisterTask;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class SetupActivity extends Activity {


    private static final String TAG = "SetupActivity"; // for LogCat
    private UserRegisterTask userRegisterTask = new UserRegisterTask(this);

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

        if (editText.getText() == null || editText.getText().length() < 1) {
            Toast.makeText(this, getString(R.string.ui_setup_noname),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        userRegisterTask.execute();
        // start Task
    }

}
