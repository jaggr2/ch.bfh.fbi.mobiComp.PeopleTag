package ch.bfh.fbi.mobiComp.PeopleTag.tasks;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
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

public abstract class UserUpdateTask extends AsyncTask<String, Integer, UserData> {

    private static final String TAG = "UserUpdateTask"; // for LogCat
    private Location location;
    private String userID;
    private String displayName;

    /*
     * Constructor
     */
    public UserUpdateTask(String userId, String displayName, Location location) {
        this.userID = userId;
        this.displayName = displayName;
        this.location = location;
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

    @Override
    public abstract void onPostExecute(UserData result);

    @Override
    public abstract void onPreExecute();

    @Override
    public abstract void onProgressUpdate(Integer... values);


    @Override
    protected UserData doInBackground(String... Params) { // this method runs in dedicated non-UI thread
        return SendPost(0);
    }

    private UserData SendPost(Integer recursionCount) {

        String jsonResponse = null;

        String baseURL = "http://peopletag.xrj.ch";

        String url = (this.userID != null && this.userID.length() > 0 ? baseURL + "/users/" + this.userID : baseURL + "/users");


        String longitude = (location != null ? String.valueOf(location.getLongitude()) : "null");
        String latitude = (location != null ? String.valueOf(location.getLatitude()) : "null");

        this.publishProgress(20);


        String json = "{ " +
                "\"displayName\": \"" + displayName + "\"," +
                "\"currentLongitude\": " + longitude + "," +
                "\"currentLatitude\": " + latitude +
                " }";

        try {
            HttpResponse response = makePostRequest(url, json);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                jsonResponse = EntityUtils.toString(response.getEntity());
                Log.d(TAG, "POST " + url + " returned Code " + response.getStatusLine().getStatusCode() + " with Content: " + jsonResponse);
            } else {
                Log.e(TAG, "POST " + url + " returned Code " + response.getStatusLine().getStatusCode() + " with Error-Content: " + response.getEntity());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading " + url, e);
        }

        this.publishProgress(50);

        if (jsonResponse == null) {
            return null;
        }

        try {
            // retrieve the object that contains the array named "data" --> see structure of the json response
            JSONObject rootJsonObject = new JSONObject(jsonResponse);

            this.publishProgress(100);

            // the container contains name-value pairs -> retrieve value by name
            if (rootJsonObject.has("_id")) {
                // mind security threats resulting from the get method internally using eval
                // (exploits by malicious content in the response are feasible)
                // we do not fix this for the moti project as security is not a concern
                return new UserData(rootJsonObject.get("_id").toString(),
                        rootJsonObject.get("displayName").toString(),
                        rootJsonObject.getDouble("currentLatitude"),
                        rootJsonObject.getDouble("currentLongitude"),
                        rootJsonObject.getString("updatedAt"));
            } else if (rootJsonObject.has("msg")) {

                if (rootJsonObject.getLong("msg") == 0) {
                    Log.e(TAG, "UserID does not exists on Server, try reregister it");
                    userID = null;
                    if (recursionCount < 3) {

                        try {
                            Thread.sleep((long) Math.pow(2, recursionCount) * 1000);
                        } catch (InterruptedException e) {
                            Thread.interrupted();
                        }

                        return SendPost(recursionCount + 1);
                    } else {
                        return null;
                    }
                }

                return new UserData(userID, displayName, (location != null ? location.getLongitude() : 0), (location != null ? location.getLatitude() : 0), "");
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception", e);
        }
        return null;
    }
}

