package su.Jalapeno.AntiSpam.Util.UI;

import roboguice.activity.RoboActivity;

public class JalapenoActivity extends RoboActivity  {

    protected UiControlsUtil UiUtils;

    public JalapenoActivity() {
        UiUtils = new UiControlsUtil(this);
    }
}

