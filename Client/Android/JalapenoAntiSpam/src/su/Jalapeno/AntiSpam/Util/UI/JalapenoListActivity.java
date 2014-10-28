package su.Jalapeno.AntiSpam.Util.UI;

import su.Jalapeno.AntiSpam.Activities.SettingsActivity;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;

import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockListActivity;

public class JalapenoListActivity extends RoboSherlockListActivity {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "JalapenoListActivity";

	protected UiControlsUtil UiUtils;

	public JalapenoListActivity() {
		UiUtils = new UiControlsUtil(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Logger.Debug(LOG_TAG, "onOptionsItemSelected to parent");
			
			UiUtils.NavigateAndClearHistory(SettingsActivity.class);
			
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
