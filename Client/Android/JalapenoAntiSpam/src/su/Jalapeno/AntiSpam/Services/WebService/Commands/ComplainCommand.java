package su.Jalapeno.AntiSpam.Services.WebService.Commands;

import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.Sms.AccessService;
import su.Jalapeno.AntiSpam.Services.WebService.EncoderService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.WebService.WebConstants;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.ComplainRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.ComplainResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.WebErrorEnum;

public class ComplainCommand extends
		BaseCommand<ComplainRequest, ComplainResponse> {

	public ComplainCommand(JalapenoHttpService httpService,
			SettingsService settingsService, EncoderService encoderService,
			AccessService accessService,
			Class<ComplainResponse> respClazz) {
		super(httpService, settingsService, encoderService, accessService,
				respClazz);
	}

	@Override
	protected String GetAction() {
		return WebConstants.COMPLAIN_URL;
	}

	@Override
	protected ComplainResponse OnServiceNotAvailable() {
		ComplainResponse response = new ComplainResponse();
		response.WasSuccessful = false;
		response.ErrorMessage = WebErrorEnum.NoConnection;

		return response;
	}
}
