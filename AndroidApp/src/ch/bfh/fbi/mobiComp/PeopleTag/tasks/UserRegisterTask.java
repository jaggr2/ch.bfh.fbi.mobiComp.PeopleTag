package ch.bfh.fbi.mobiComp.PeopleTag.tasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import ch.bfh.fbi.mobiComp.PeopleTag.R;
import ch.bfh.fbi.mobiComp.PeopleTag.gui.SetupActivity;
import ch.bfh.fbi.mobiComp.PeopleTag.model.UserData;
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

public class UserRegisterTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "UserRegisterTask"; // for LogCat
    private Activity mHostActivity;
    private Location location;
    public static UserData result = null;


    /*
         * Constructor
         */
    public UserRegisterTask(Activity hostActivity, Location location) {
        this.mHostActivity = hostActivity;
        this.location = location;
    }

    @Override
    protected Boolean doInBackground(String ... Params) { // this method runs in dedicated non-UI thread

        Boolean querySuccessful = false;

        String jsonResponse= null;
        String url = "http://peopletag.xrj.ch/users";

        EditText editText = (EditText) mHostActivity.findViewById(R.id.editTextDisplayName);

        String longitude = (location != null ? String.valueOf(location.getLongitude()) : "null");
        String latitude = (location != null ? String.valueOf(location.getLatitude()) : "null");


        String json = "{ " +
                "\"displayName\": \"" + editText.getText().toString() + "\"," +
                "\"currentLongitude\": " + longitude + "," +
                "\"currentLatitude\": " + latitude +
                " }";

        try {
            HttpResponse response = makePostRequest(url, json);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                jsonResponse = EntityUtils.toString(response.getEntity());
                Log.d(TAG, "Contents of HTTP POST response: " + jsonResponse);
                querySuccessful = true;
            } else {
                Log.e(TAG, "HTTP POST did return: " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading " + url, e);
        }

        if(jsonResponse == null || !querySuccessful) {
            return false;
        }

        try {
            // retrieve the object that contains the array named "data" --> see structure of the json response
            JSONObject rootJsonObject = new JSONObject(jsonResponse);

            // the container contains name-value pairs -> retrieve value by name
            result = new UserData(
                    // mind security threats resulting from the get method internally using eval
                    // (exploits by malicious content in the response are feasible)
                    // we do not fix this for the moti project as security is not a concern
                    rootJsonObject.get("_id").toString(),
                    rootJsonObject.get("displayName").toString(),
                    rootJsonObject.getDouble("currentLatitude"),
                    rootJsonObject.getDouble("currentLongitude"));


        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception", e);
            return false;
        }

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = mHostActivity.getSharedPreferences(SetupActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("ID", result.getId());

        // Commit the edits!
        editor.commit();

        return querySuccessful;
    }

    @Override
    protected void onPostExecute(Boolean querySuccessful) {
        if (querySuccessful) {
            mHostActivity.finish();


        } else {
            // optionally handle the unsuccessful query
/*
            Toast.makeText(this, mHostActivity.getString(R.string.ui_setup_error),
                    Toast.LENGTH_SHORT).show();
            return;
*/
        }
    }

    @Override
    protected void onPreExecute() {
        // not used in this example
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        // not used in this example
    }


    public static HttpResponse makePostRequest(String uri, String json) {
        try {
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new StringEntity(json));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            return new DefaultHttpClient().execute(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, "makePostRequest() -> UnsupportedEncodingException", e);
        } catch (ClientProtocolException e) {
            Log.e(TAG, "makePostRequest() -> ClientProtocolException", e);
        } catch (IOException e) {
            Log.e(TAG, "makePostRequest() -> IOException", e);
        }
        return null;
    }

       }

