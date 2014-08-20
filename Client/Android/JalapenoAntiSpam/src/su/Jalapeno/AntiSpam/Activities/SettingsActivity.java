package su.Jalapeno.AntiSpam.Activities;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.Sms.AccessService;
import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.Util.AccessInfo;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.inject.Inject;

@ContentView(R.layout.settings)
public class SettingsActivity extends JalapenoActivity {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "Settings";

	@Inject
	Context _context;

	@Inject
	AccessService _accessService;

	@Inject
	SettingsService _settingsService;

	@InjectView(R.id.buttonDebug)
	Button buttonDebug;

	@InjectView(R.id.toggleEnabled)
	Button toogleButton;

	@InjectView(R.id.textAccessInfo)
	TextView textAccessInfo;

	@Override
	public void onBackPressed() {
		Logger.Debug(LOG_TAG, "onBackPressed");
		UiUtils.NavigateAndClearHistory(SettingsActivity.class);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.Debug(LOG_TAG, "onCreate");
		Init();
	}

	public void NavigateToDebug(View v) {
		if (Constants.VIEW_DEBUG_UI) {
			UiUtils.NavigateTo(Debug.class);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Logger.Debug(LOG_TAG, "onPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Logger.Debug(LOG_TAG, "onResume");
		Resume();
	}

	private void Init() {
		SetDebugMode(Constants.VIEW_DEBUG_UI);
	}

	private void Resume() {
		boolean clientIsRegistered = _settingsService.ClientIsRegistered();
		Logger.Debug(LOG_TAG, "Init ClientRegistered " + clientIsRegistered);
		if (!clientIsRegistered) {
			_settingsService.HandleClientNotRegistered();
			Logger.Debug(LOG_TAG, "Init NavigateTo RegisterActivity");
			UiUtils.NavigateTo(RegisterActivity.class);
		}

		UpdateOnOffButton(_settingsService.AntispamEnabled());

		if (!_accessService.AccessCheck()) {
			UiUtils.NavigateTo(BillingActivity.class);
		}
		AccessInfo accessInfo = _settingsService.GetAccessInfo();
		if (accessInfo.IsUnlimitedAccess) {
			textAccessInfo.setText(R.string.AccessFullInfo);
		} else {
			String info = _context.getString(R.string.AccessInfo, accessInfo.EvaluationDaysLast);
			textAccessInfo.setText(info);
		}
	}

	private void UpdateOnOffButton(boolean antispamEnabled) {
		if (antispamEnabled) {
			toogleButton.setText(R.string.AntiSpamOn);
			toogleButton.setTextColor(getResources().getColor(R.color.toogle_green));
		} else {
			// toogleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_button_black_off));
			toogleButton.setText(R.string.AntiSpamOff);
			toogleButton.setTextColor(getResources().getColor(R.color.toogle_red));
		}
	}

	private void SetDebugMode(boolean isDebug) {
		if (isDebug) {
			buttonDebug.setVisibility(View.VISIBLE);
		} else {
			buttonDebug.setVisibility(View.INVISIBLE);
		}
	}

	public void ViewSpamerList(View v) {
		UiUtils.NavigateTo(SpamerList.class);
	}

	public void toggleClick(View view) {
		boolean enabled = _settingsService.ToggleAntispamEnabled();
		UpdateOnOffButton(enabled);
		startService(new Intent(this, AppService.class));
	}

	public void smsTrash(View view) {
		UiUtils.NavigateTo(TrashSmsActivity.class);
	}

	public void smsValidate(View view) {
		UiUtils.NavigateTo(SmsAnalyzerActivity.class);
	}
}
