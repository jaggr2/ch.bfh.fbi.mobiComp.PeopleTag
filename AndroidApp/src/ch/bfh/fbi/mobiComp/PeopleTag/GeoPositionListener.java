package ch.bfh.fbi.mobiComp.PeopleTag;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by heroku on 27.03.14.
 */
public class GeoPositionListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

            // TODO Lan hier könnte man immer seine position Updaten für die View

        }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

}
