package su.Jalapeno.AntiSpam.Util.UI;

import su.Jalapeno.AntiSpam.Activities.SettingsActivity;

import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

public class JalapenoActivity extends RoboSherlockActivity{
    public UiControlsUtil UiUtils;

    public JalapenoActivity() {
        UiUtils = new UiControlsUtil(this);
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	UiUtils.NavigateAndClearHistory(SettingsActivity.class);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}

