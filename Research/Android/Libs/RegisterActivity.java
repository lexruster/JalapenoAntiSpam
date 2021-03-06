package su.Jalapeno.AntiSpam.Activities;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
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
import android.content.IntentSender.SendIntentException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;
import com.google.inject.Inject;

@ContentView(R.layout.activity_register)
public class RegisterActivity extends JalapenoActivity implements/* com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks,*/ com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener, ConnectionCallbacks
{
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "RegisterActivity";
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

	private String link;

	public String Email;
	public String Token;
	static String SCOPE;
	 //private static final String SCOPE =
	 //"oauth2:https://www.googleapis.com/auth/userinfo.profile
	// audience:server:client_id:140853970719-javp5dr54lnale1hvr0cc2iujeoq2t46.apps.googleusercontent.com";
	// final private String CLIENT_ID =
	// "140853970719-javp5dr54lnale1hvr0cc2iujeoq2t46.apps.googleusercontent.com";
	// from web app id
	final private String WEB_CLIENT_ID = "140853970719-4ohgmn0eojg2qeh75r96m9iojpra4omr.apps.googleusercontent.com"; 
	final private List<String> SCOPES = Arrays.asList(new String[] {
	 //"https://www.googleapis.com/auth/plus.login",
	 "profile"
	//"https://www.googleapis.com/auth/userinfo.profile",
	// "https://www.googleapis.com/auth/userinfo.email"
			});
	
	 /* Request code used to invoke sign in user interactions. */
	  private static final int RC_SIGN_IN = 615874;

	  //private GoogleApiClient mGoogleApiClient;
	  private PlusClient mGoogleApiClient;

	  /* A flag indicating that a PendingIntent is in progress and prevents
	   * us from starting further intents.
	   */
	  private boolean mIntentInProgress;
	  private boolean mSignInClicked;

