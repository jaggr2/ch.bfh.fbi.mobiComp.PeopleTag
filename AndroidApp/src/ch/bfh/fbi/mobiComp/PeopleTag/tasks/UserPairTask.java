package ch.bfh.fbi.mobiComp.PeopleTag.tasks;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public abstract class UserPairTask extends AsyncTask<String, Integer, Boolean> {

    private static final String TAG = "UserPairTask"; // for LogCat
    private Boolean createTrueDeleteFalse;
    private String firstUserID;
    private String secondUserId;

    /*
     * Constructor
     */
    public UserPairTask(String firstUserID, String secondUserId, Boolean createTrueDeleteFalse) {
        this.firstUserID = firstUserID;
        this.secondUserId = secondUserId;
        this.createTrueDeleteFalse = createTrueDeleteFalse;
    }

    public static HttpResponse makePostRequest(String uri, String json) {
        try {
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setHeader("Accept", "application/json");
            if(json != null && json.length() > 0) {
                httpPost.setEntity(new StringEntity(json));
                httpPost.setHeader("Content-type", "application/json");
            }
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

    public static HttpResponse makeDeleteRequest(String uri) {
        try {
            HttpDelete httpDelete = new HttpDelete(uri);
            httpDelete.setHeader("Accept", "application/json");
            return new DefaultHttpClient().execute(httpDelete);
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
    public abstract void onPostExecute(Boolean result);

    @Override
    public abstract void onPreExecute();

    @Override
    public abstract void onProgressUpdate(Integer... values);


    @Override
    protected Boolean doInBackground(String... Params) { // this method runs in dedicated non-UI thread

        String baseURL = "http://peopletag.xrj.ch";

        String url = baseURL + "/pairing/" + this.firstUserID + "/" + this.secondUserId;

        this.publishProgress(20);

        Boolean returnValue = false;
        try {
            HttpResponse response = ( createTrueDeleteFalse ? makePostRequest(url, null) : makeDeleteRequest(url) );
            if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                returnValue = true;
                Log.d(TAG, ( createTrueDeleteFalse ? "POST " : "DELETE ") + url + " returned Code " + response.getStatusLine().getStatusCode());
            } else {
                Log.e(TAG, ( createTrueDeleteFalse ? "POST " : "DELETE ") + url + " returned Code " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while making " + ( createTrueDeleteFalse ? "POST " : "DELETE ") + url, e);
        }

        return returnValue;
    }
}

