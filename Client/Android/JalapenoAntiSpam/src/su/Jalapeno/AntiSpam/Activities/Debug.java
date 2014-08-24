package su.Jalapeno.AntiSpam.Activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import su.Jalapeno.AntiSpam.Billing.util.IabException;
import su.Jalapeno.AntiSpam.Billing.util.IabHelper;
import su.Jalapeno.AntiSpam.Billing.util.IabResult;
import su.Jalapeno.AntiSpam.Billing.util.Inventory;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.Services.AccessService;
import su.Jalapeno.AntiSpam.Services.BillingService;
import su.Jalapeno.AntiSpam.Services.ContactsService;
import su.Jalapeno.AntiSpam.Services.EmailSender;
import su.Jalapeno.AntiSpam.Services.NotifyService;
import su.Jalapeno.AntiSpam.Services.SenderService;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsReceiver;
import su.Jalapeno.AntiSpam.Services.Sms.SmsReceiverLogic;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.WebService.WebClient;
import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.Util.BillingConstants;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.CryptoService;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.ServiceFactory;
import su.Jalapeno.AntiSpam.Util.UI.AlertMessage;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.inject.Inject;

@ContentView(R.layout.debug)
public class Debug extends JalapenoActivity {
	@Inject
	ContactsService contactsService;
	@Inject
	JalapenoHttpService jalapenoHttpService;
	@Inject
	SettingsService _settingsService;

	@Inject
	AccessService _accessService;

	@InjectView(R.id.textDeviceInfo)
	TextView textDeviceInfo;

	IabHelper mHelper;
	BillingService _billingService;

	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "DebugActivity";

	SmsReceiverLogic _smsService;
	SmsReceiver _smsReceiver;
	Context _context;
	private String spamPhone = "+79999";
	private String normalPhone = "+78888";
	private int ACCOUNT_CODE = 16307;
	private String SCOPE_BASE = "audience:server:client_id:";
	private String SCOPE;
	private String CLIENT_ID = "140853970719-4ohgmn0eojg2qeh75r96m9iojpra4omr.apps.googleusercontent.com";
	private Debug mActivity;

