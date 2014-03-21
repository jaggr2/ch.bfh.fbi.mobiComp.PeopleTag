package ch.bfh.fbi.mobiComp.PeopleTag;


/**
 * Created by Pascal on 21.03.14.
 */
public class UserData {
    Integer id;
    String displayName;
    long latitude;
    long longitude;



    public UserData(Integer id, String displayName, long latitude, long longitude)
    {
        this.id = id;
        this.displayName = displayName;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public Integer getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getLatitude() {
        return latitude;
    }

    public long getLongitude() {
        return longitude;
    }
}
