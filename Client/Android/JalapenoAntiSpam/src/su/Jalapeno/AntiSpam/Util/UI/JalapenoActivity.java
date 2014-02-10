package su.Jalapeno.AntiSpam.Util.UI;

import android.app.Activity;
import android.app.ListActivity;

public class JalapenoActivity extends Activity {

    protected UiControlsUtil UiUtils;

    public JalapenoActivity() {
        UiUtils = new UiControlsUtil(this);
    }
}

