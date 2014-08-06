package su.Jalapeno.AntiSpam.Services.WebService.Commands;

import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.Sms.AccessService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsAnalyzerService;
import su.Jalapeno.AntiSpam.Services.WebService.EncoderService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.WebService.WebConstants;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.RegisterClientResponse;

public class RegisterTestClientCommand extends RegisterClientCommand {

	public RegisterTestClientCommand(JalapenoHttpService httpService,
			SettingsService settingsService, EncoderService encoderService,
			AccessService accessService, Class<RegisterClientResponse> respClazz) {
		super(httpService, settingsService, encoderService, accessService,
				respClazz);
	}

	@Override
	protected String GetAction() {
		return WebConstants.REGISTER_TEST_CLIENT_URL;
	}
}
