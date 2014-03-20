package su.Jalapeno.AntiSpam.Util.UI;

import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockListActivity;

import roboguice.activity.RoboListActivity;

public class JalapenoListActivity extends RoboSherlockListActivity{

    protected UiControlsUtil UiUtils;

    public JalapenoListActivity() {
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
