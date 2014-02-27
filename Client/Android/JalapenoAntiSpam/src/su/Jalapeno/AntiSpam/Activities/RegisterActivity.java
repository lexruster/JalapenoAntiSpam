package su.Jalapeno.AntiSpam.Activities;

import java.util.UUID;

import com.google.inject.Inject;

import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.R.layout;
import su.Jalapeno.AntiSpam.R.menu;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class RegisterActivity extends JalapenoActivity {

	@Inject
	JalapenoWebServiceWraper _jalapenoWebServiceWraper;
	
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "RegisterActivity";
	
	Context _context;
	private SettingsService _settingsService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		Init();
	}

	private void Init() {
		_context = this.getApplicationContext();
		_settingsService = new SettingsService(_context);

	}

	public void Register(View view) {
		
		Config config = _settingsService.LoadSettings();
		config.ClientId = UUID.randomUUID();
		
		if(_jalapenoWebServiceWraper.RegisterClient(config.ClientId)){
			config.ClientRegistered = true;
			config.Enabled = true;
			_settingsService.SaveSettings(config);
			Log.d(LOG_TAG, "Register with guid " + config.ClientId );
			UiUtils.NavigateTo(Settings.class);
		}
		else
		{
			Toast.makeText(this, R.string.ErrorRegister, Toast.LENGTH_SHORT).show();
		}
	}
}
