package su.Jalapeno.AntiSpam.Activities;

import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ToggleButton;

//import com.google.android.gms.*;

public class Settings extends JalapenoActivity {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "Settings";
	ToggleButton toogleButton;
	SettingsService settingsService;
	private Config config;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);

		SetEvent();
		Init();
	}

	private void SetEvent() {
		UiUtils.SetTapForButton(R.id.buttonSpammerList,
				new View.OnClickListener() {
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

		toogleButton = (ToggleButton) findViewById(R.id.toggleEnabled);
	}

	@Override
	protected void onPause() {
		super.onPause();
		settingsService.SaveSettings(config);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Init();
	}

	private void Init() {
		Context context = this.getApplicationContext();
		settingsService = new SettingsService(context);
		config = settingsService.LoadSettings();
		Log.d(LOG_TAG, "Init ClientRegistered " + config.ClientRegistered);
		if (config.ClientRegistered) {

		} else {
			config.Enabled = false;
			settingsService.SaveSettings(config);
			UiUtils.NavigateTo(RegisterActivity.class);
		}

		toogleButton.setChecked(config.Enabled);
	}

	private void ViewSpamerList() {
		UiUtils.NavigateTo(SpamerList.class);
	}

	public void toggleClick(View view) {
		config.Enabled = toogleButton.isChecked();
		settingsService.SaveSettings(config);
	}

	public void smsTrash(View view) {
		UiUtils.NavigateTo(TrashSmsActivity.class);
	}
		
	public void smsValidate(View view) {
		UiUtils.NavigateTo(SmsAnalyzerActivity.class);
	}
}
