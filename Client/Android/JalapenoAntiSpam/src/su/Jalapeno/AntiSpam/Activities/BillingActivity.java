package su.Jalapeno.AntiSpam.Activities;

import static org.solovyev.android.checkout.ProductTypes.IN_APP;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.solovyev.android.checkout.*;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import su.Jalapeno.AntiSpam.MyApplication;
import su.Jalapeno.AntiSpam.Activities.Tasks.PurchaseAntispamTask;
import su.Jalapeno.AntiSpam.Activities.Tasks.TestPurchaseAntispamTask;
import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.Services.AccessService;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import su.Jalapeno.AntiSpam.Util.UI.Spiner;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.inject.Inject;

@ContentView(R.layout.activity_billing)
public class BillingActivity extends JalapenoActivity {
	@Nonnull
	protected final ActivityCheckout checkout = Checkout.forActivity(this, MyApplication.get().getCheckout());

	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "BillingActivity";

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
		spiner.Show();
		checkout.start();
		checkout.createPurchaseFlow(new PurchaseListener());
		Logger.Debug(LOG_TAG, "onCreate createPurchaseFlow");
		inventory = checkout.loadInventory();
		inventory.whenLoaded(new InventoryLoadedListener());
	}

	@Override
	protected void onResume() {
		super.onResume();
		Resume();
	}

	private void Resume() {
		boolean clientIsRegistered = _settingsService.ClientIsRegistered();
		Logger.Debug(LOG_TAG, "onResume ClientRegistered:" + clientIsRegistered);
		if (!clientIsRegistered) {
			_settingsService.HandleClientNotRegistered();
			Logger.Debug(LOG_TAG, "onResume NavigateTo RegisterActivity");
			UiUtils.NavigateTo(RegisterActivity.class);
			return;
		}
		
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

		purchase(_skuAccess);
	}

	private void ActivateAccess(boolean newBuy, String orderId) {
		GetPurchaseAntispamTask(orderId, _clientId).execute();
	}

	private PurchaseAntispamTask GetPurchaseAntispamTask(String orderId, UUID clientId) {
		return new PurchaseAntispamTask(_activity, _accessService, _jalapenoWebServiceWraper, spiner, orderId, clientId);
	}

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
			Logger.Debug(LOG_TAG, "PurchaseListener onSuccess  orderId: " + purchase.orderId + " sku" + purchase.sku);
			onPurchased();
		}

		private void onPurchased() {
			Logger.Debug(LOG_TAG, "PurchaseListener onPurchased");
			// let's update purchase information in local inventory
			inventory.load().whenLoaded(new InventoryLoadedListener());
			ShowToast(R.string.PurchaseComplete);
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
			Logger.Debug(LOG_TAG, "InventoryLoadedListener onLoaded");
			final Inventory.Product product = products.get(IN_APP);
			if (product.isSupported()) {
				_skuAccess = product.getSkus().get(0);
				Logger.Debug(LOG_TAG, "InventoryLoadedListener isSupported " + _skuAccess.title + " cost " + _skuAccess.price);
				final Purchase purchase = product.getPurchaseInState(_skuAccess, Purchase.State.PURCHASED);
				boolean isPurchased = purchase != null && !TextUtils.isEmpty(purchase.token);
				String message = "Ready to buy " + _skuAccess.title + " and status Purch= " + isPurchased;
				Logger.Debug(LOG_TAG, "InventoryLoadedListener " + message);
				if (isPurchased) {
					ActivateAccess(false, purchase.orderId);
				} else {
					spiner.Hide();
				}
			} else {
				Logger.Error(LOG_TAG, "InventoryLoadedListener  support false");
				ShowToast(R.string.ErrorBilling);
			}
		}
	}

	private abstract class BaseRequestListener<Req> implements RequestListener<Req> {

		@Override
		public void onError(int response, @Nonnull Exception ex) {
			Logger.Error(LOG_TAG, "PurchaseListener BaseRequestListener onError", ex);
			ShowToast(R.string.ErrorBilling);
		}
	}
}
