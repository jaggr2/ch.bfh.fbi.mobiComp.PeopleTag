package ch.bfh.fbi.mobiComp.PeopleTag.gui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import ch.bfh.fbi.mobiComp.PeopleTag.R;

/**
 * Created by heroku on 27.03.14.
 */
public class SonarPanelActivity extends Activity {
    SonarPanelView sonarPanel;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sonarpanel);
    }
}