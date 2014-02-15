package su.Jalapeno.AntiSpam.Util.UI;

import roboguice.activity.RoboActivity;
import android.app.Activity;
import android.app.ListActivity;

public class JalapenoActivity extends RoboActivity  {

    protected UiControlsUtil UiUtils;

    public JalapenoActivity() {
        UiUtils = new UiControlsUtil(this);
    }
}

