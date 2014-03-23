package ch.bfh.fbi.mobiComp.PeopleTag;


import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Pascal on 07.03.14.
 */
public class PeopleSearch extends ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        return inflater.inflate(R.layout.people_search_fragment, container, false);

    }
}