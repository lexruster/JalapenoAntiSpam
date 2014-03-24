package su.Jalapeno.AntiSpam.Services.WebService.Commands;

import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.EncoderService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.WebService.WebConstants;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.RegisterClientRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.RegisterClientResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.WebErrorEnum;

public class RegisterClientCommand extends BaseCommand<RegisterClientRequest, RegisterClientResponse> {

	public RegisterClientCommand(JalapenoHttpService httpService, SettingsService settingsService, EncoderService encoderService,
			Class<RegisterClientResponse> respClazz) {
		super(httpService, settingsService, encoderService, respClazz);
	}

	@Override
	protected String GetAction() {
		return WebConstants.REGISTER_CLIENT_URL;
	}

	@Override
	protected RegisterClientResponse OnServiceNotAvailable() {

		RegisterClientResponse registerClientResponse = new RegisterClientResponse();
		registerClientResponse.ErrorMessage = WebErrorEnum.NoConnection;

		return registerClientResponse;
	}
}
