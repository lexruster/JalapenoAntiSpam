package su.Jalapeno.AntiSpam.Activities;

import java.util.ArrayList;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import su.Jalapeno.AntiSpam.Activities.Tasks.TestPurchaseAntispamTask;
import su.Jalapeno.AntiSpam.Billing.util.IabException;
import su.Jalapeno.AntiSpam.Billing.util.IabHelper;
import su.Jalapeno.AntiSpam.Billing.util.IabResult;
import su.Jalapeno.AntiSpam.Billing.util.Inventory;
import su.Jalapeno.AntiSpam.Billing.util.SkuDetails;
import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.NotifyAboutPaymentRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.NotifyAboutPaymentResponse;
import su.Jalapeno.AntiSpam.Util.BillingConstants;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.CryptoService;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.inject.Inject;

@ContentView(R.layout.activity_billing)
public class BillingActivity extends JalapenoActivity {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "BillingActivity";
	final int PURCHASE_RESULT = 941952;

	@Inject
	Context _context;
	@Inject
	public SettingsService _settingsService;

	@Inject
	JalapenoWebServiceWraper _jalapenoWebServiceWraper;

	@InjectView(R.id.buttonDebugPurchase)
	Button buttonDebug;

	IabHelper mHelper;
	protected boolean hasPremium;

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
	}

	private void Init() {
		SetDebugMode(Constants.VIEW_DEBUG_UI);
	}

	@Override
	public void onBackPressed() {
		Logger.Debug(LOG_TAG, "onBackPressed");
		UiUtils.NavigateAndClearHistory(SettingsActivity.class);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PURCHASE_RESULT) {
			Logger.Debug(LOG_TAG, "onActivityResult PURCHASE_RESULT");
			if (resultCode == RESULT_OK) {
				Logger.Debug(LOG_TAG, "onActivityResult RESULT_OK");
				ShowToast(R.string.PurchaseComplete);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public void BuyTest(View view) {
		Logger.Debug(LOG_TAG, "BuyTest");
		if (Constants.VIEW_DEBUG_UI) {
			new TestPurchaseAntispamTask(this, _settingsService, _jalapenoWebServiceWraper).execute();
		}
	}

	public void Buy(View view) {
		Logger.Debug(LOG_TAG, "Buy pressed");
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
		Logger.Debug(LOG_TAG, "CheckAccess");
		ArrayList<String> additionalSkuList = new ArrayList<String>();
		additionalSkuList.add(BillingConstants.ANTISPAM_ACCESS);

		try {
			Inventory inventory = mHelper.queryInventory(true, additionalSkuList);

			BuyAccess(inventory);

		} catch (IabException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Logger.Error(LOG_TAG, "ProceedWithPurchase error", e);
			ShowToast(R.string.ErrorBilling);
		}
	}

	private void BuyAccess(Inventory inventory) {
		Logger.Debug(LOG_TAG, "BuyAccess");
		SkuDetails skuDetails = inventory.getSkuDetails(BillingConstants.ANTISPAM_ACCESS);

		if (inventory.hasPurchase(BillingConstants.ANTISPAM_ACCESS)) {
			Logger.Debug(LOG_TAG, "ANTISPAM_ACCESS already purchase");
			ShowToast(R.string.PurchaseComplete);

			ActivateAccess();
		}

		String accessPrice = skuDetails.getPrice();
		Logger.Debug(LOG_TAG, "Available access with cost " + accessPrice);

		IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
			public void onIabPurchaseFinished(IabResult result, su.Jalapeno.AntiSpam.Billing.util.Purchase purchase) {
				if (result.isFailure()) {
					Logger.Debug(LOG_TAG, "Error purchasing: " + result);
					ShowToast(R.string.ErrorBilling);
					return;
				} else if (purchase.getSku().equals(BillingConstants.ANTISPAM_ACCESS)) {
					Logger.Debug(LOG_TAG, "onIabPurchaseFinished Successfully result");
					CheckComplete();
				}
			}
		};

		mHelper.launchPurchaseFlow(this, BillingConstants.ANTISPAM_ACCESS, PURCHASE_RESULT, mPurchaseFinishedListener, "");
	}

	protected void CheckComplete() {
		Logger.Debug(LOG_TAG, "CheckComplete");

		IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
			public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

				if (result.isFailure()) {

				} else {
					hasPremium = inventory.hasPurchase(BillingConstants.ANTISPAM_ACCESS);

					Logger.Debug(LOG_TAG, "ANTISPAM_ACCESS  purchase status " + hasPremium);
					if (hasPremium) {
						ShowToast(R.string.PurchaseComplete);
						ActivateAccess();
					} else {
						Logger.Debug(LOG_TAG, "ANTISPAM_ACCESS purchase ERROR");
						ShowToast(R.string.ErrorBilling);
					}
				}
			}
		};

		mHelper.queryInventoryAsync(mGotInventoryListener);
	}

	private void ActivateAccess() {
		NotifyAboutPaymentResponse notifyAboutPayment = _jalapenoWebServiceWraper.NotifyAboutPayment(new NotifyAboutPaymentRequest(
				"Already buy"));
		if (notifyAboutPayment.WasSuccessful) {
			_settingsService.ActivateUnlimitedAccess();
			UiUtils.NavigateAndClearHistory(SettingsActivity.class);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null)
			mHelper.dispose();
		mHelper = null;
	}

	private void SetDebugMode(boolean isDebug) {
		if (isDebug) {
			buttonDebug.setVisibility(View.VISIBLE);
		} else {
			buttonDebug.setVisibility(View.INVISIBLE);
		}
	}
}
