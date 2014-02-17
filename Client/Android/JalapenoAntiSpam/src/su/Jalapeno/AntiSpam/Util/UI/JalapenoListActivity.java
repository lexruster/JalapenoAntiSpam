package su.Jalapeno.AntiSpam.Util.UI;

import roboguice.activity.RoboListActivity;

public class JalapenoListActivity extends RoboListActivity {

    protected UiControlsUtil UiUtils;

    public JalapenoListActivity() {
        UiUtils = new UiControlsUtil(this);
    }
}
