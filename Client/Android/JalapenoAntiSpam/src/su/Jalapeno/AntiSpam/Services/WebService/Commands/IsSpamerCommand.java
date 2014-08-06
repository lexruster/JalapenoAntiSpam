package su.Jalapeno.AntiSpam.Services.WebService.Commands;

import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.Sms.AccessService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsAnalyzerService;
import su.Jalapeno.AntiSpam.Services.WebService.EncoderService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.WebService.WebConstants;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.IsSpammerRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.IsSpammerResponse;

public class IsSpamerCommand extends BaseCommand<IsSpammerRequest, IsSpammerResponse> {

	public IsSpamerCommand(JalapenoHttpService httpService, SettingsService settingsService, EncoderService encoderService,
			AccessService accessService,
			Class<IsSpammerResponse> respClazz) {
		super(httpService, settingsService, encoderService, accessService,respClazz);
	}

	@Override
	protected String GetAction() {
		return WebConstants.IS_SPAMMER_URL;
	}

	@Override
	protected IsSpammerResponse OnServiceNotAvailable() {
		IsSpammerResponse isSpammerResponse = new IsSpammerResponse();
		isSpammerResponse.IsSpammer = false;

		return isSpammerResponse;
	}
}
