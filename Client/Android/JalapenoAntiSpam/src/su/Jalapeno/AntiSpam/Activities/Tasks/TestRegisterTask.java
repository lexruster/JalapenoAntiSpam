package su.Jalapeno.AntiSpam.Activities.Tasks;

import java.util.UUID;

import su.Jalapeno.AntiSpam.Activities.RegisterActivity;
import su.Jalapeno.AntiSpam.Activities.SettingsActivity;
import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.RegisterClientRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.RegisterClientResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.WebErrorEnum;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.Spiner;
import android.os.AsyncTask;

public class TestRegisterTask extends
		AsyncTask<RegisterActivity, Void, RegisterClientResponse> {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "TestRegisterTask";
	protected RegisterActivity _activity;
	protected SettingsService _settingsService;
	protected JalapenoWebServiceWraper _jalapenoWebServiceWraper;
	Spiner spiner;

	public TestRegisterTask(RegisterActivity activity,
			SettingsService settingsService,
			JalapenoWebServiceWraper jalapenoWebServiceWraper) {
		_activity = activity;
		_settingsService = settingsService;
		_jalapenoWebServiceWraper = jalapenoWebServiceWraper;
		spiner = new Spiner(activity);
	}

	@Override
	protected void onPostExecute(RegisterClientResponse registerClient) {
		if (registerClient.WasSuccessful) {
			_settingsService.RegisterClient(registerClient.ExpirationDate);
			Logger.Debug(LOG_TAG,
					"Test Register with guid " + _settingsService.GetClientId());
			spiner.Hide();
			_activity.UiUtils.NavigateAndClearHistory(SettingsActivity.class);
		} else {
			spiner.Hide();
			if (registerClient.ErrorMessage == WebErrorEnum.UserBanned) {
				_activity.ShowToast(R.string.BannedRegister);
			} else {
				_activity.ShowToast(R.string.ErrorRegister);
			}
		}
	}

	@Override
	protected void onPreExecute() {
		spiner.Show();
	}

	// @SuppressWarnings("deprecation")
	@Override
	protected RegisterClientResponse doInBackground(
			RegisterActivity... activitis) {
		Logger.Debug(LOG_TAG, "doInBackground");
		RegisterClientResponse registerClient = new RegisterClientResponse();
		registerClient.ErrorMessage = WebErrorEnum.NoConnection;
		registerClient.WasSuccessful = false;

		UUID uuid = UUID.randomUUID();
		_settingsService.PrepareClientForRegister(uuid);
		RegisterClientRequest request = new RegisterClientRequest(uuid);
		request.Token = "Test_token";

		registerClient = _jalapenoWebServiceWraper.RegisterTestClient(request);

		return registerClient;
	}

	protected void onError(String msg, Exception e) {
		if (e != null) {
			Logger.Error(LOG_TAG, "Exception: ", e);
		}
		_activity.ShowToast(msg); // will be run in UI thread
	}
}