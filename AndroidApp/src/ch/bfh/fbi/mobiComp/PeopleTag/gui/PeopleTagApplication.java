package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import ch.bfh.fbi.mobiComp.PeopleTag.model.UserData;

/**
 * Created by roger.jaggi on 28.03.2014.
 */
public class PeopleTagApplication extends Application {

    public static final String PREFS_NAME = "PeopleTagUser";
    public static final String PREF_ID = "ID";
    public static final String PREF_DISPLAYNAME = "displayName";
    public static final String PREF_SHOWALL = "showAllUsers";
    private SharedPreferences settings = null;

    public String getUserID() {
        return (settings != null ? this.settings.getString(PREF_ID, "") : "");
    }

    public String getDisplayName() {
        return (settings != null ? this.settings.getString(PREF_DISPLAYNAME, "") : "");
    }

    public Boolean getPrefShowall() {
        return (settings != null && this.settings.getBoolean(PREF_SHOWALL, false));
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
