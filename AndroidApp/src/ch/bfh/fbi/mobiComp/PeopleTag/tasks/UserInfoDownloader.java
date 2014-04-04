package ch.bfh.fbi.mobiComp.PeopleTag.tasks;

import android.os.AsyncTask;
import android.util.Log;
import ch.bfh.fbi.mobiComp.PeopleTag.gui.PeopleTagApplication;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public abstract class UserInfoDownloader extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "UserInfoDownloader"; // for LogCat
    protected PeopleTagApplication peopleTagApplication;

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
    	public UserInfoDownloader(PeopleTagApplication peopleTagApplication) {
    		this.peopleTagApplication = peopleTagApplication;
    	}

    	@Override
        protected Boolean doInBackground(String ... Params) { // this method runs in dedicated non-UI thread
    		    		
    		String searchURL;

            if(peopleTagApplication.getPrefShowall()) {
                searchURL = "http://peopletag.xrj.ch/users";
            }
            else {
                searchURL = "http://peopletag.xrj.ch/users/paired-with/" + peopleTagApplication.getUserID();
            }
        	// to determine the structure of the json formatted response, download this url into
        	// a JSON formatter such as http://jsonformat.com
    		// Then copy/paste the formatted response into an editor that supports syntax highlighting
    		//  => structure: 
    		//       - the big array delimited with [] which has the name "data",
    		//       - the array elements are comma-separated containers delimited with {}
    		//       - array elements contain comma-separated name-value pairs
            //       - names and text-values are surrounded with double quotes
    		// the subsequent code assumes that the json-formatted response to the http-get has this structure

            return peopleTagApplication.reloadListFromJson(getJSONFeed(searchURL));
        }


    @Override
    public abstract void onPostExecute(Boolean result);

    @Override
    public abstract void onPreExecute();

    @Override
    public abstract void onProgressUpdate(Void... values);
  } 

