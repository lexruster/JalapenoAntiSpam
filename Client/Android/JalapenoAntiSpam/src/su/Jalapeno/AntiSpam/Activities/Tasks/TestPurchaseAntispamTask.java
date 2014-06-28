package su.Jalapeno.AntiSpam.Activities.Tasks;

import su.Jalapeno.AntiSpam.Activities.SettingsActivity;
import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.NotifyAboutPaymentRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.NotifyAboutPaymentResponse;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.JalapenoActivity;
import su.Jalapeno.AntiSpam.Util.UI.Spiner;
import android.os.AsyncTask;

public class TestPurchaseAntispamTask extends
		AsyncTask<JalapenoActivity, Void, NotifyAboutPaymentResponse> {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "TestPurchaseAntispamTask";
	protected JalapenoActivity _activity;
	protected SettingsService _settingsService;
	protected JalapenoWebServiceWraper _jalapenoWebServiceWraper;
	Spiner spiner;

	public TestPurchaseAntispamTask(JalapenoActivity activity,
			SettingsService settingsService,
			JalapenoWebServiceWraper jalapenoWebServiceWraper) {
		_activity = activity;
		_settingsService = settingsService;
		_jalapenoWebServiceWraper = jalapenoWebServiceWraper;
		spiner = new Spiner(activity);
	}

	@Override
	protected void onPostExecute(NotifyAboutPaymentResponse notifyAboutPayment) {
		if (notifyAboutPayment.WasSuccessful) {
			_settingsService.ActivateUnlimitedAccess();
			Logger.Debug(LOG_TAG, "Test purchase complete");
			spiner.Hide();
			_activity.UiUtils.NavigateAndClearHistory(SettingsActivity.class);
		} else {
			spiner.Hide();
			_activity.ShowToast(R.string.ErrorBilling);
		}
	}

	@Override
	protected void onPreExecute() {
		spiner.Show();
	}

	// @SuppressWarnings("deprecation")
	@Override
	protected NotifyAboutPaymentResponse doInBackground(
			JalapenoActivity... activitis) {
		Logger.Debug(LOG_TAG, "doInBackground");
		NotifyAboutPaymentResponse notifyAboutPayment = _jalapenoWebServiceWraper
				.NotifyAboutPayment(new NotifyAboutPaymentRequest("Test ClientId: "+_settingsService.GetClientId()));

		return notifyAboutPayment;
	}
}
