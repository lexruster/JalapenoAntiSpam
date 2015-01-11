package su.Jalapeno.AntiSpam.Activities.Tasks;

import java.io.IOException;
import java.util.UUID;

import su.Jalapeno.AntiSpam.Activities.RegisterActivity;
import su.Jalapeno.AntiSpam.Activities.SettingsActivity;
import su.Jalapeno.AntiSpam.FilterPro.R;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.NotifyAboutPaymentRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.RegisterClientRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.NotifyAboutPaymentResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.RegisterClientResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.WebErrorEnum;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.Spiner;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

public class RegisterTask extends
		AsyncTask<RegisterActivity, Void, RegisterClientResponse> {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "RegisterTask";
	protected RegisterActivity _activity;

	Spiner spiner;
	private SettingsService _settingsService;
	private JalapenoWebServiceWraper _jalapenoWebServiceWraper;

	public RegisterTask(RegisterActivity activity,
			SettingsService settingsService,
			JalapenoWebServiceWraper serviceWraper) {
		_activity = activity;
		_settingsService = settingsService;
		_jalapenoWebServiceWraper = serviceWraper;
		spiner = new Spiner(activity);
	}

	@Override
	protected void onPostExecute(RegisterClientResponse registerClient) {
		if (registerClient.WasSuccessful) {
			_settingsService.RegisterClient();
			Logger.Debug(LOG_TAG,
					"Register with guid " + _settingsService.GetClientId());
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

		try {
			fetchToken();
			Logger.Debug(LOG_TAG, "fetchNameFromProfileServer token="
					+ _activity.Token);
		} catch (IOException ex) {
			onError("Following Error occured, please try again. "
					+ ex.getMessage(), ex);
		} catch (Exception ex) {
			Logger.Error(LOG_TAG, "doInBackground Exception", ex);
		}
		if (TextUtils.isEmpty(_activity.Token)) {
			return registerClient;
		}
		UUID uuid = UUID.randomUUID();
		_settingsService.PrepareClientForRegister(uuid);

		RegisterClientRequest request = new RegisterClientRequest(uuid);
		request.Token = _activity.Token;

		registerClient = _jalapenoWebServiceWraper.RegisterClient(request);
		
		if (registerClient.WasSuccessful) {
			String message = "";
			String orderId = _activity.PaidOrderId;
			message = String.format("PrePaid CId:%s, PayInf:%s",
					uuid.toString(), orderId);
			NotifyAboutPaymentResponse notifyAboutPayment = _jalapenoWebServiceWraper
					.NotifyAboutPayment(new NotifyAboutPaymentRequest(message,
							uuid));
			registerClient.WasSuccessful = notifyAboutPayment.WasSuccessful;
			registerClient.ErrorMessage = notifyAboutPayment.ErrorMessage;
		}

		return registerClient;
	}

	protected void onError(String msg, Exception e) {
		if (e != null) {
			Logger.Error(LOG_TAG, "Exception: ", e);
		}
		_activity.ShowToast(msg); // will be run in UI thread
	}

	private void fetchToken() throws IOException {
		String token = null;
		try {
			Logger.Debug(LOG_TAG, "fetchToken scope: " + _activity.SCOPE);
			token = GoogleAuthUtil.getToken(_activity, _activity.Email,
					_activity.SCOPE);
		} catch (UserRecoverableAuthException userRecoverableException) {
			// GooglePlayServices.apk is either old, disabled, or not
			// present, which is
			// recoverable, so we need to show the user some UI through the
			// activity.
			_activity.handleException(userRecoverableException);
		} catch (GoogleAuthException fatalException) {
			onError("Unrecoverable error " + fatalException.getMessage(),
					fatalException);
		}

		if (token == null) {
			return;
		}

		_activity.Token = token;
	}
}
