package su.Jalapeno.AntiSpam.Activities;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import su.Jalapeno.AntiSpam.Activities.Tasks.RegisterTask;
import su.Jalapeno.AntiSpam.Activities.Tasks.TestRegisterTask;
import su.Jalapeno.AntiSpam.FilterPro.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Util.BillingConstants;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.CryptoService;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.StrictPolicy;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import su.Jalapeno.AntiSpam.Util.UI.Spiner;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ResponseData;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.inject.Inject;

@ContentView(R.layout.activity_register)
public class RegisterActivity extends JalapenoActivity {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "RegisterActivity";
	static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
	static final int REQUEST_CODE_PICK_ACCOUNT = 13000;
	static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 13001;
	static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 13002;

	public static final String EXTRA_ACCOUNTNAME = "extra_accountname";

	@Inject
	public JalapenoWebServiceWraper _jalapenoWebServiceWraper;
	@Inject
	Context _context;
	@Inject
	public SettingsService _settingsService;
	@InjectView(R.id.licenseLink)
	public TextView _licenseLink;

	@InjectView(R.id.buttonDebugRegister)
	Button buttonDebugRegister;

	@InjectView(R.id.buttonGoToDebugRegister)
	Button buttonGoToDebug;
	private String link;

	public String Email;
	public String Token;
	public String SCOPE;
	// from web app id
	final private String WEB_CLIENT_ID = "140853970719-4ohgmn0eojg2qeh75r96m9iojpra4omr.apps.googleusercontent.com";

	private LicenseCheckerCallback mSmsExtraDecoder;
	private LicenseChecker mMmsData;
	// A handler on the UI thread.
	private Handler mHandler;
	public String PaidOrderId;
	private StrictPolicy policy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SetDebugMode(Constants.VIEW_DEBUG_UI);

