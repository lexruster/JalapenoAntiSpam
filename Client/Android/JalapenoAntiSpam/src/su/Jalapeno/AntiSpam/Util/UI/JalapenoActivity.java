package su.Jalapeno.AntiSpam.Util.UI;

import su.Jalapeno.AntiSpam.Activities.SettingsActivity;
import android.content.Context;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

public class JalapenoActivity extends RoboSherlockActivity {
	public UiControlsUtil UiUtils;
	protected Context _context;

	public JalapenoActivity() {
		UiUtils = new UiControlsUtil(this);
		_context = this;
	}

	public void ShowToast(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(_context, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void ShowToast(final int resourse) {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(_context, resourse, Toast.LENGTH_SHORT).show();
			}
		});
		// Toast.makeText(_context, resourse, Toast.LENGTH_SHORT).show();
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
