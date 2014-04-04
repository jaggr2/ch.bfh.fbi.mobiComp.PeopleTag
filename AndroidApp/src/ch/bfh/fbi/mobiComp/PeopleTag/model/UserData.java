package ch.bfh.fbi.mobiComp.PeopleTag.model;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Pascal on 21.03.14.
 */
public class UserData {
    String id;
    String displayName;
    double latitude;
    double longitude;
    String timeStamp;



    public UserData(String id, String displayName, double latitude, double longitude, String timeStamp)
    {
        this.id = id;
        this.displayName = displayName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
    }

    public UserData(JSONObject jsonObject) throws JSONException {

        if(jsonObject == null) {
            throw new JSONException("JsonObject is null");
        }
        this.id = jsonObject.get("_id").toString();
        this.displayName = jsonObject.get("displayName").toString();
        this.latitude = jsonObject.optDouble("currentLatitude",0.0);
        this.longitude = jsonObject.optDouble("currentLongitude",0.0);
        this.timeStamp = jsonObject.getString("updatedAt");
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
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * Casts the userData to a geo Location
     *
     * @return Location
     */
    public Location getUserLocation(){
    Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    /**
     * Get distance from user location to current Location
     *
     * @param current
     * @return float Distance if distance cannot calculated 0 is returned
     */
    public float getDistanceToUserLocation(Location current){
        if(current != null) {
            return current.distanceTo(getUserLocation());
        }
        else return 0;
    }

    /**
     * Get Angle between current Location and UserLocation
     *
     * @param current
     * @return double angle
     */
    public double getAngleFromCurrentLocationToUserLoaction(Location current) {

        double dLon = (getUserLocation().getLongitude() - current.getLongitude());

        double y = Math.sin(dLon) * Math.cos(getUserLocation().getLatitude());
        double x = Math.cos(current.getLatitude()) * Math.sin(getUserLocation().getLatitude()) - Math.sin(current.getLatitude())
                * Math.cos(getUserLocation().getLatitude()) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng;

        return brng;
    }
}
