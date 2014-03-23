package su.Jalapeno.AntiSpam.Services.WebService.Commands;

import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.EncoderService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.WebService.WebConstants;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.BaseRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.PublicKeyResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.WebErrorEnum;

public class GetPublicKeyCommand extends BaseCommand<BaseRequest, PublicKeyResponse> {

	public GetPublicKeyCommand(JalapenoHttpService httpService, SettingsService settingsService, EncoderService encoderService,
			Class<PublicKeyResponse> respClazz) {
		super(httpService, settingsService, encoderService, respClazz);
	}

	@Override
	protected String GetAction() {
		return WebConstants.PUBLIC_KEY_URL;
	}

	@Override
	protected PublicKeyResponse OnServiceNotAvailable() {
		PublicKeyResponse response = new PublicKeyResponse();
		response.WasSuccessful = false;
		response.Error = WebErrorEnum.NoConnection;

		return response;
	}
}
