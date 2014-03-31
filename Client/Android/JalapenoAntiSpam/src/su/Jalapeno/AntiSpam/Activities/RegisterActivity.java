package su.Jalapeno.AntiSpam.Activities;

import java.util.Date;
import java.util.UUID;

import roboguice.inject.ContentView;
import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.Token.AbstractGetNameTask;
import su.Jalapeno.AntiSpam.Services.Token.GetNameInForeground;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.RegisterClientRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.PublicKeyResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.RegisterClientResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.WebErrorEnum;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.CryptoService;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.PublicKeyInfo;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import su.Jalapeno.AntiSpam.Util.UI.Spiner;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.inject.Inject;

@ContentView(R.layout.activity_register)
public class RegisterActivity extends JalapenoActivity {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "RegisterActivity";
	static final int REQUEST_CODE_PICK_ACCOUNT = 13000;
	static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 13001;
	static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 13002;

	private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
	public static final String EXTRA_ACCOUNTNAME = "extra_accountname";

	@Inject
	JalapenoWebServiceWraper _jalapenoWebServiceWraper;
	@Inject
	Context _context;
	@Inject
	private SettingsService _settingsService;

	private String mEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.Debug(LOG_TAG, "onCreate");

		Bundle extras = getIntent().getExtras();
		if (extras.containsKey(EXTRA_ACCOUNTNAME)) {
			mEmail = extras.getString(EXTRA_ACCOUNTNAME);
			getTask(RegisterActivity.this, mEmail, SCOPE).execute();
		}
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
			UiUtils.NavigateAndClearHistory(SettingsActivity.class);
		}
	}

	@Override
	public void onBackPressed() {
		Logger.Debug(LOG_TAG, "onBackPressed");
	}

	public void Register(View view) {
		Logger.Debug(LOG_TAG, "Register");
		new RegisterTask().execute(this);
		getUsername();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
			if (resultCode == RESULT_OK) {
				Logger.Debug(LOG_TAG, "onActivityResult RESULT_OK");
				mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				getUsername();
			} else if (resultCode == RESULT_CANCELED) {
				Logger.Debug(LOG_TAG, "onActivityResult RESULT_CANCELED");
				Toast.makeText(this, "You must pick an account",
						Toast.LENGTH_SHORT).show();
			}
		} else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR || requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
				&& resultCode == RESULT_OK) {
			Logger.Debug(LOG_TAG,
					"onActivityResult REQUEST_CODE_RECOVER_FROM_AUTH_ERROR");
			handleAuthorizeResult(resultCode, data);
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void getUsername() {
		Logger.Debug(LOG_TAG, "getUsername mEmail=" + mEmail);
		if (mEmail == null) {
			pickUserAccount();
		} else {
			if (_jalapenoWebServiceWraper.ServiceIsAvailable()) {
				getTask(RegisterActivity.this, mEmail, SCOPE).execute();
			} else {
				// Toast.makeText(this, "No network connection available",
				// Toast.LENGTH_SHORT).show();
				show("No network connection available");
			}
		}
	}

	/**
	 * Starts an activity in Google Play Services so the user can pick an
	 * account
	 */
	private void pickUserAccount() {
		Logger.Debug(LOG_TAG, "pickUserAccount");
		String[] accountTypes = new String[] { "com.google" };
		Intent intent = AccountPicker.newChooseAccountIntent(null, null,
				accountTypes, false, null, null, null, null);
		startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
	}

	private void handleAuthorizeResult(int resultCode, Intent data) {
		if (data == null) {
			show("Unknown error, click the button again");
			return;
		}
		if (resultCode == RESULT_OK) {
			Logger.Debug(LOG_TAG, "handleAuthorizeResult Retrying");
			getTask(this, mEmail, SCOPE).execute();
			return;
		}
		if (resultCode == RESULT_CANCELED) {
			show("User rejected authorization.");
			return;
		}
		show("Unknown error, click the button again");
	}

	public void show(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// mOut.setText(message);
				Toast.makeText(RegisterActivity.this, message,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void ShowToast(int res) {
		Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
	}

	public void handleException(final Exception e) {
		Logger.Error(LOG_TAG, "handleException", e);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (e instanceof GooglePlayServicesAvailabilityException) {
					// The Google Play services APK is old, disabled, or not
					// present.
					// Show a dialog created by Google Play services that allows
					// the user to update the APK
					int statusCode = ((GooglePlayServicesAvailabilityException) e)
							.getConnectionStatusCode();
					Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
							statusCode, RegisterActivity.this,
							REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
					dialog.show();
				} else if (e instanceof UserRecoverableAuthException) {
					// Unable to authenticate, such as when the user has not yet
					// granted
					// the app access to the account, but the user can fix this.
					// Forward the user to an activity in Google Play services.
					Intent intent = ((UserRecoverableAuthException) e)
							.getIntent();
					startActivityForResult(intent,
							REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
				}
			}
		});
	}

	private AbstractGetNameTask getTask(RegisterActivity activity,
			String email, String scope) {
		Logger.Debug(LOG_TAG, "getTask");
		return new GetNameInForeground(activity, email, scope);
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
				if (registerClient.ErrorMessage == WebErrorEnum.UserBanned) {
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
				registerClient.ErrorMessage = WebErrorEnum.NoConnection;
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
