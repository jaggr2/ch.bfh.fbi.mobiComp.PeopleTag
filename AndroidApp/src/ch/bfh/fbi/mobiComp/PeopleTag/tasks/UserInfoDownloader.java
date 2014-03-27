package ch.bfh.fbi.mobiComp.PeopleTag.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import ch.bfh.fbi.mobiComp.PeopleTag.R;
import ch.bfh.fbi.mobiComp.PeopleTag.gui.UserDataAdapter;
import ch.bfh.fbi.mobiComp.PeopleTag.model.UserData;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserInfoDownloader extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "UserInfoDownloader"; // for LogCat
    private ArrayList<UserData> datas;
    private Activity mHostActivity;

    	/*
    	 * auxiliary method to perform a query with HTTP Get (may throw an error if called in the UI-thread)
    	 * @result raw JSON feed from server in form of a String
    	 */
        public String getJSONFeed(String getURL) {

        	String rawJSONFeed= null;

            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(getURL);

            try {
    	    	HttpResponse response = client.execute(httpGet);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                	rawJSONFeed = EntityUtils.toString(response.getEntity()); // FIXME: this may not work with large json feeds
                	Log.d(TAG, "Contents of HTTP response: " + rawJSONFeed);
                } else {
    	        	 Log.d(TAG, "HTTP Status Code was not 200 / OK");
    	        }
            } catch (Exception e) {
                  Log.e(TAG, "Error loading " + getURL, e);
            }
            return rawJSONFeed;
        }

        /*
         * Constructor
         */
    	public UserInfoDownloader(Activity hostActivity) {
    		this.mHostActivity = hostActivity;
    	}

    	@Override
        protected Boolean doInBackground(String ... Params) { // this method runs in dedicated non-UI thread
    		    		
    		String searchURL ="http://peopletag.xrj.ch/users";
        	// to determine the structure of the json formatted response, download this url into
        	// a JSON formatter such as http://jsonformat.com
    		// Then copy/paste the formatted response into an editor that supports syntax highlighting
    		//  => structure: 
    		//       - the big array delimited with [] which has the name "data",
    		//       - the array elements are comma-separated containers delimited with {}
    		//       - array elements contain comma-separated name-value pairs
            //       - names and text-values are surrounded with double quotes
    		// the subsequent code assumes that the json-formatted response to the http-get has this structure
    		
    		datas = new ArrayList<UserData>();
    		Boolean querySuccessful = false;
    		  		
    		try {
    			//JSONObject rootJSONObject = new JSONObject(getJSONFeed(searchURL)); // FIXME: this may not work with large json feeds
    			// retrieve the object that contains the array named "data" --> see structure of the json response
    			JSONArray JSONsessionArray= new JSONArray(getJSONFeed(searchURL));
    			for (int i = 0; i < JSONsessionArray.length(); i++) { // step through array elements  
    				JSONObject JSONsessionObject = JSONsessionArray.getJSONObject(i);  // get a container of an array element
    	                        
    				// the container contains name-value pairs -> retrieve value by name
    				UserData data = new UserData(
    	               				    // mind security threats resulting from the get method internally using eval
    									// (exploits by malicious content in the response are feasible)
    									// we do not fix this for the moti project as security is not a concern
    	            					JSONsessionObject.get("_id").toString(),
    	            					JSONsessionObject.get("displayName").toString(),
                                        JSONsessionObject.getDouble("currentLatitude"),
                                        JSONsessionObject.getDouble("currentLongitude"));
    				datas.add(data);
    			} // for (int i = 0; i < JSONsessionArray.length(); i++)
    			querySuccessful = true;
    		} catch (JSONException e) {
    			Log.e(TAG, "JSON Exception");
    		}    		
    		return querySuccessful;
        }      

        @Override
        protected void onPostExecute(Boolean querySuccessful) {
        	if (querySuccessful) {
        	    // display the tweets in a listView
                ListView listView = (ListView) mHostActivity.findViewById(R.id.user_list);
                listView.setAdapter(new UserDataAdapter(mHostActivity, R.layout.listitem, datas));
        	} else {
        		// optionally handle the unsuccessful query
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
  } 
