package su.Jalapeno.AntiSpam.Activities;

import static org.solovyev.android.checkout.ProductTypes.IN_APP;

import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.Inventory.Product;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.RequestListener;
import org.solovyev.android.checkout.ResponseCodes;
import org.solovyev.android.checkout.Sku;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import org.solovyev.android.checkout.*;

import su.Jalapeno.AntiSpam.MyApplication;
import su.Jalapeno.AntiSpam.Activities.Tasks.PurchaseAntispamTask;
import su.Jalapeno.AntiSpam.Activities.Tasks.TestPurchaseAntispamTask;
/*import su.Jalapeno.AntiSpam.Billing.util.IabException;
 import su.Jalapeno.AntiSpam.Billing.util.IabHelper;
 import su.Jalapeno.AntiSpam.Billing.util.IabResult;
 import su.Jalapeno.AntiSpam.Billing.util.Inventory;
 import su.Jalapeno.AntiSpam.Billing.util.SkuDetails;*/
import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.Filter.R.string;
import su.Jalapeno.AntiSpam.Services.AccessService;
import su.Jalapeno.AntiSpam.Services.BillingService;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Util.BillingConstants;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.CryptoService;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import su.Jalapeno.AntiSpam.Util.UI.Spiner;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.inject.Inject;

@ContentView(R.layout.activity_billing)
public class BillingActivity extends JalapenoActivity {

	@Nonnull
	protected final ActivityCheckout checkout = Checkout.forActivity(this, MyApplication.get().getCheckout());

	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "BillingActivity";
	final int PURCHASE_RESULT = 941952;

	@Inject
	Context _context;
	@Inject
	public SettingsService _settingsService;

	private Sku _skuAccess;

	@Inject
	public AccessService _accessService;

	@Inject
	JalapenoWebServiceWraper _jalapenoWebServiceWraper;

	@InjectView(R.id.buttonDebugPurchase)
	Button buttonDebug;

	// BillingService _billingService;

	// IabHelper mHelper;
	protected boolean hasPremium;

	@Nonnull
	protected Inventory inventory;

	Spiner spiner;
	private JalapenoActivity _activity;
	private UUID _clientId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.Debug(LOG_TAG, "onCreate");

		spiner = new Spiner(this);
		_activity = this;
		// _billingService = new BillingService(_context);

		spiner.Show();
		checkout.start();
		checkout.createPurchaseFlow(new PurchaseListener());
		Logger.Debug(LOG_TAG, "onCreate createPurchaseFlow");

