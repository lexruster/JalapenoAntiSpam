package su.Jalapeno.AntiSpam.Activities;

import java.util.Date;
import java.util.UUID;

import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Services.WebService.WebErrors;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.RegisterClientRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.RegisterClientResponse;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.inject.Inject;

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

	public void ShowToast(int res) {
		Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
	}

	public void Register(View view) {
		new RegisterTask().execute(this);
	}

	class RegisterTask extends AsyncTask<RegisterActivity, Void, RegisterClientResponse> {

		@Override
		protected void onPostExecute(RegisterClientResponse registerClient) {
			if (registerClient.WasSuccessful) {
				Config config = _settingsService.LoadSettings();
				config.ClientRegistered = true;
				config.Enabled = true;
				_settingsService.SaveSettings(config);
				Log.d(LOG_TAG, "Register with guid " + config.ClientId);
				UiUtils.NavigateTo(Settings.class);
			} else {
				if (registerClient.ErrorMessage.equals(WebErrors.UserBanned)) {
					ShowToast(R.string.BannedRegister);
				} else {
					ShowToast(R.string.ErrorRegister);
				}
			}
		}

		@Override
		protected RegisterClientResponse doInBackground(RegisterActivity... activitis) {
			Config config = _settingsService.LoadSettings();
			config.ClientId = UUID.randomUUID();
			RegisterClientRequest request = new RegisterClientRequest();
			request.ClientId = config.ClientId;
			request.Token = "TOKEN " + new Date().toLocaleString();
			_settingsService.SaveSettings(config);

			RegisterClientResponse registerClient = _jalapenoWebServiceWraper.RegisterClient(request);

			return registerClient;
		}
	}
}