	  /* Store the connection result from onConnectionFailed callbacks so that we can
	   * resolve them when the user clicks sign-in.
	   */
	  private ConnectionResult mConnectionResult;
	  

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		link = _context.getResources().getString(R.string.LicenseAgreementUrl);
		_licenseLink.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
				startActivity(browserIntent);

			}
		});
		
		/*mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener( this)
        .addApi(Plus.API, null)
        .addScope(Plus.SCOPE_PLUS_LOGIN)
        .build();*/
		mGoogleApiClient = new PlusClient.Builder(this, this, this)
         .setActions("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
         .setScopes("PLUS_LOGIN")
         .build();
		Logger.Debug(LOG_TAG, "mGoogleApiClient build");

		 //SCOPE = String.format("audience:server:client_id:%s:api_scope:%s", CLIENT_ID, TextUtils.join(" ", SCOPES));
		//SCOPE = String.format("audience:server:client_id:%s", WEB_CLIENT_ID);
		//SCOPE = String.format("oauth2:server:client_id:%s:api_scope:%s", WEB_CLIENT_ID, TextUtils.join(" ", SCOPES));
		 SCOPE = String.format("oauth2:%s", TextUtils.join(" ", SCOPES));
		
		//SCOPE = String.format("audience:server:client_id:%s", CLIENT_ID);

		Logger.Debug(LOG_TAG, "onCreate");

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(EXTRA_ACCOUNTNAME)) {
			Email = extras.getString(EXTRA_ACCOUNTNAME);
			GetRegiseterTask().execute();
		}
	}
	
	@Override
    public void onDisconnected() {
        Logger.Debug(LOG_TAG, "disconnected");
    }
		  private void resolveSignInError() {
			  Logger.Debug(LOG_TAG, "resolveSignInError");
			  if (mConnectionResult.hasResolution()) {
			    try {
			      mIntentInProgress = true;
			      mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			    } catch (SendIntentException e) {
			      // The intent was canceled before it was sent.  Return to the default
			      // state and attempt to connect to get an updated ConnectionResult.
			      mIntentInProgress = false;
			      mGoogleApiClient.connect();
			    }
			  }
			}

		  
			public void onConnectionFailed(ConnectionResult result) {
				Logger.Debug(LOG_TAG, "onConnectionFailed");
			  if (!mIntentInProgress) {
			    // Store the ConnectionResult so that we can use it later when the user clicks
			    // 'sign-in'.
			    mConnectionResult = result;

			    if (mSignInClicked) {
			      // The user has already clicked 'sign-in' so we attempt to resolve all
			      // errors until the user is signed in, or they cancel.
			      resolveSignInError();
			    }
			  }
			}
			
		
			
			public void onConnected(Bundle connectionHint) {
				 mSignInClicked = false;
				 Logger.Debug(LOG_TAG, "onConnected");
				  Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
				  getUsername();
			}
			
			
			public void onConnectionSuspended(int cause) {
				Logger.Debug(LOG_TAG, "onConnectionSuspended");
				  mGoogleApiClient.connect();
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
		Email = "";
		Token = "";
		mSignInClicked = true;
		mGoogleApiClient.connect();
		 if(!mGoogleApiClient.isConnecting()) {
			    mSignInClicked = true;
			    resolveSignInError();
		 }
			    
		//getUsername();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 if (requestCode == RC_SIGN_IN) {
			 Logger.Debug(LOG_TAG, "onActivityResult RC_SIGN_IN");
			    if (resultCode != RESULT_OK) {
			      mSignInClicked = false;
			    }

			    mIntentInProgress = false;

			    if (!mGoogleApiClient.isConnecting()) {
			      mGoogleApiClient.connect();
			    }
			  }
		
		
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

	public void show(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void ShowToast(final int res) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(RegisterActivity.this, res, Toast.LENGTH_SHORT).show();
			}
		});
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
		return new RegisterTask();
	}

	class RegisterTask extends AsyncTask<RegisterActivity, Void, RegisterClientResponse> {
		final String LOG_TAG = Constants.BEGIN_LOG_TAG + "RegisterTask";
		protected RegisterActivity activity;

		Spiner spiner;

		public RegisterTask() {
			activity = RegisterActivity.this;
			spiner = new Spiner(activity);
		}

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

		// @SuppressWarnings("deprecation")
		@Override
		protected RegisterClientResponse doInBackground(RegisterActivity... activitis) {
			Logger.Debug(LOG_TAG, "doInBackground");
			RegisterClientResponse registerClient = new RegisterClientResponse();
			registerClient.ErrorMessage = WebErrorEnum.NoConnection;
			registerClient.WasSuccessful = false;

			try {
				fetchToken();
				Logger.Debug(LOG_TAG, "fetchNameFromProfileServer token=" + activity.Token);
			} catch (IOException ex) {
				onError("Following Error occured, please try again. " + ex.getMessage(), ex);
			} catch (Exception ex) {
				Logger.Error(LOG_TAG, "doInBackground Exception", ex);
			}
			// return null;

			if (TextUtils.isEmpty(activity.Token)) {
				return registerClient;
			}

			PublicKeyResponse pbk = _jalapenoWebServiceWraper.GetPublicKey();

			Logger.Debug(LOG_TAG, "doInBackground GetPublicKey  " + pbk.WasSuccessful);
			if (pbk.WasSuccessful) {
				PublicKeyInfo publicKeyInfo = CryptoService.GetPublicKeyInfo(pbk.PublicKey);
				_settingsService.UpdatePublicKey(publicKeyInfo);
			} else {
				return registerClient;
			}

			Config config = _settingsService.LoadSettings();
			config.ClientId = UUID.randomUUID();
			RegisterClientRequest request = new RegisterClientRequest();
			request.ClientId = config.ClientId;
			request.Token = activity.Token;
			_settingsService.SaveSettings(config);

			registerClient = _jalapenoWebServiceWraper.RegisterClient(request);

			return registerClient;
		}

		protected void onError(String msg, Exception e) {
			if (e != null) {
				Logger.Error(LOG_TAG, "Exception: ", e);
			}
			activity.show(msg); // will be run in UI thread
		}

		private void fetchToken() throws IOException {
			String token = null;
			try {
				Logger.Debug(LOG_TAG, "fetchToken scope: " + RegisterActivity.SCOPE);
				token = GoogleAuthUtil.getToken(activity, activity.Email, RegisterActivity.SCOPE);
			} catch (UserRecoverableAuthException userRecoverableException) {
				// GooglePlayServices.apk is either old, disabled, or not
				// present, which is
				// recoverable, so we need to show the user some UI through the
				// activity.
				activity.handleException(userRecoverableException);
			} catch (GoogleAuthException fatalException) {
				onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
			}

			if (token == null) {
				return;
			}

			activity.Token = token;
		}
	}

	
}