		inventory = checkout.loadInventory();
		inventory.whenLoaded(new InventoryLoadedListener());
		ShowToast("Inv load");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Logger.Debug(LOG_TAG, "onResume");
		Resume();
	}

	private void Resume() {
		_clientId = _settingsService.GetClientId();
		SetDebugMode(Constants.VIEW_DEBUG_UI);
	}

	@Override
	public void onBackPressed() {
		Logger.Debug(LOG_TAG, "onBackPressed");
		UiUtils.NavigateAndClearHistory(SettingsActivity.class);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		checkout.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PURCHASE_RESULT) {
			Logger.Debug(LOG_TAG, "onActivityResult PURCHASE_RESULT");
			if (resultCode == RESULT_OK) {
				Logger.Debug(LOG_TAG, "onActivityResult RESULT_OK");
				ShowToast(R.string.PurchaseComplete);
				// CheckComplete("Unknown");
				ShowToast(R.string.PurchaseComplete);
				// ActivateAccess(true, "3423");
			}
		}
	}

	@Override
	protected void onDestroy() {
		checkout.stop();
		checkout.destroyPurchaseFlow();
		super.onDestroy();
	}

	@Nonnull
	public ActivityCheckout getCheckout() {
		return checkout;
	}

	public void BuyTest(View view) {
		Logger.Debug(LOG_TAG, "BuyTest");
		if (Constants.VIEW_DEBUG_UI) {
			new TestPurchaseAntispamTask(this, _accessService, _settingsService, _jalapenoWebServiceWraper).execute();
		}
	}

	public void Buy(View view) {
		Logger.Debug(LOG_TAG, "Buy pressed");
		spiner.Show();
		// ShowToast(R.string.ErrorBilling);
		// Logger.Debug(LOG_TAG, "Billing initialized");
		// CheckAccess();

		purchase(_skuAccess);
	}

	protected void CheckAccess() {
		Logger.Debug(LOG_TAG, "CheckAccess");
		// ArrayList<String> additionalSkuList = new ArrayList<String>();
		// additionalSkuList.add(BillingConstants.ANTISPAM_ACCESS);
		/*
		 * try { Inventory inventory = mHelper.queryInventory(true,
		 * additionalSkuList);
		 * 
		 * BuyAccess(inventory);
		 * 
		 * } catch (IabException e) { spiner.Hide(); e.printStackTrace();
		 * Logger.Error(LOG_TAG, "ProceedWithPurchase error", e);
		 * ShowToast(R.string.ErrorBilling); }
		 */
	}

	private void BuyAccess(Inventory inventory) {
		Logger.Debug(LOG_TAG, "BuyAccess");
		// SkuDetails skuDetails =
		// inventory.getSkuDetails(BillingConstants.ANTISPAM_ACCESS);

		// if (inventory.hasPurchase(BillingConstants.ANTISPAM_ACCESS)) {
		// Logger.Debug(LOG_TAG, "ANTISPAM_ACCESS already purchase");
		// ShowToast(R.string.PurchaseComplete);
		//
		// ActivateAccess(false, "");
		// } else {
		// String accessPrice = skuDetails.getPrice();
		// Logger.Debug(LOG_TAG, "Available access with cost " + accessPrice);
		// IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener =
		// new IabHelper.OnIabPurchaseFinishedListener() {
		// public void onIabPurchaseFinished(IabResult result,
		// su.Jalapeno.AntiSpam.Billing.util.Purchase purchase) {
		// Logger.Debug(LOG_TAG, "onIabPurchaseFinished");
		// if (result.isFailure()) {
		// spiner.Hide();
		// Logger.Debug(LOG_TAG, "Error purchasing: " + result);
		// ShowToast(R.string.ErrorBilling);
		// return;
		// } else if
		// (purchase.getSku().equals(BillingConstants.ANTISPAM_ACCESS)) {
		// Logger.Debug(LOG_TAG, "onIabPurchaseFinished Successfully result");
		// String orderId = purchase.getOrderId();
		// CheckComplete(orderId);
		// }
		// }
		// };
		//
		// mHelper.launchPurchaseFlow(this, BillingConstants.ANTISPAM_ACCESS,
		// PURCHASE_RESULT, mPurchaseFinishedListener,
		// _clientId.toString());
		// }

	}

	protected void CheckComplete(final String orderId) {
		Logger.Debug(LOG_TAG, "CheckComplete");

		// IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new
		// IabHelper.QueryInventoryFinishedListener() {
		// public void onQueryInventoryFinished(IabResult result, Inventory
		// inventory) {
		//
		// if (result.isFailure()) {
		// spiner.Hide();
		// } else {
		// hasPremium = inventory.hasPurchase(BillingConstants.ANTISPAM_ACCESS);
		//
		// Logger.Debug(LOG_TAG, "ANTISPAM_ACCESS  purchase status " +
		// hasPremium);
		// if (hasPremium) { // spiner.Hide();
		// ShowToast(R.string.PurchaseComplete);
		// ActivateAccess(true, orderId);
		// } else {
		// Logger.Debug(LOG_TAG, "ANTISPAM_ACCESS purchase ERROR");
		// spiner.Hide();
		// ShowToast(R.string.ErrorBilling);
		// }
		// }
		// }
		// };
		//
		// mHelper.queryInventoryAsync(mGotInventoryListener);

	}

	private void ActivateAccess(boolean newBuy, String orderId) {
		GetPurchaseAntispamTask(newBuy, orderId, _clientId).execute();
	}

	private PurchaseAntispamTask GetPurchaseAntispamTask(boolean newBuy, String orderId, UUID clientId) {
		return new PurchaseAntispamTask(_activity, _accessService, _jalapenoWebServiceWraper, spiner, newBuy, orderId, clientId);
	}

	/*
	 * @Override public void onDestroy() { super.onDestroy(); if (mHelper !=
	 * null) mHelper.dispose(); mHelper = null; }
	 */
	private void SetDebugMode(boolean isDebug) {
		if (isDebug) {
			buttonDebug.setVisibility(View.VISIBLE);
		} else {
			buttonDebug.setVisibility(View.INVISIBLE);
		}
	}

	private void purchase(@Nonnull final Sku sku) {
		Logger.Debug(LOG_TAG, "purchase started");
		checkout.whenReady(new Checkout.ListenerAdapter() {
			@Override
			public void onReady(@Nonnull BillingRequests requests) {
				Logger.Debug(LOG_TAG, "purchase onReady start purchase");
				requests.purchase(sku, null, checkout.getPurchaseFlow());
			}
		});
	}

	private class PurchaseListener extends BaseRequestListener<Purchase> {
		@Override
		public void onSuccess(@Nonnull Purchase purchase) {
			ShowToast("Ura! buy it! order " + purchase.orderId + " " + purchase.sku);
			Logger.Debug(LOG_TAG, "PurchaseListener onSuccess  orderId" + purchase.orderId + " sku" + purchase.sku);
			onPurchased();

		}

		private void onPurchased() {
			Logger.Debug(LOG_TAG, "PurchaseListener onPurchased");
			// let's update purchase information in local inventory
			inventory.load().whenLoaded(new InventoryLoadedListener());
			// Toast.makeText(getActivity(),
			// R.string.msg_thank_you_for_purchase, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(int response, @Nonnull Exception ex) {
			// it is possible that our data is not synchronized with data on
			// Google Play => need to handle some errors
			if (response == ResponseCodes.ITEM_ALREADY_OWNED) {
				Logger.Debug(LOG_TAG, "PurchaseListener onError ITEM_ALREADY_OWNED");
				onPurchased();
			} else {
				super.onError(response, ex);
			}
		}
	}

	private class InventoryLoadedListener implements Inventory.Listener {
		@Override
		public void onLoaded(@Nonnull Inventory.Products products) {
			ShowToast("Inv load");
			Logger.Debug(LOG_TAG, "InventoryLoadedListener onLoaded");
			final Inventory.Product product = products.get(IN_APP);
			if (product.isSupported()) {
				_skuAccess = product.getSkus().get(0);
				Logger.Debug(LOG_TAG, "InventoryLoadedListener isSupported " + _skuAccess.title + " cost " + _skuAccess.price);
				final Purchase purchase = product.getPurchaseInState(_skuAccess, Purchase.State.PURCHASED);
				boolean isPurchased = purchase != null && !TextUtils.isEmpty(purchase.token);
				String message = "Ready to buy " + _skuAccess.title + " and status Purch= " + isPurchased;
				Logger.Debug(LOG_TAG, "InventoryLoadedListener " + message);
				ShowToast(message);
				if (isPurchased) {
					ActivateAccess(false, purchase.orderId);
				} else {
					spiner.Hide();
				}
			} else {
				Logger.Error(LOG_TAG, "InventoryLoadedListener  support false");
				ShowToast(R.string.ErrorBilling);
				ShowToast("product not Supported");
			}
		}
	}

	private abstract class BaseRequestListener<Req> implements RequestListener<Req> {

		@Override
		public void onError(int response, @Nonnull Exception ex) {
			// todo serso: add alert dialog or console
			Logger.Error(LOG_TAG, "PurchaseListener BaseRequestListener onError", ex);
			ShowToast(R.string.ErrorBilling);
		}
	}
}
