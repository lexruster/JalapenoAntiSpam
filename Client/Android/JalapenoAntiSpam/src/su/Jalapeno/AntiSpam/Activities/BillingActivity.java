package su.Jalapeno.AntiSpam.Activities;

import static org.solovyev.android.checkout.ProductTypes.IN_APP;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.solovyev.android.checkout.*;
import org.solovyev.android.checkout.Inventory.Product;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import su.Jalapeno.AntiSpam.MyApplication;
import su.Jalapeno.AntiSpam.Activities.Tasks.PurchaseAntispamTask;
import su.Jalapeno.AntiSpam.Activities.Tasks.TestPurchaseAntispamTask;
import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.Services.AccessService;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Util.BillingConstants;
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
import android.widget.TextView;

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

	private Sku _skuAccessToBuy;

	@Inject
	public AccessService _accessService;

	@Inject
	JalapenoWebServiceWraper _jalapenoWebServiceWraper;

	@InjectView(R.id.buttonDebugPurchase)
	Button buttonDebug;

	@InjectView(R.id.textPriceInfo)
	TextView textPriceInfo;

	@Nonnull
	protected Inventory inventory;

	Spiner spiner;
	private JalapenoActivity _activity;
	private UUID _clientId;
	private boolean _accessIsAllowed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.Debug(LOG_TAG, "onCreate");

		_skuAccessToBuy = null;
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
		_accessIsAllowed = _accessService.AccessCheck();
		SetDebugMode(Constants.VIEW_DEBUG_UI);
	}

	@Override
	public void onBackPressed() {
		Logger.Debug(LOG_TAG, "onBackPressed");
		if (_accessIsAllowed) {
			UiUtils.NavigateTo(SettingsActivity.class);
		} else {
			UiUtils.NavigateToExit();
		}
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

		purchase(_skuAccessToBuy);
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
		boolean billingSupported = checkout.isBillingSupported(IN_APP);
		Logger.Debug(LOG_TAG, "purchase started billingSupported = " + billingSupported);
		if (!billingSupported) {
			Logger.Error(LOG_TAG, "purchase billingSupported FALSE");
			spiner.Hide();
			ShowToast(R.string.ErrorBilling);
			return;
		}

		checkout.whenReady(new Checkout.ListenerAdapter() {
			@Override
			public void onReady(@Nonnull BillingRequests requests) {
				Logger.Debug(LOG_TAG, "purchase onReady start purchase");
				requests.purchase(sku, null, checkout.getPurchaseFlow());
			}
		});
	}

	private void SetPriceInfo(Sku sku) {
		if (sku != null) {
			String message = "Ready to buy " + _skuAccessToBuy.title + " price " + _skuAccessToBuy.price;
			Logger.Debug(LOG_TAG, "InventoryLoadedListener " + message);
			textPriceInfo.setText(String.format(Locale.getDefault(), "%s: %s", sku.description, sku.price));
			textPriceInfo.setVisibility(View.VISIBLE);
		} else {
			Logger.Error(LOG_TAG, "SetPriceInfo failed find sku");
			ShowToast(R.string.ErrorBilling);
		}
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
		private String _purchaseOrderId;

		@Override
		public void onLoaded(@Nonnull Inventory.Products products) {
			Logger.Debug(LOG_TAG, "InventoryLoadedListener onLoaded");

			final Inventory.Product product = products.get(IN_APP);
			if (product.isSupported()) {
				Logger.Debug(LOG_TAG, "InventoryLoadedListener isSupported");
				boolean isPurchased = InspectPurchases(product);

				if (isPurchased) {
					ActivateAccess(false, _purchaseOrderId);
				} else {
					spiner.Hide();
				}
			} else {
				Logger.Error(LOG_TAG, "InventoryLoadedListener support false");
				spiner.Hide();
				ShowToast(R.string.ErrorBilling);
			}
		}

		private boolean InspectPurchases(final Inventory.Product product) {
			List<Sku> skus = product.getSkus();
			Logger.Debug(LOG_TAG, "InspectPurchases skus count " + skus.size());
			boolean isPurchased = CheckIsPurchased(skus, product);
			Logger.Debug(LOG_TAG, "InspectPurchases isPurchased=" + isPurchased);
			if (isPurchased)
				return true;

			_skuAccessToBuy = FindSkuForBuy(skus);
			SetPriceInfo(_skuAccessToBuy);

			return isPurchased;
		}

		private boolean CheckIsPurchased(List<Sku> skus, Product product) {
			for (Sku sku : skus) {
				final Purchase purchase = product.getPurchaseInState(sku, Purchase.State.PURCHASED);
				boolean isPurchased = purchase != null && !TextUtils.isEmpty(purchase.token);
				if (isPurchased) {
					_purchaseOrderId = purchase.orderId;
					return true;
				}
			}

			return false;
		}

		private Sku FindSkuForBuy(List<Sku> skus) {
			if (skus.size() == 1) {
				Sku sku = skus.get(0);
				Logger.Debug(LOG_TAG, "FindSkuForBuy single skuId=" + sku.id);
				return sku;
			}

			for (Sku sku : skus) {
				Logger.Debug(LOG_TAG, "FindSkuForBuy skuId=" + sku.id + " product=" + sku.product);
				if (_accessIsAllowed && sku.id == BillingConstants.EARLY_ANTISPAM_ACCESS) {
					Logger.Debug(LOG_TAG, "TrySetSkuForBuy set accessIsAllowed sku=" + sku.id);
					return sku;
				}
				if (!_accessIsAllowed && sku.id == BillingConstants.ANTISPAM_ACCESS) {
					Logger.Debug(LOG_TAG, "FindSkuForBuy set not accessIsAllowed sku=" + sku.id);
					return sku;
				}
			}

			return null;
		}
	}

	private abstract class BaseRequestListener<Req> implements RequestListener<Req> {

		@Override
		public void onError(int response, @Nonnull Exception ex) {
			Logger.Error(LOG_TAG, "PurchaseListener BaseRequestListener onError", ex);
			spiner.Hide();
			ShowToast(R.string.ErrorBilling);
		}
	}
}
