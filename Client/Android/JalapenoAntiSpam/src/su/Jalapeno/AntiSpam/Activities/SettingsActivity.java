package su.Jalapeno.AntiSpam.Activities;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.google.inject.Inject;

@ContentView(R.layout.settings)
public class SettingsActivity extends JalapenoActivity {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "Settings";

	@Inject
	Context _context;

	@Inject
	SettingsService _settingsService;

	@InjectView(R.id.buttonDebug)
	Button buttonDebug;

	@InjectView(R.id.toggleEnabled)
	ToggleButton toogleButton;

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
		SetEvent();
	}

	private void SetEvent() {
		Logger.Debug(LOG_TAG, "SetEvent");
		UiUtils.SetTapForButton(R.id.buttonSpammerList, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewSpamerList();
			}
		});

		UiUtils.SetTapForButton(R.id.buttonDebug, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				UiUtils.NavigateTo(Debug.class);
			}
		});
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
		Config config = _settingsService.LoadSettings();
		Logger.Debug(LOG_TAG, "Init ClientRegistered " + config.ClientRegistered);
		if (config.ClientRegistered) {

		} else {
			config.Enabled = false;
			_settingsService.SaveSettings(config);
			Logger.Debug(LOG_TAG, "Init NavigateTo RegisterActivity");
			UiUtils.NavigateTo(RegisterActivity.class);
		}
		toogleButton.setChecked(config.Enabled);
	}

	private void SetDebugMode(boolean isDebug) {
		if (isDebug) {
			buttonDebug.setVisibility(View.VISIBLE);
		} else {
			buttonDebug.setVisibility(View.INVISIBLE);
		}
	}

	private void ViewSpamerList() {
		UiUtils.NavigateTo(SpamerList.class);
	}

	public void toggleClick(View view) {
		Config config = _settingsService.LoadSettings();
		config.Enabled = toogleButton.isChecked();
		_settingsService.SaveSettings(config);
		startService(new Intent(this, AppService.class));
	}

	public void smsTrash(View view) {
		UiUtils.NavigateTo(TrashSmsActivity.class);
	}

	public void smsValidate(View view) {
		UiUtils.NavigateTo(SmsAnalyzerActivity.class);
	}
}