	private NotifyService _ringtoneService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		SetEvent();
		Init();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SetBiling();
	}

	@Override
	public void onBackPressed() {
		Logger.Debug(LOG_TAG, "onBackPressed");
		UiUtils.NavigateAndClearHistory(SettingsActivity.class);
	}

	private void Init() {
		Logger.Debug(LOG_TAG, "Start debug");
		SCOPE = SCOPE_BASE + CLIENT_ID;
		// SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
		_context = getApplicationContext();
		_smsService = ServiceFactory.GetSmsReceiverLogic(_context);
		_smsReceiver = new SmsReceiver(_settingsService, _smsService);
		_ringtoneService = new NotifyService(_context);
		mActivity = this;

		_billingService = new BillingService(_context);
		
	}

	private void SetBiling() {
		CryptoService cr = new CryptoService();
		String base64EncodedPublicKey = cr.Decrypt(BillingConstants.ENCYPTED_LICENCE_KEY);

		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		Logger.Debug(LOG_TAG, "mHelper ready");

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {

				Logger.Debug(LOG_TAG, "onIabSetupFinished");
				if (!result.isSuccess()) {
					ShowToast(R.string.ErrorBilling);
					// Oh noes, there was a problem.
					Logger.Error(LOG_TAG, "Problem setting up In-app Billing: " + result);
				}
				// Hooray, IAB is fully set up!
				Logger.Debug(LOG_TAG, "Billing initialized");
				CheckAccess();
			}
		});
	}

	protected void CheckAccess() {
		_billingService.Init(this, mHelper);
		boolean checkPurchase = _billingService.CheckPurchaseSync();
		SetText(checkPurchase);
	}

	private void SetText(boolean checkPurchase) {
		String info = String.format(
				"Enabled: %b, Registered: %b, AccesAllowed: %b, DaysLast: %d, Unlimited: %b, ClientId: %s, Domain: %s, Purchase: %b",
				_settingsService.AntispamEnabled(), _settingsService.ClientIsRegistered(),
				_settingsService.GetAccessInfo().AccessIsAllowed, _settingsService.GetAccessInfo().EvaluationDaysLast,
				_settingsService.GetAccessInfo().IsUnlimitedAccess, _settingsService.GetClientId(), _settingsService.GetDomain(),
				checkPurchase);
		textDeviceInfo.setText(info);
	}

	private void SetEvent() {
		UiUtils.SetTapForButton(R.id.buttonAuthorization, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Authorization();
			}
		});

		UiUtils.SetTapForButton(R.id.buttonInfo, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Info();
			}
		});

		UiUtils.SetTapForButton(R.id.buttonNewSms, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NewSms();
			}
		});

		UiUtils.SetTapForButton(R.id.buttonSmsSpam, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Spam();
			}
		});

		UiUtils.SetTapForButton(R.id.buttonClearSpam, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ClearSpam();
			}
		});

		UiUtils.SetTapForButton(R.id.buttonFillSpam, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FillSpam();
			}
		});

		UiUtils.SetTapForButton(R.id.buttonTestInBaseSpam, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TestInSpam();
			}
		});

		UiUtils.SetTapForButton(R.id.buttonRecieve9999, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Receive9999();
			}
		});

		UiUtils.SetTapForButton(R.id.buttonRecieve8888, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Receive8888();
			}
		});

		UiUtils.SetTapForButton(R.id.buttonLocalhostRequest, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LocalhostRequest();
			}
		});
		UiUtils.SetTapForButton(R.id.buttonSendTokenToEmail, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SentTokenEmail();
			}
		});

		UiUtils.SetTapForButton(R.id.buttonSettingsTest, new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		UiUtils.SetTapForButton(R.id.button7968InCont, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InContacts("+79689264552");
			}
		});

		UiUtils.SetTapForButton(R.id.button8968InCont, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InContacts("89689264552");
			}
		});
		UiUtils.SetTapForButton(R.id.button32424InCont, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InContacts("545454654564");
			}
		});
	}

	public void DropRegister(View v) {
		_settingsService.DropRegistration();
		startService(new Intent(this, AppService.class));
	}
	
	public void Consume(View v) {
		_billingService.ConsumePurchaseAsyncNoWait();
	}
	
	public void GoToBuy(View v) {
		_accessService.HandleAccessNotAllowed(true);
		UiUtils.NavigateTo(BillingActivity.class);
		// startService(new Intent(this, AppService.class));
	}

	protected void InContacts(String ph) {
		boolean InC = contactsService.PhoneInContact(ph);
		AlertMessage.Alert(mActivity, String.format("%s In cont: %s", ph, InC));
	}

	protected void InContactsWrong() {
		boolean InC = contactsService.PhoneInContact("89689264552");
		AlertMessage.Alert(mActivity, String.format("89689264552 In cont: %s", InC));
	}

	void getAndUseAuthTokenBlocking(String token) {
		EmailSender emailSender = new EmailSender(this);
		emailSender.SendEmail("lexruster@gmail.com;timur.khodzhaev@gmail.com;", "Token", "Token:" + token);
	}

	private void LocalhostRequest() {
		boolean avail = jalapenoHttpService.ServiceIsAvailable();
		Toast.makeText(this, String.format("Available: %s", avail), Toast.LENGTH_LONG).show();

		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				Logger.Debug(LOG_TAG, "LocalhostRequest");
				String resp = WebClient.Get("https://10.0.2.2/wcf/Service.svc/Test");
				Logger.Debug(LOG_TAG, resp);
				Toast.makeText(Debug.this, String.format("Responce: %s", resp.toString()), Toast.LENGTH_LONG).show();
				return resp;
			}
		};

		task.execute();

	}

	private void Receive(String phone, String body) {
		Sms sms = new Sms();
		sms.SenderId = phone;
		sms.Text = body;
		_smsReceiver.Receive(sms, _context);
	}

	private void Receive8888() {
		Receive(normalPhone, "test no spam body");
	}

	private void Receive9999() {
		Receive(spamPhone, "test spam body!");
	}

	private void TestInSpam() {
		SenderService spamBase = new SenderService(RepositoryFactory.getRepository());
		Boolean testResult = spamBase.PhoneIsSpammer(spamPhone);
		Toast.makeText(this, String.format("Test: %s", testResult.toString()), Toast.LENGTH_LONG).show();
	}

	private void FillSpam() {
		SenderService spamBase = new SenderService(RepositoryFactory.getRepository());
		Random rnd = new Random();
		for (int i = 0; i < 20; ++i) {
			int random = rnd.nextInt();
			if (random < 0) {
				random *= -1;
			}
			String phone = String.format(Locale.ENGLISH, "+7%d", random);
			String normalPhone = phone;
			spamBase.AddOrUpdateSender(normalPhone, true);
		}
		spamBase.AddOrUpdateSender(spamPhone, true);
	}

	private void ClearSpam() {
		SenderService spamBase = new SenderService(RepositoryFactory.getRepository());
		spamBase.Clear();
	}

	private void Info() {
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String number = tm.getLine1Number();

		String imei = tm.getDeviceId();
		Toast.makeText(this, String.format("%s - %s", number, imei), Toast.LENGTH_LONG).show();
	}

	private void Authorization() {
		Intent intent = com.google.android.gms.common.AccountPicker.newChooseAccountIntent(null, null, new String[] { "com.google" },
				false, null, null, null, null);
		startActivityForResult(intent, 13);
	}

	private void Spam() {
		Toast.makeText(this, "Spam", Toast.LENGTH_LONG).show();
		/*
		 * MediaPlayer mp = MediaPlayer.create(Debug.this, R.raw.cartoon003);
		 * mp.start();
		 */
		_ringtoneService.OnIncomeSms();
	}

	/*
	 * Uri notification =
	 * RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	 * Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
	 * notification); r.play();
	 */

	private void NewSms() {
		Toast.makeText(this, "SMS", Toast.LENGTH_LONG).show();
		/*
		 * MediaPlayer mp = MediaPlayer.create(Debug.this, R.raw.cartoon010);
		 * mp.start();
		 */
		_ringtoneService.ContactRingtone();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		Logger.Debug(LOG_TAG, "onActivityResult resultCode=" + resultCode);
		if (requestCode == 13 && resultCode == RESULT_OK) {
			String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			Toast.makeText(this, accountName, Toast.LENGTH_LONG).show();
		}

		if (requestCode == ACCOUNT_CODE && resultCode == RESULT_OK) {
			String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			Logger.Debug(LOG_TAG, "accountName =" + accountName);
			SentTokenEmail2(accountName);
		}
	}

	private void SentTokenEmail() {
		// Intent intent = AccountPicker.newChooseAccountIntent(null, null, new
		// String[] { "com.google" }, false, null, null, null, null);
		// startActivityForResult(intent, ACCOUNT_CODE);
		Logger.Debug(LOG_TAG, "SentTokenEmail");
		String[] accountTypes = new String[] { "com.google" };
		Intent intent = AccountPicker.newChooseAccountIntent(null, null, accountTypes, true, null, null, null, null);
		startActivityForResult(intent, ACCOUNT_CODE);
	}

	private void SentTokenEmail2(final String accountName) {
		/*
		 * AccountManager accountManager =
		 * AccountManager.get(getApplicationContext()); Account[] allAccounts =
		 * accountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		 * Account myAccount = null; for (Account account : allAccounts) { if
		 * (account.name.equals(accountName)) { myAccount = account; } }
		 */
		Logger.Debug(LOG_TAG, "SentTokenEmail2");
		final String[] error = new String[1];
		error[0] = "";
		AlertMessage.Alert(mActivity, accountName);

		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String token = "";
				/*
				 * try { Log.i(TAG, "Begin get token"); token =
				 * GoogleAuthUtil.getToken(Debug.this, accountName, SCOPE); }
				 * catch (IOException transientEx) { // Network or server error,
				 * try later Log.e(TAG, transientEx.toString()); } catch
				 * (UserRecoverableAuthException e) { // Recover (with
				 * e.getIntent()) Log.e(TAG, e.toString()); Intent recover =
				 * e.getIntent(); startActivityForResult(recover, ACCOUNT_CODE);
				 * } catch (GoogleAuthException authEx) { // The call is not
				 * ever expected to succeed // assuming you have already
				 * verified that // Google Play services is installed.
				 * Log.e(TAG, authEx.toString()); }
				 */
				// String token = null;
				try {

					Logger.Debug(LOG_TAG, "Scope: " + SCOPE);
					Logger.Debug(LOG_TAG, "Email: " + accountName);
					token = GoogleAuthUtil.getToken(mActivity, accountName, SCOPE);
					Logger.Debug(LOG_TAG, "token: " + token);
				} catch (GooglePlayServicesAvailabilityException playEx) {
					error[0] = playEx.getMessage();
					Dialog dialog = GooglePlayServicesUtil.getErrorDialog(playEx.getConnectionStatusCode(), Debug.this, 1);
					dialog.show();
					// Use the dialog to present to the user.
				} catch (UserRecoverableAuthException recoverableException) {
					error[0] = recoverableException.getMessage();
					Intent recoveryIntent = recoverableException.getIntent();
					// Use the intent in a custom dialog or just
					// startActivityForResult.
					Debug.this.startActivityForResult(recoveryIntent, 2);
				} catch (GoogleAuthException authEx) {
					// This is likely unrecoverable.
					error[0] = "Unrecoverable authentication exception: " + authEx.getMessage();
					Logger.Error(LOG_TAG, "Unrecoverable authentication exception: " + authEx.getMessage(), authEx);

					return "";
				} catch (IOException ioEx) {
					error[0] = "transient error encountered: " + ioEx.getMessage();
					Logger.Debug(LOG_TAG, "transient error encountered: " + ioEx.getMessage());

					// doExponentialBackoff();
					return "";
				}

				Logger.Debug(LOG_TAG, "return token: " + token);
				return token;
			}

			@Override
			protected void onPostExecute(String token) {
				Logger.Debug(LOG_TAG, "Access token retrieved:" + token);
				if (error[0] != "") {
					AlertMessage.Alert(mActivity, error[0]);
				}
				if (token != "") {
					AlertMessage.Alert(mActivity, token);
					getAndUseAuthTokenBlocking(token);
				}
			}
		};

		task.execute();
	}
}
