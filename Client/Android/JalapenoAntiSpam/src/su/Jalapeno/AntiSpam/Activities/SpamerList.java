package su.Jalapeno.AntiSpam.Activities;

import roboguice.inject.ContentView;
import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.Adapters.SenderAdapter;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoListActivity;
import android.content.Context;
import android.os.Bundle;

import com.google.inject.Inject;

@ContentView(R.layout.activity_spamer_list)
public class SpamerList extends JalapenoListActivity {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "SpamerList";
	final String ATTRIBUTE_NAME_SENDER_ID = "SenderId";
	final String ATTRIBUTE_NAME_IS_SPAMER = "IsSpamer";

	Context _context;

	@Inject
	SenderAdapter _senderAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Init();
	}
	
	@Override
	public void onBackPressed() {
		Logger.Debug(LOG_TAG, "onBackPressed");
		UiUtils.NavigateAndClearHistory(SettingsActivity.class);
	}


	private void Init() {
		_context = this.getApplicationContext();
		LoadList();
	}

	private void LoadList() {
		setListAdapter(_senderAdapter);
	}
}