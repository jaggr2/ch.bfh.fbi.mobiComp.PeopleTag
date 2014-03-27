package ch.bfh.fbi.mobiComp.PeopleTag;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

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
     * @return float Distance
     */
    public float getDistanceToUserLocation(Location current){
        return current.distanceTo(getUserLocation());
    }

    /**
     * Get Angle between current Location and UserLocation
     *
     * @param current
     * @return double angle
     */
    private double getAngleFromCurrentLocationToUserLoaction(Location current) {

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
