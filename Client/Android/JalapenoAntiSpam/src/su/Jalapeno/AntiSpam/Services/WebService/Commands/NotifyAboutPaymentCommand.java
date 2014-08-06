package su.Jalapeno.AntiSpam.Services.WebService.Commands;

import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsAnalyzerService;
import su.Jalapeno.AntiSpam.Services.WebService.EncoderService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.WebService.WebConstants;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.NotifyAboutPaymentRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.NotifyAboutPaymentResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.WebErrorEnum;

public class NotifyAboutPaymentCommand extends BaseCommand<NotifyAboutPaymentRequest, NotifyAboutPaymentResponse> {

	public NotifyAboutPaymentCommand(JalapenoHttpService httpService, SettingsService settingsService, EncoderService encoderService,
			SmsAnalyzerService smsAnalyzerService,
			Class<NotifyAboutPaymentResponse> respClazz) {
		super(httpService, settingsService, encoderService, smsAnalyzerService,respClazz);
	}

	@Override
	protected String GetAction() {
		return WebConstants.NOTIFY_ABOUT_PAYMENT_URL;
	}

	@Override
	protected NotifyAboutPaymentResponse OnServiceNotAvailable() {

		NotifyAboutPaymentResponse notifyAboutPaymentResponse = new NotifyAboutPaymentResponse();
		notifyAboutPaymentResponse.ErrorMessage = WebErrorEnum.NoConnection;

		return notifyAboutPaymentResponse;
	}
}
