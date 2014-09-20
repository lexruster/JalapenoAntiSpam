package su.Jalapeno.AntiSpam.Activities;

import java.util.Locale;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.Services.AccessService;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.Sms.TrashSmsService;
import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.SystemService.NotifyType;
import su.Jalapeno.AntiSpam.Util.AccessInfo;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.inject.Inject;

@ContentView(R.layout.activity_settings)
public class SettingsActivity extends JalapenoActivity {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "Settings";

	@Inject
	Context _context;

	@Inject
	AccessService _accessService;

	@Inject
	SettingsService _settingsService;

	@Inject
	TrashSmsService _trashSmsService;

	@InjectView(R.id.toggleEnabled)
	Button toogleButton;

	@InjectView(R.id.textAccessInfo)
	TextView textAccessInfo;

	@InjectView(R.id.textEarlyAccess)
	TextView linkEarlyAccess;

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
		_receiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				Logger.Debug(LOG_TAG, "BroadcastReceiver onReceive");
				new UpdateTrashTextAsync().execute();
			}
		};
		
		
		linkEarlyAccess.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UiUtils.NavigateTo(BillingActivity.class);
			}
		});

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

		if (!_accessService.AccessCheck()) {
			Logger.Debug(LOG_TAG, "Init NavigateTo BillingActivity");
			UiUtils.NavigateAndClearHistory(BillingActivity.class);
			return;
		}
		ShowAccessInfo();
		registerReceiver(_receiver, _intFilt);
		reciverRegistered = true;
		new UpdateTrashTextAsync().execute();
	}

	private void ShowAccessInfo() {
		AccessInfo accessInfo = _settingsService.GetAccessInfo();
		if (accessInfo.IsUnlimitedAccess) {
			textAccessInfo.setText(R.string.AccessFullInfo);
			linkEarlyAccess.setVisibility(View.INVISIBLE);
		} else {
			ShowLimitedAccessInfo(accessInfo);
		}
	}

	private void ShowLimitedAccessInfo(AccessInfo accessInfo) {
		if (accessInfo.EvaluationDaysLast < 1) {
			textAccessInfo.setText(R.string.AccessLastDay);
		} else {
			String info = _context.getString(R.string.AccessInfo, accessInfo.EvaluationDaysLast);
			textAccessInfo.setText(info);
		}
		linkEarlyAccess.setVisibility(View.VISIBLE);
	}

	private void UpdateOnOffButton(boolean antispamEnabled) {
		Logger.Debug(LOG_TAG, "UpdateOnOffButton enabled: " + antispamEnabled);
		if (antispamEnabled) {
			toogleButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_switch_on, 0, 0);
			toogleButton.setText(R.string.AntiSpamOn);
		} else {
			toogleButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ico_switch_off, 0, 0);
			toogleButton.setText(R.string.AntiSpamOff);
		}
	}

	@Override
	public void onBackPressed() {
		Logger.Debug(LOG_TAG, "onBackPressed");
		finish();
	}

	public void ViewSpamerList(View v) {
		UiUtils.NavigateTo(SpamerList.class);
	}

	public void toggleClick(View view) {
		boolean enabled = _settingsService.ToggleAntispamEnabled();
		UpdateOnOffButton(enabled);
		startService(new Intent(this, AppService.class).putExtra(NotifyType.ExtraConstant, NotifyType.RefreshSmsNotify));
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
			startService(new Intent(_context, AppService.class).putExtra(NotifyType.ExtraConstant, NotifyType.RefreshSmsNotify));
			Integer i = (int) _trashSmsService.Count();
			Logger.Debug(LOG_TAG, "UpdateTrashTextAsync trash " + i);
			return i;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result > 0) {
				Logger.Debug(LOG_TAG, "UpdateTrashTextAsync onPostExecute trash " + result);
				String trashText = _context.getResources().getString(R.string.SmsTrashList);
				String newTrashText = String.format(Locale.getDefault(), "%s (%d)", trashText, result);
				buttonSmsTrash.setText(newTrashText);
			} else {
				buttonSmsTrash.setText(R.string.SmsTrashList);
			}
		}
	}
}
