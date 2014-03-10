package su.Jalapeno.AntiSpam.Services.WebService;

import java.util.UUID;

import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.BaseResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.ComplainRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.ComplainResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.IsSpammerRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.IsSpammerResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.PublicKeyResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.RegisterClientRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.RegisterClientResponse;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import android.util.Log;

import com.google.inject.Inject;

public class JalapenoWebServiceWraper {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "JalapenoWebServiceWraper";
	private JalapenoHttpService _jalapenoHttpService;
	private SettingsService _settingsService;
	private UUID _clientId;

	@Inject
	public JalapenoWebServiceWraper(JalapenoHttpService jalapenoHttpService, SettingsService settingsService) {
		_jalapenoHttpService = jalapenoHttpService;
		_settingsService = settingsService;
		_clientId = _settingsService.LoadSettings().ClientId;
	}

	public boolean  ServiceIsAvailable()
	{
		return _jalapenoHttpService.ServiceIsAvailable();
	}
	
	public boolean IsSpamer(String address, String smsTexthash) {
		Log.d(LOG_TAG, "IsSpamer " + address);
		if (_jalapenoHttpService.ServiceIsAvailable()) {
			IsSpammerRequest isSpamerRequest = new IsSpammerRequest();
			isSpamerRequest.Hash = smsTexthash;
			isSpamerRequest.SenderId = address;
			isSpamerRequest.ClientId = _clientId;

			IsSpammerResponse spammerResponse = _jalapenoHttpService.IsSpamerRequest(isSpamerRequest);
			if (ValidateAndNeedResend(spammerResponse)) {
				spammerResponse = _jalapenoHttpService.IsSpamerRequest(isSpamerRequest);
			}

			Log.d(LOG_TAG, "IsSpamer response " + spammerResponse.IsSpammer);

			return spammerResponse.IsSpammer && spammerResponse.WasSuccessful;
		}

		return false;
	}

	public RegisterClientResponse RegisterClient(RegisterClientRequest request) {
		Log.d(LOG_TAG, "RegisterClient  " + request.Token);
		RegisterClientResponse response = new RegisterClientResponse();
		response.WasSuccessful = false;
		if (_jalapenoHttpService.ServiceIsAvailable()) {
			response = _jalapenoHttpService.RegisterClient(request);
			if (ValidateAndNeedResend(response)) {
				response = _jalapenoHttpService.RegisterClient(request);
			}
		}

		Log.d(LOG_TAG, "RegisterClient response " + response.WasSuccessful);
		return response;
	}

	public ComplainResponse Complain(String sender, String hash) {
		Log.d(LOG_TAG, "Complain " + sender + " hash " + hash);
		ComplainRequest request = new ComplainRequest();
		request.ClientId = _clientId;
		request.Hash = hash;
		request.SenderId = sender;
		ComplainResponse response = new ComplainResponse();
		if (_jalapenoHttpService.ServiceIsAvailable()) {
			response = _jalapenoHttpService.Complain(request);
			if (ValidateAndNeedResend(response)) {
				response = _jalapenoHttpService.Complain(request);
			}
		}

		Log.d(LOG_TAG, "Complain resp" + response.WasSuccessful);
		return response;
	}

	public PublicKeyResponse GetPublicKey() {
		PublicKeyResponse response = null;
		if (_jalapenoHttpService.ServiceIsAvailable()) {
			response = _jalapenoHttpService.GetPublicKey();
		}

		return response;
	}

	public boolean ValidateAndNeedResend(BaseResponse response) {
		if (response.WasSuccessful) {
			return false;
		}

		if (response.ErrorMessage.equals(WebErrors.InvalidRequest)) {
			PublicKeyResponse pbk = GetPublicKey();
			if (pbk.WasSuccessful) {
				Config config = _settingsService.LoadSettings();
				String key = pbk.PublicKey;
				config.PublicKey = key;
				_settingsService.SaveSettings(config);

				return true;
			}

			return false;
		}

		if (response.ErrorMessage.equals(WebErrors.InvalidToken) || response.ErrorMessage.equals(WebErrors.UserBanned)
				|| response.ErrorMessage.equals(WebErrors.NotAuthorizedRequest)) {

			Config config = _settingsService.LoadSettings();
			config.ClientRegistered = false;
			_settingsService.SaveSettings(config);

			return false;
		}

		return false;
	}
}
