package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ch.bfh.fbi.mobiComp.PeopleTag.R;
import ch.bfh.fbi.mobiComp.PeopleTag.model.UserData;

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

			if (tvName != null) {
                tvName.setText(data.getDisplayName());
			}

			if (tvLocation != null) {
                tvLocation.setText("Latitude: " + Double.toString(data.getLatitude()) + " Longitude: " + Double.toString(data.getLongitude()));
			}
			
		}
		return v;
	}
}