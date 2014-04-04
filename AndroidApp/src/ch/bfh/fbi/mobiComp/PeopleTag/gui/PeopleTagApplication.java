package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;
import ch.bfh.fbi.mobiComp.PeopleTag.model.UserData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by roger.jaggi on 28.03.2014.
 */
public class PeopleTagApplication extends Application {

    private static final String TAG = "PeopleTagApplication"; // for LogCat

    public static final String PREFS_NAME = "PeopleTagUser";
    public static final String PREF_ID = "ID";
    public static final String PREF_DISPLAYNAME = "displayName";
    public static final String PREF_SHOWALL = "showAllUsers";
    private SharedPreferences settings = null;

    private ConcurrentLinkedQueue<UserData> users = new ConcurrentLinkedQueue<>();

    public String getUserID() {
        return (settings != null ? this.settings.getString(PREF_ID, "") : "");
    }

    public String getDisplayName() {
        return (settings != null ? this.settings.getString(PREF_DISPLAYNAME, "") : "");
    }

    public Boolean getPrefShowall() {
        return (settings != null && this.settings.getBoolean(PREF_SHOWALL, false));
    }

    public ConcurrentLinkedQueue<UserData> getUsers() {
        return users;
    }

    public UserData getUserById(String Id) {
        for (UserData userData : users) {
            if(userData.getId().equals(Id)) {
                return userData;
            }
        }
        return null;
    }

    private final ConcurrentLinkedQueue<IUserListChangedListener> eventListeners = new ConcurrentLinkedQueue<IUserListChangedListener>();

    public void addUserListChangedListener( IUserListChangedListener listener )
    {
        if ( ! eventListeners.contains( listener ) ) {
            eventListeners.add( listener );
        }
    }

    public void removeUserListChangedListener( IUserListChangedListener observer )
    {
        eventListeners.remove( observer );
    }

    public boolean reloadListFromJson(String jsonList) {
        boolean result = false;
        try {
            //JSONObject rootJSONObject = new JSONObject(getJSONFeed(searchURL)); // FIXME: this may not work with large json feeds
            // retrieve the object that contains the array named "data" --> see structure of the json response
            if(jsonList == null || jsonList.length() < 1) {
                // no data
                return false;
            }

            users.clear();

            JSONArray JSONsessionArray= new JSONArray(jsonList);
            for (int i = 0; i < JSONsessionArray.length(); i++) { // step through array elements
                JSONObject JSONsessionObject = JSONsessionArray.getJSONObject(i);  // get a container of an array element

                // the container contains name-value pairs -> retrieve value by name
                users.add(new UserData(JSONsessionObject));
            }

            result = true;
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception");
        }

        return result;
    }

    public void notifiyListeners() {
        // notify Listeners
        for(IUserListChangedListener listener : eventListeners) {
            listener.userListChanged(this);
        }
    }

    public void setPrefShowall(Boolean enabled) {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(PREF_SHOWALL, enabled);

        // Commit the edits!
        editor.commit();
    }

    public void setUserData(UserData data) {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(PREF_ID, data.getId());
        editor.putString(PREF_DISPLAYNAME, data.getDisplayName());

        // Commit the edits!
        editor.commit();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        settings = getSharedPreferences(PREFS_NAME, 0);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
