package su.Jalapeno.AntiSpam.Activities;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import su.Jalapeno.AntiSpam.Billing.util.IabHelper;
import su.Jalapeno.AntiSpam.Billing.util.IabResult;
import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.CryptoService;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.inject.Inject;

@ContentView(R.layout.activity_billing)
public class BillingActivity extends JalapenoActivity {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "BillingActivity";
	@Inject
	Context _context;
	@Inject
	public SettingsService _settingsService;

	IabHelper mHelper;

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
		/*
		 * if (config.ClientRegistered) { Logger.Error(LOG_TAG,
		 * "Init clientRegistered!");
		 * UiUtils.NavigateAndClearHistory(SettingsActivity.class); }
		 */
	}

	@Override
	public void onBackPressed() {
		Logger.Debug(LOG_TAG, "onBackPressed");
		UiUtils.NavigateAndClearHistory(SettingsActivity.class);
	}

	public void Buy(View view) {
		Logger.Debug(LOG_TAG, "Buy");
		CryptoService cr = new CryptoService();
		String base64EncodedPublicKey = cr
				.Decrypt(Constants.ENCYPTED_LICENCE_KEY);

		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					Logger.Debug(LOG_TAG, "Problem setting up In-app Billing: "
							+ result);
				}
				// Hooray, IAB is fully set up!
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null)
			mHelper.dispose();
		mHelper = null;
	}
}
