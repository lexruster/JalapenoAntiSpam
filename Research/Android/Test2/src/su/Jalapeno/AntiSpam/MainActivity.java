package su.Jalapeno.AntiSpam;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;


import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;



import java.io.IOException;
import su.Jalapeno.AntiSpam.R;


public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	Context _context;

	private int ACCOUNT_CODE = 16307;
	private String SCOPE_BASE = "audience:server:client_id:";
	private String SCOPE;
	private String CLIENT_ID = "140853970719-g5ol9mh39f2trarq7f0qt353gdens3sl.apps.googleusercontent.com";
	private MainActivity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Init();

	}

	private void Init() {
		Log.i(TAG, "Start debug");
		SCOPE = SCOPE_BASE + CLIENT_ID;
		_context = getApplicationContext();

		SetTapForButton(R.id.button1, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SentTokenEmail();
			}
		});

	}

	private void SentTokenEmail() {
		Intent intent = AccountPicker.newChooseAccountIntent(null, null,
				new String[] { "com.google" }, false, null, null, null, null);
		startActivityForResult(intent, ACCOUNT_CODE);
	}

	public void SetTapForButton(int id, View.OnClickListener onClickListener) {
		Button button = (Button) this.findViewById(id);
		View.OnClickListener tapListener = onClickListener;
		button.setOnClickListener(tapListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (requestCode == 13 && resultCode == RESULT_OK) {
			String accountName = data
					.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			Toast.makeText(this, accountName, Toast.LENGTH_LONG).show();
		}

		if (requestCode == ACCOUNT_CODE && resultCode == RESULT_OK) {
			String accountName = data
					.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			SentTokenEmail2(accountName);
		}

	}

	void getAndUseAuthTokenBlocking(String token) {
		EmailSender emailSender = new EmailSender(this);
		emailSender.SendEmail("lexruster@gmail.com;timur.khodzhaev@gmail.com;",
				"Token", "Token:" + token);
	}

	private void SentTokenEmail2(final String accountName) {
		mActivity = this;
		final String[] error = new String[1];
		error[0] = "";
		DebugMessage.Debug(mActivity, accountName);

		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String token = "";
				try {

					Log.i(TAG, "Scope: " + SCOPE);
					Log.i(TAG, "Email: " + accountName);
					token = GoogleAuthUtil.getToken(mActivity, accountName,
							SCOPE);
				} catch (GooglePlayServicesAvailabilityException playEx) {
					error[0] = playEx.getMessage();
					Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
							playEx.getConnectionStatusCode(),
							MainActivity.this, 1);
					dialog.show();
					// Use the dialog to present to the user.
				} catch (UserRecoverableAuthException recoverableException) {
					error[0] = recoverableException.getMessage();
					Intent recoveryIntent = recoverableException.getIntent();
					MainActivity.this.startActivityForResult(recoveryIntent, 2);
				} catch (GoogleAuthException authEx) {
					error[0] = "Unrecoverable authentication exception: "
							+ authEx.getMessage();
					Log.e(TAG, "Unrecoverable authentication exception: "
							+ authEx.getMessage(), authEx);

					return "";
				} catch (IOException ioEx) {
					error[0] = "transient error encountered: "
							+ ioEx.getMessage();
					Log.i(TAG,
							"transient error encountered: " + ioEx.getMessage());
					return "";
				}

				return token;
			}

			@Override
			protected void onPostExecute(String token) {
				Log.i(TAG, "Access token retrieved:" + token);
				if (error[0] != "") {
					DebugMessage.Debug(mActivity, error[0]);
				}
				if (token != "") {
					DebugMessage.Debug(mActivity, token);
					getAndUseAuthTokenBlocking(token);
				}
			}

		};

		task.execute();
	}
}
