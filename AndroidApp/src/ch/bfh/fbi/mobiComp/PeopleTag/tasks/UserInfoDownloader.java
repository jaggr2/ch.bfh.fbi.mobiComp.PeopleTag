package ch.bfh.fbi.mobiComp.PeopleTag.tasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import ch.bfh.fbi.mobiComp.PeopleTag.R;
import ch.bfh.fbi.mobiComp.PeopleTag.gui.MainActivity;
import ch.bfh.fbi.mobiComp.PeopleTag.gui.PeopleTagApplication;
import ch.bfh.fbi.mobiComp.PeopleTag.gui.SonarPanelActivity;
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
import android.widget.AdapterView.OnItemLongClickListener;

import java.util.ArrayList;

public class UserInfoDownloader extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "UserInfoDownloader"; // for LogCat
    private ArrayList<UserData> datas;
    private MainActivity mHostActivity;

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
    	public UserInfoDownloader(MainActivity hostActivity) {
    		this.mHostActivity = hostActivity;
    	}

    	@Override
        protected Boolean doInBackground(String ... Params) { // this method runs in dedicated non-UI thread
    		    		
    		String searchURL;

            if(((PeopleTagApplication)mHostActivity.getApplication()).getPrefShowall()) {
                searchURL = "http://peopletag.xrj.ch/users";
            }
            else {
                searchURL = "http://peopletag.xrj.ch/users/paired-with/" + mHostActivity.getCurrentUserId();
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
    		
    		datas = new ArrayList<UserData>();
    		Boolean querySuccessful = false;
    		  		
    		try {
    			//JSONObject rootJSONObject = new JSONObject(getJSONFeed(searchURL)); // FIXME: this may not work with large json feeds
    			// retrieve the object that contains the array named "data" --> see structure of the json response
                String jsonFeed = getJSONFeed(searchURL);
                if(jsonFeed == null || jsonFeed.length() < 1) {
                    // no data
                    return true;
                }
    			JSONArray JSONsessionArray= new JSONArray(jsonFeed);
    			for (int i = 0; i < JSONsessionArray.length(); i++) { // step through array elements  
    				JSONObject JSONsessionObject = JSONsessionArray.getJSONObject(i);  // get a container of an array element
    	                        
    				// the container contains name-value pairs -> retrieve value by name
    				UserData data = new UserData(
    	               				    // mind security threats resulting from the get method internally using eval
    									// (exploits by malicious content in the response are feasible)
    									// we do not fix this for the moti project as security is not a concern
    	            					JSONsessionObject.get("_id").toString(),
    	            					JSONsessionObject.get("displayName").toString(),
                                        JSONsessionObject.optDouble("currentLatitude",0.0),
                                        JSONsessionObject.optDouble("currentLongitude",0.0),
                                        JSONsessionObject.getString("updatedAt"));
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
                if (mHostActivity instanceof MainActivity)
                {
                    //Update List in MainActivity
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

                    listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                            final UserData userData = (UserData) listView.getItemAtPosition(arg2);

                            final String currentUserID = ((PeopleTagApplication)mHostActivity.getApplication()).getUserID();
                            if(currentUserID == null || currentUserID.length() <= 0) {
                                Toast.makeText(mHostActivity, "Please do setup first", Toast.LENGTH_LONG).show();
                                return false;
                            }

                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(which == DialogInterface.BUTTON_POSITIVE) {

                                        UserPairTask userPairTask = new UserPairTask( currentUserID, userData.getId(), false) {
                                            @Override
                                            public void onPostExecute(Boolean result) {
                                                if(result) {
                                                    Toast.makeText(mHostActivity, "Successfully removed paring with " + userData.getDisplayName() + " :)", Toast.LENGTH_LONG).show();
                                                }
                                                else {
                                                    Toast.makeText(mHostActivity, "Error on removed paring with " + userData.getDisplayName() + " :(", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onPreExecute() {

                                            }

                                            @Override
                                            public void onProgressUpdate(Integer... values) {

                                            }
                                        };
                                        userPairTask.execute();
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(mHostActivity);
                            builder.setMessage("Delete Paring with " + userData.getDisplayName() + "?").setPositiveButton("Delete", dialogClickListener)
                                    .setNegativeButton("Cancel", dialogClickListener).show();
                            return false;
                        }
                    });
                }
                /*
                else if (mHostActivity instanceof SonarPanelActivity)
                {
                    UserData correctUser = null;
                    for(UserData data : datas){
                        if(data.getDisplayName().equalsIgnoreCase(((SonarPanelActivity) mHostActivity).getUserid())){
                            correctUser = data;
                        }
                    }

                    ((SonarPanelActivity) mHostActivity).setLastUserData(correctUser);
                    ((SonarPanelActivity) mHostActivity).refresh(correctUser);
                }
                */
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

