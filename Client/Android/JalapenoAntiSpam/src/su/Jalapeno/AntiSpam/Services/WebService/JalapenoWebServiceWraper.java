package su.Jalapeno.AntiSpam.Services.WebService;

import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.Commands.ComplainCommand;
import su.Jalapeno.AntiSpam.Services.WebService.Commands.GetPublicKeyCommand;
import su.Jalapeno.AntiSpam.Services.WebService.Commands.IsSpamerCommand;
import su.Jalapeno.AntiSpam.Services.WebService.Commands.RegisterClientCommand;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.ComplainRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.IsSpammerRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.RegisterClientRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.ComplainResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.IsSpammerResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.PublicKeyResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.RegisterClientResponse;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;

import com.google.inject.Inject;

public class JalapenoWebServiceWraper {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "JalapenoWebServiceWraper";
	private JalapenoHttpService _jalapenoHttpService;
	private SettingsService _settingsService;
	private EncoderService _encoderService;

	@Inject
	public JalapenoWebServiceWraper(JalapenoHttpService jalapenoHttpService, SettingsService settingsService, EncoderService encoderService) {
		_jalapenoHttpService = jalapenoHttpService;
		_settingsService = settingsService;
		_encoderService = encoderService;
	}

	public boolean ServiceIsAvailable() {
		return _jalapenoHttpService.ServiceIsAvailable();
	}

	public RegisterClientResponse RegisterClient(RegisterClientRequest request) {
		Logger.Debug(LOG_TAG, "RegisterClient  " + request.Token);

		RegisterClientCommand command = new RegisterClientCommand(_jalapenoHttpService, _settingsService, _encoderService,
				RegisterClientResponse.class);
		RegisterClientResponse response = command.Execute(request);

		Logger.Debug(LOG_TAG, "RegisterClient response " + response.WasSuccessful);
		return response;
	}

	public boolean IsSpamer(String address, String smsTexthash) {
		Logger.Debug(LOG_TAG, "IsSpamer " + address);
		IsSpammerRequest isSpamerRequest = new IsSpammerRequest();
		isSpamerRequest.Hash = smsTexthash;
		isSpamerRequest.SenderId = address;
		isSpamerRequest.ClientId = _settingsService.GetClientId();

		IsSpamerCommand command = new IsSpamerCommand(_jalapenoHttpService, _settingsService, _encoderService, IsSpammerResponse.class);
		IsSpammerResponse spammerResponse = command.Execute(isSpamerRequest);
		Logger.Debug(LOG_TAG, "IsSpamer response " + spammerResponse.IsSpammer);

		return spammerResponse.IsSpammer && spammerResponse.WasSuccessful;
	}

	public ComplainResponse Complain(String sender, String hash) {
		Logger.Debug(LOG_TAG, "Complain " + sender + " hash " + hash);
		ComplainRequest request = new ComplainRequest();
		request.ClientId = _settingsService.GetClientId();
		request.Hash = hash;
		request.SenderId = sender;
		ComplainCommand command = new ComplainCommand(_jalapenoHttpService, _settingsService, _encoderService, ComplainResponse.class);
		ComplainResponse response = command.Execute(request);

		Logger.Debug(LOG_TAG, "Complain resp " + response.WasSuccessful);
		return response;
	}

	public PublicKeyResponse GetPublicKey() {
		GetPublicKeyCommand command = new GetPublicKeyCommand(_jalapenoHttpService, _settingsService, _encoderService,
				PublicKeyResponse.class);
		PublicKeyResponse response = command.Execute(null);

		return response;
	}
}
