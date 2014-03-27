package ch.bfh.fbi.mobiComp.PeopleTag.gui;


import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.bfh.fbi.mobiComp.PeopleTag.R;

/**
 * Created by Pascal on 07.03.14.
 */
public class PeopleSearchListFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.people_search_fragment, container, false);

    }
}