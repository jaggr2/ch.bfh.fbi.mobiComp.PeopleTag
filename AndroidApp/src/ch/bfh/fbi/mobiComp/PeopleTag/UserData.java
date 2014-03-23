package ch.bfh.fbi.mobiComp.PeopleTag;


/**
 * Created by Pascal on 21.03.14.
 */
public class UserData {
    String id;
    String displayName;
    double latitude;
    double longitude;



    public UserData(String id, String displayName, double latitude, double longitude)
    {
        this.id = id;
        this.displayName = displayName;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
