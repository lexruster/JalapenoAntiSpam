package su.Jalapeno.AntiSpam.Util.UI;

import android.app.ListActivity;

public class JalapenoListActivity extends ListActivity {

    protected UiControlsUtil UiUtils;

    public JalapenoListActivity() {
        UiUtils = new UiControlsUtil(this);
    }
}
