package su.Jalapeno.AntiSpam.Activities.Tasks;

import java.util.UUID;

import su.Jalapeno.AntiSpam.Activities.SettingsActivity;
import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.Sms.AccessService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.NotifyAboutPaymentRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.NotifyAboutPaymentResponse;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import su.Jalapeno.AntiSpam.Util.UI.Spiner;
import android.os.AsyncTask;

public class PurchaseAntispamTask extends AsyncTask<JalapenoActivity, Void, NotifyAboutPaymentResponse> {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "PurchaseAntispamTask";
	protected JalapenoActivity _activity;
	protected AccessService _accessService;
	protected SettingsService _settingsService;
	protected JalapenoWebServiceWraper _jalapenoWebServiceWraper;
	Spiner _spiner;
	private boolean _newBuy;
	private String _orderId;
	private UUID _clientId;

	public PurchaseAntispamTask(JalapenoActivity activity, AccessService accessService, SettingsService settingsService,
			JalapenoWebServiceWraper jalapenoWebServiceWraper, Spiner spiner, boolean newBuy, String orderId, UUID clientId) {
		_activity = activity;
		_accessService = accessService;
		_settingsService = settingsService;
		_jalapenoWebServiceWraper = jalapenoWebServiceWraper;
		_newBuy = newBuy;
		_spiner = spiner;
		_orderId = orderId;
		_clientId = clientId;
	}

	@Override
	protected void onPostExecute(NotifyAboutPaymentResponse notifyAboutPayment) {

		if (notifyAboutPayment.WasSuccessful) {
			Logger.Debug(LOG_TAG, "Purchase complete");
			_accessService.HandleUnlimitedAccessEnabled();
			_spiner.Hide();
			_activity.UiUtils.NavigateAndClearHistory(SettingsActivity.class);
		} else {
			Logger.Debug(LOG_TAG, "Purchase fail");
			_spiner.Hide();
			_activity.ShowToast(R.string.ErrorBilling);
		}
	}

	@Override
	protected NotifyAboutPaymentResponse doInBackground(JalapenoActivity... activitis) {
		Logger.Debug(LOG_TAG, "doInBackground");
		String message="";
		if(_newBuy)
		{
			message=String.format("ClientId:%s, OrderId:%s", _clientId.toString(),_orderId);
		}
		else
		{
			message=String.format("ClientId:%s Already buy it", _clientId.toString());
		}
		NotifyAboutPaymentResponse notifyAboutPayment = _jalapenoWebServiceWraper.NotifyAboutPayment(new NotifyAboutPaymentRequest(
				message, _settingsService.GetClientId()));

		return notifyAboutPayment;
	}
}
