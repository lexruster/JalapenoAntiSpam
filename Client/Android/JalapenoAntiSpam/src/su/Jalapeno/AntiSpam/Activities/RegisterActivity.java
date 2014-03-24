package su.Jalapeno.AntiSpam.Activities;

import java.util.Date;
import java.util.UUID;

import roboguice.inject.ContentView;
import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Services.WebService.WebErrors;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.PublicKeyResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.RegisterClientRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.RegisterClientResponse;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.CryptoService;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.PublicKeyInfo;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import su.Jalapeno.AntiSpam.Util.UI.Spiner;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.inject.Inject;

@ContentView(R.layout.activity_register)
public class RegisterActivity extends JalapenoActivity {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "RegisterActivity";
	
	@Inject
	JalapenoWebServiceWraper _jalapenoWebServiceWraper;
	@Inject
	Context _context;
	@Inject
	private SettingsService _settingsService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.Debug(LOG_TAG, "onCreate");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Logger.Debug(LOG_TAG, "onResume");
		Resume();
	}

	private void Resume() {
		Config config = _settingsService.LoadSettings();
		if (config.ClientRegistered) {
			Logger.Error(LOG_TAG, "Init clientRegistered!");
			UiUtils.NavigateTo(SettingsActivity.class);
		}
	}
	
	@Override
	public void onBackPressed() {
		Logger.Debug(LOG_TAG, "onBackPressed");
	}


	public void ShowToast(int res) {
		Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
	}

	public void Register(View view) {
		new RegisterTask().execute(this);
	}

	class RegisterTask extends
			AsyncTask<RegisterActivity, Void, RegisterClientResponse> {

		Spiner spiner = new Spiner(RegisterActivity.this);

		@Override
		protected void onPostExecute(RegisterClientResponse registerClient) {
			if (registerClient.WasSuccessful) {
				Config config = _settingsService.LoadSettings();
				config.ClientRegistered = true;
				config.Enabled = true;
				_settingsService.SaveSettings(config);
				Logger.Debug(LOG_TAG, "Register with guid " + config.ClientId);
				spiner.Hide();
				UiUtils.NavigateAndClearHistory(SettingsActivity.class);
			} else {
				spiner.Hide();
				if (registerClient.ErrorMessage.equals(WebErrors.UserBanned)) {
					ShowToast(R.string.BannedRegister);
				} else {
					ShowToast(R.string.ErrorRegister);
				}
			}
		}

		@Override
		protected void onPreExecute() {
			spiner.Show();
		}

		@SuppressWarnings("deprecation")
		@Override
		protected RegisterClientResponse doInBackground(
				RegisterActivity... activitis) {
			PublicKeyResponse pbk = _jalapenoWebServiceWraper.GetPublicKey();

			Logger.Debug(LOG_TAG, "doInBackground GetPublicKey  "
					+ pbk.WasSuccessful);
			if (pbk.WasSuccessful) {
				PublicKeyInfo publicKeyInfo = CryptoService
						.GetPublicKeyInfo(pbk.PublicKey);
				_settingsService.UpdatePublicKey(publicKeyInfo);
			} else {
				RegisterClientResponse registerClient = new RegisterClientResponse();
				registerClient.ErrorMessage = "";
				registerClient.WasSuccessful = false;

				return registerClient;
			}

			Config config = _settingsService.LoadSettings();
			config.ClientId = UUID.randomUUID();
			RegisterClientRequest request = new RegisterClientRequest();
			request.ClientId = config.ClientId;
			request.Token = "TOKEN " + new Date().toLocaleString();
			_settingsService.SaveSettings(config);

			RegisterClientResponse registerClient = _jalapenoWebServiceWraper
					.RegisterClient(request);

			return registerClient;
		}
	}
}
