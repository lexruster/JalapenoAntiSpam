package su.Jalapeno.AntiSpam.Services.WebService;

import java.util.UUID;

import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.IsSpammerRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.IsSpammerResponse;
import su.Jalapeno.AntiSpam.Util.Constants;
import android.util.Log;

import com.google.inject.Inject;

public class JalapenoWebServiceWraper {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "JalapenoWebServiceWraper";
	private JalapenoHttpService _jalapenoHttpService;
	private SettingsService _settingsService;
	private UUID _clientId;

	@Inject
	public JalapenoWebServiceWraper(JalapenoHttpService jalapenoHttpService,
			SettingsService settingsService) {
		_jalapenoHttpService = jalapenoHttpService;
		_settingsService = settingsService;
		_clientId = _settingsService.LoadSettings().ClientId;
	}

	public boolean IsSpamer(String address, String smsTexthash) {
		Log.d(LOG_TAG, "IsSpamer " + address );
		if (_jalapenoHttpService.ServiceIsAvailable()) {
			IsSpammerRequest isSpamerRequest = new IsSpammerRequest();
			isSpamerRequest.Hash = smsTexthash;
			isSpamerRequest.SenderId = address;
			isSpamerRequest.ClientId = _clientId;

			IsSpammerResponse spammerResponse = _jalapenoHttpService
					.IsSpamerRequest(isSpamerRequest);

			return spammerResponse.IsSpammer && spammerResponse.WasSuccessful;
		}

		return false;
	}

	public boolean RegisterClient(UUID clientId) {
		if (_jalapenoHttpService.ServiceIsAvailable()) {
		
		}
		return false;
	}
}
