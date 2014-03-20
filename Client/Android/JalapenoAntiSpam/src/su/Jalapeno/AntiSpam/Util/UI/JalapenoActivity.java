package su.Jalapeno.AntiSpam.Util.UI;

import android.support.v4.app.NavUtils;

import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

import roboguice.activity.RoboActivity;
import roboguice.util.RoboContext;
import su.Jalapeno.AntiSpam.Util.Logger;

public class JalapenoActivity extends RoboSherlockActivity{
    protected UiControlsUtil UiUtils;

    public JalapenoActivity() {
        UiUtils = new UiControlsUtil(this);
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}

