package ch.bfh.fbi.mobiComp.PeopleTag.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import ch.bfh.fbi.mobiComp.PeopleTag.R;
import ch.bfh.fbi.mobiComp.PeopleTag.gui.SonarPanelActivity;
import ch.bfh.fbi.mobiComp.PeopleTag.gui.UserDataAdapter;
import ch.bfh.fbi.mobiComp.PeopleTag.model.UserData;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class UserRegisterTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "UserRegisterTask"; // for LogCat
    private Activity mHostActivity;
    private UserData result = null;

    /*
         * Constructor
         */
    public UserRegisterTask(Activity hostActivity) {
        this.mHostActivity = hostActivity;
    }

    @Override
    protected Boolean doInBackground(String ... Params) { // this method runs in dedicated non-UI thread

        Boolean querySuccessful = false;

        String jsonResponse= null;
        String url = "http://peopletag.xrj.ch/users";

        EditText editText = (EditText) mHostActivity.findViewById(R.id.editTextDisplayName);

        String json = "{ \"DisplayName\": \"" + editText.getText().toString() + "\" }";

        try {
            HttpResponse response = makePostRequest(url, json);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                jsonResponse = EntityUtils.toString(response.getEntity());
                Log.d(TAG, "Contents of HTTP response: " + jsonResponse);
                querySuccessful = true;
            } else {
                Log.d(TAG, "HTTP Status Code was not 200 / OK");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading " + url, e);
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
        }

        return querySuccessful;
    }

    @Override
    protected void onPostExecute(Boolean querySuccessful) {
        if (querySuccessful) {
            mHostActivity.finish();
            // display the tweets in a listView
            /*
            final ListView listView = (ListView) mHostActivity.findViewById(R.id.user_list);
            listView.setAdapter(new UserDataAdapter(mHostActivity, R.layout.listitem, datas));
            listView.setClickable(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    UserData o = (UserData) listView.getItemAtPosition(position);
                    Intent intent = new Intent(mHostActivity, SonarPanelActivity.class);
                    intent.putExtra("user", o.getDisplayName());
                    mHostActivity.startActivity(intent);
                }
            });
            */
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

