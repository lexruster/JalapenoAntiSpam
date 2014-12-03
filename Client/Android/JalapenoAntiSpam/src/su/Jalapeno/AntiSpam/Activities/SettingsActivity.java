package su.Jalapeno.AntiSpam.Activities;

import java.util.Locale;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import su.Jalapeno.AntiSpam.FilterPro.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.Sms.TrashSmsService;
import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.SystemService.NotifyType;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;

@ContentView(R.layout.activity_settings)
public class SettingsActivity extends JalapenoActivity {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "Settings";

	@Inject
	Context _context;

	@Inject
	SettingsService _settingsService;

	@Inject
	TrashSmsService _trashSmsService;

	@InjectView(R.id.buttonDebug)
	Button buttonDebug;

	@InjectView(R.id.toggleEnabled)
	Button toogleButton;

	@InjectView(R.id.buttonSmsTrash)
	Button buttonSmsTrash;

	BroadcastReceiver _receiver;
	IntentFilter _intFilt;

	boolean reciverRegistered;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.Debug(LOG_TAG, "onCreate");
		if (getIntent().getBooleanExtra("EXIT", false)) {
			finish();
			return;
		}
		Init();
	}

	private void Init() {
		SetDebugMode(Constants.VIEW_DEBUG_UI);

		_receiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				Logger.Debug(LOG_TAG, "BroadcastReceiver onReceive");
				new UpdateTrashTextAsync().execute();
			}
		};

		_intFilt = new IntentFilter(Constants.BROADCAST_TRASH_SMS_ACTION);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Logger.Debug(LOG_TAG, "onPause");
		if (reciverRegistered) {
			unregisterReceiver(_receiver);
			reciverRegistered = false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Logger.Debug(LOG_TAG, "onResume");
		Resume();
	}

	private void Resume() {
		boolean clientIsRegistered = _settingsService.ClientIsRegistered();
		Logger.Debug(LOG_TAG, "Init ClientRegistered " + clientIsRegistered);
		if (!clientIsRegistered) {
			_settingsService.HandleClientNotRegistered();
			Logger.Debug(LOG_TAG, "Init NavigateTo RegisterActivity");
			UiUtils.NavigateAndClearHistory(RegisterActivity.class);
			return;
		}

		UpdateOnOffButton(_settingsService.AntispamEnabled());
		registerReceiver(_receiver, _intFilt);
		reciverRegistered = true;
		new UpdateTrashTextAsync().execute();
	}

	private void UpdateOnOffButton(boolean antispamEnabled) {
		Logger.Debug(LOG_TAG, "UpdateOnOffButton enabled: " + antispamEnabled);
		if (antispamEnabled) {
			toogleButton.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.ico_switch_on, 0, 0);
			toogleButton.setText(R.string.AntiSpamOn);
		} else {
			toogleButton.setCompoundDrawablesWithIntrinsicBounds(0,
					R.drawable.ico_switch_off, 0, 0);
			toogleButton.setText(R.string.AntiSpamOff);
		}
	}

	private void SetDebugMode(boolean isDebug) {
		if (isDebug) {
			buttonDebug.setVisibility(View.VISIBLE);
		} else {
			buttonDebug.setVisibility(View.INVISIBLE);
		}
	}
	 
	/*@Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        //super.onCreateOptionsMenu(menu);
	        getSupportMenuInflater().inflate(R.menu.activity_itemlist, menu);
	        return true;
	    }
*/
 
/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		  com.actionbarsherlock.view.MenuInflater inflater =
		  getSupportMenuInflater(); inflater.inflate(R.menu.menu, menu); return
		  super.onCreateOptionsMenu(menu);
		 

		menu.add("Save").setIcon(R.drawable.ic_notif_white_pepper)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}
	*/
	/* @Override
	    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	        menu.add("One");
	        menu.add("Two");
	        menu.add("Three");
	        menu.add("Four");
	    }*/
	 /*
	 @Override
	    public boolean onContextItemSelected(android.view.MenuItem item) {
	        //Note how this callback is using the fully-qualified class name
	        Toast.makeText(this, "Got click: " + item.toString(), Toast.LENGTH_SHORT).show();
	        return true;
	    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String str = " - " + item.getItemId() + " - ";
		ShowToast(str);
		switch (item.getItemId()) {
		case android.R.id.home:
			UiUtils.NavigateAndClearHistory(SettingsActivity.class);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
*/
	@Override
	public void onBackPressed() {
		Logger.Debug(LOG_TAG, "onBackPressed");
		finish();
	}

	public void NavigateToDebug(View v) {
		if (Constants.VIEW_DEBUG_UI) {
			UiUtils.NavigateTo(Debug.class);
		}
	}

	public void ViewSpamerList(View v) {
		UiUtils.NavigateTo(SpamerList.class);
	}

	public void toggleClick(View view) {
		boolean enabled = _settingsService.ToggleAntispamEnabled();
		UpdateOnOffButton(enabled);
		startService(new Intent(this, AppService.class).putExtra(
				NotifyType.ExtraConstant, NotifyType.RefreshSmsNotify));
	}

	public void smsTrash(View view) {
		UiUtils.NavigateTo(TrashSmsActivity.class);
	}

	public void smsValidate(View view) {
		UiUtils.NavigateTo(SmsAnalyzerActivity.class);
	}

	class UpdateTrashTextAsync extends AsyncTask<Void, Void, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			startService(new Intent(_context, AppService.class).putExtra(
					NotifyType.ExtraConstant, NotifyType.RefreshSmsNotify));
			Integer i = (int) _trashSmsService.Count();
			Logger.Debug(LOG_TAG, "UpdateTrashTextAsync trash " + i);
			return i;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result > 0) {
				Logger.Debug(LOG_TAG,
						"UpdateTrashTextAsync onPostExecute trash " + result);
				String trashText = _context.getResources().getString(
						R.string.SmsTrashList);
				String newTrashText = String.format(Locale.getDefault(),
						"%s (%d)", trashText, result);
				buttonSmsTrash.setText(newTrashText);
			} else {
				buttonSmsTrash.setText(R.string.SmsTrashList);
			}
		}
	}
}
