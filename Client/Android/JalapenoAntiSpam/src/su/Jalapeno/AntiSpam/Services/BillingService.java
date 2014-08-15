package su.Jalapeno.AntiSpam.Services;

import java.util.ArrayList;
import java.util.UUID;

import su.Jalapeno.AntiSpam.Billing.util.IabException;
import su.Jalapeno.AntiSpam.Billing.util.IabHelper;
import su.Jalapeno.AntiSpam.Billing.util.IabResult;
import su.Jalapeno.AntiSpam.Billing.util.Inventory;
import su.Jalapeno.AntiSpam.Billing.util.Purchase;
import su.Jalapeno.AntiSpam.Billing.util.SkuDetails;
import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.Util.BillingConstants;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.CryptoService;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import su.Jalapeno.AntiSpam.Util.UI.Spiner;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.google.inject.Inject;

public class BillingService {
	private Context _context;
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "BillingService";
	IabHelper _mHelper;
	protected boolean hasPremium;
	private UUID _clientId;
	private JalapenoActivity _activity;

	@Inject
	public BillingService(Context context) {
		_context = context;
	}

	public void Init(JalapenoActivity activity,IabHelper mHelper) {
		_activity = activity;
		_mHelper=mHelper;
	}

	public Purchase GetPurchaseSync() {
		Logger.Debug(LOG_TAG, "GetPurchase");
		Inventory inventory = GetInventorySync();
		if (inventory != null) {
			return inventory.getPurchase(BillingConstants.ANTISPAM_ACCESS);
		}
		return null;
	}
	
	public boolean CheckPurchaseSync() {
		Logger.Debug(LOG_TAG, "GetPurchase");
		Inventory inventory = GetInventorySync();
		if (inventory != null) {
			boolean result = inventory.hasPurchase(BillingConstants.ANTISPAM_ACCESS);
			return result;
		}
		return false;
	}

	public Inventory GetInventorySync() {
		Logger.Debug(LOG_TAG, "GetPurchase");
		ArrayList<String> additionalSkuList = new ArrayList<String>();
		additionalSkuList.add(BillingConstants.ANTISPAM_ACCESS);

		try {
			Inventory inventory = _mHelper.queryInventory(true, additionalSkuList);
			return inventory;
		} catch (IabException e) {
			e.printStackTrace();
			Logger.Error(LOG_TAG, "CheckInventory error", e);
			_activity.ShowToast(R.string.ErrorBilling);
			return null;
		}
	}

	public void ConsumePurchaseAsyncNoWait() {
		IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
			public void onConsumeFinished(Purchase purchase, IabResult result) {
				if (result.isSuccess()) {
					_activity.ShowToast("ConsumePurchase success");
				} else {
					_activity.ShowToast("ConsumePurchase fail");
				}
			}
		};
		Purchase purchase = GetPurchaseSync();
		_mHelper.consumeAsync(purchase, mConsumeFinishedListener);
	}
}
