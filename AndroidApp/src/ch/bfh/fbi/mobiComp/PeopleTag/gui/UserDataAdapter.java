package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ch.bfh.fbi.mobiComp.PeopleTag.R;
import ch.bfh.fbi.mobiComp.PeopleTag.model.UserData;

import java.text.DecimalFormat;
import java.util.ArrayList;

/*
 * adapter to display a single tweet in the testViews of a single list item
 */
public class UserDataAdapter extends ArrayAdapter<UserData> {
	private ArrayList<UserData> datas;
	private Context mContext;

	public UserDataAdapter(Context context, int textViewResourceId, ArrayList<UserData> datas) {
		super(context, textViewResourceId, datas); // let the system do its job
		this.datas = datas; // initialize the member variable
		mContext = context;
	}

	// TODO: recycle the Views (see tutorial by L. Vogel)
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) { // v should not be null
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.listitem, null);
		}

		// fill the particular tweet into the textViews of this list item
		UserData data = datas.get(position);
		if (data != null) {
			TextView tvName = (TextView) v.findViewById(R.id.name);
			TextView tvLocation = (TextView) v.findViewById(R.id.location);
            TextView tvDistance = (TextView) v.findViewById(R.id.distance);

			if (tvName != null) {
                tvName.setText(data.getDisplayName());
			}

			if (tvLocation != null) {
                tvLocation.setText("Latitude: " + Double.toString(data.getLatitude()) + " Longitude: " + Double.toString(data.getLongitude()));
			}

            if (tvLocation != null) {
                //TODO Lan hier müsste die Current Location sein (momentan dummy)
               Location loc =  ((MainActivity) mContext).getActualLocation();

                if(loc!=null){
                    tvDistance.setText("Distance: " + readableDistance(data.getDistanceToUserLocation(loc)) + "\nRegisterd at: " + data.getTimeStamp());
                }
                else{
                    tvDistance.setText("NoGeoPositionFound");
                }
            }
			
		}
		return v;
	}

    public static String readableDistance(double distanceInMeters) {

        if(distanceInMeters <= 0) return "0";
        distanceInMeters *= 1000; // convert to mm

        final String[] units = new String[] { "mm", "m", "Km", "Mm", "Gm", "Tm" };
        int digitGroups = (int) (Math.log10(distanceInMeters)/Math.log10(1000));
        return new DecimalFormat("#,##0.#").format(distanceInMeters/Math.pow(1000, digitGroups)) + " " + units[digitGroups];
    }
}