		// spiner=new Spiner(this);
		link = _context.getResources().getString(R.string.LicenseAgreementUrl);
		_licenseLink.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
				startActivity(browserIntent);
			}
		});

		Logger.Debug(LOG_TAG, "mGoogleApiClient build");
		SCOPE = String.format("audience:server:client_id:%s", WEB_CLIENT_ID);
		Logger.Debug(LOG_TAG, "onCreate");

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(EXTRA_ACCOUNTNAME)) {
			Email = extras.getString(EXTRA_ACCOUNTNAME);
			GetRegiseterTask().execute();
		}

		mHandler = new Handler();
		mSmsExtraDecoder = new SmsExtraDecoder();
		policy = new StrictPolicy();
		mMmsData = new LicenseChecker(this, policy, getP());
	}

	@Override
	protected void onResume() {
		super.onResume();
		Logger.Debug(LOG_TAG, "onResume");
		Resume();
	}

	private void Resume() {
		if (_settingsService.ClientIsRegistered()) {
			Logger.Error(LOG_TAG, "Init clientRegistered!");
			UiUtils.NavigateAndClearHistory(SettingsActivity.class);
		}
	}

	@Override
	public void onBackPressed() {
		Logger.Debug(LOG_TAG, "onBackPressed");
		UiUtils.NavigateToExit();
	}

	public void Register(View view) {
		Logger.Debug(LOG_TAG, "Register");
		Email = "";
		Token = "";
		if (checkPlayServices()) {
			HeaderIsValid();
		}
	}

	private boolean checkPlayServices() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (status != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
				ShowErrorDialog(status);
			} else {
				ShowToast(R.string.DeviceNotSupported);
			}
			return false;
		}
		return true;
	}

	void ShowErrorDialog(int code) {
		GooglePlayServicesUtil.getErrorDialog(code, this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
	}

	public void TestRegister(View view) {
		Logger.Debug(LOG_TAG, "TestRegister");
		if (Constants.VIEW_DEBUG_UI) {
			new TestRegisterTask(this, _settingsService, _jalapenoWebServiceWraper).execute();
		}
	}

	public void NavigateToDebug(View v) {
		if (Constants.VIEW_DEBUG_UI) {
			UiUtils.NavigateTo(Debug.class);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
			if (resultCode == RESULT_OK) {
				Logger.Debug(LOG_TAG, "onActivityResult RESULT_OK");
				Email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				getUsername();
			} else if (resultCode == RESULT_CANCELED) {
				Logger.Debug(LOG_TAG, "onActivityResult RESULT_CANCELED");
				ShowToast(R.string.NeedPeekAccount);
			}
		} else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR || requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
				&& resultCode == RESULT_OK) {
			Logger.Debug(LOG_TAG, "onActivityResult REQUEST_CODE_RECOVER_FROM_AUTH_ERROR");
			handleAuthorizeResult(resultCode, data);
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void getUsername() {
		Logger.Debug(LOG_TAG, "getUsername mEmail=" + Email);
		if (TextUtils.isEmpty(Email)) {
			pickUserAccount();
		} else {
			if (_jalapenoWebServiceWraper.ServiceIsAvailable()) {
				GetRegiseterTask().execute();
			} else {
				Logger.Debug(LOG_TAG, "getUsername mEmail=" + Email + " No NET");
				ShowToast(R.string.ErrorRegister);
			}
		}
	}

	private void pickUserAccount() {
		Logger.Debug(LOG_TAG, "pickUserAccount");
		String[] accountTypes = new String[] { "com.google" };
		Intent intent = AccountPicker.newChooseAccountIntent(null, null, accountTypes, false, null, null, null, null);
		startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
	}

	private void handleAuthorizeResult(int resultCode, Intent data) {
		if (data == null) {
			ShowToast(R.string.ErrorRegister);
			Logger.Debug(LOG_TAG, "Unknown error, click the button again");
			return;
		}
		if (resultCode == RESULT_OK) {
			Logger.Debug(LOG_TAG, "handleAuthorizeResult Retrying");
			GetRegiseterTask().execute();
			return;
		}
		if (resultCode == RESULT_CANCELED) {
			Logger.Debug(LOG_TAG, "User rejected authorization");
			ShowToast(R.string.ErrorRegister);
			return;
		}
		Logger.Debug(LOG_TAG, "Unknown error, click the button again");
		ShowToast(R.string.ErrorRegister);
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
					int statusCode = ((GooglePlayServicesAvailabilityException) e).getConnectionStatusCode();
					Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, RegisterActivity.this,
							REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
					dialog.show();
				} else if (e instanceof UserRecoverableAuthException) {
					// Unable to authenticate, such as when the user has not yet
					// granted
					// the app access to the account, but the user can fix this.
					// Forward the user to an activity in Google Play services.
					Intent intent = ((UserRecoverableAuthException) e).getIntent();
					startActivityForResult(intent, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
				}
			}
		});
	}

	private RegisterTask GetRegiseterTask() {
		Logger.Debug(LOG_TAG, "getTask");
		return new RegisterTask(this, _settingsService, _jalapenoWebServiceWraper);
	}

	private void SetDebugMode(boolean isDebug) {
		if (isDebug) {
			buttonDebugRegister.setVisibility(View.VISIBLE);
			buttonGoToDebug.setVisibility(View.VISIBLE);
		} else {
			buttonDebugRegister.setVisibility(View.INVISIBLE);
			buttonGoToDebug.setVisibility(View.INVISIBLE);
		}
	}

	// //////////// lic ver //////

	private void HeaderIsValid() {
		Logger.Debug(LOG_TAG, "Check license strt start");
		setProgressBarIndeterminateVisibility(true);
		mMmsData.checkAccess(mSmsExtraDecoder);
	}

	private String getP() {
		CryptoService cr = new CryptoService();
		String base64EncodedPublicKey = cr.Decrypt(BillingConstants.ENCYPTED_LICENCE_KEY);

		return base64EncodedPublicKey;
	}

	private void FooterComplete() {
		mHandler.post(new Runnable() {
			public void run() {
				setProgressBarIndeterminateVisibility(false);
				ResponseData response = policy.Response;
				Logger.Debug(LOG_TAG, "Check license success extra =" + response.extra);
				PaidOrderId = "get from some code";
				getUsername();
			}
		});
	}

	private void CornerCalc() {
		mHandler.post(new Runnable() {
			public void run() {
				setProgressBarIndeterminateVisibility(false);
			}
		});
	}

	private void RoundTrip() {
		mHandler.post(new Runnable() {
			public void run() {
				Logger.Debug(LOG_TAG, "Go to market");
				Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://market.android.com/details?id=" + getPackageName()));
				startActivity(marketIntent);
			}
		});
	}

	private class SmsExtraDecoder implements LicenseCheckerCallback {
		public void allow(int reason) {
			if (isFinishing()) {
				return;
			}
			Logger.Debug(LOG_TAG, "MyLicenseCheckerCallback allow reason=" + reason);

			FooterComplete();
		}

		public void dontAllow(int reason) {
			Logger.Debug(LOG_TAG, "MyLicenseCheckerCallback dontAllow reason=" + reason);
			CornerCalc();
			if (isFinishing()) {
				return;
			}
			if (reason == Policy.RETRY) {
				ShowToast(R.string.ErrorRegister);
			} else {
				ShowToast(getString(R.string.NeedTrust));
				RoundTrip();
			}
		}

		public void applicationError(int errorCode) {
			if (isFinishing()) {
				return;
			}
			Logger.Debug(LOG_TAG, "MyLicenseCheckerCallback applicationError error=" + errorCode);
			ShowToast(R.string.ErrorRegister);
			dontAllow(0);
		}
	}
}
