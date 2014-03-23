package su.Jalapeno.AntiSpam.Util;

import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.Services.ContactsService;
import su.Jalapeno.AntiSpam.Services.RequestQueue;
import su.Jalapeno.AntiSpam.Services.NotifyService;
import su.Jalapeno.AntiSpam.Services.SenderService;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsAnalyzerService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsHashService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsQueueService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsReceiverLogic;
import su.Jalapeno.AntiSpam.Services.Sms.SmsService;
import su.Jalapeno.AntiSpam.Services.Sms.TrashSmsService;
import su.Jalapeno.AntiSpam.Services.WebService.EncoderService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import android.content.Context;

public class ServiceFactory {

	public static SmsReceiverLogic GetSmsService(Context context) {
		Repository repository = RepositoryFactory.getRepository();
		SettingsService _settingsService = new SettingsService(context);
		EncoderService encodeService = new EncoderService(_settingsService);
		JalapenoHttpService jalapenoHttpService = new JalapenoHttpService(context, encodeService);
		JalapenoWebServiceWraper jalapenoWebServiceWraper = new JalapenoWebServiceWraper(jalapenoHttpService, _settingsService,
				encodeService);
		ContactsService contactsService = new ContactsService(context);
		SmsHashService smsHashService = new SmsHashService(repository);
		SenderService senderService = new SenderService(repository);
		RequestQueue _requestQueue = new RequestQueue(repository, jalapenoWebServiceWraper, _settingsService);
		SmsQueueService smsQueueService = new SmsQueueService(repository);
		TrashSmsService _trashSmsService = new TrashSmsService(context, repository);
		SmsService smsService = new SmsService(context);
		NotifyService notifyService = new NotifyService(context, _settingsService);

		SmsAnalyzerService smsAnalyzerService = new SmsAnalyzerService(context, smsQueueService, _requestQueue, smsHashService,
				senderService, smsService);

		SmsReceiverLogic _smsReceiverLogic = new SmsReceiverLogic(context, contactsService, jalapenoWebServiceWraper, smsAnalyzerService,
				senderService, _requestQueue, _settingsService, notifyService, smsHashService, _trashSmsService);

		return _smsReceiverLogic;
	}

	public static RequestQueue GetRequestQueue(Context context) {
		Repository repository = RepositoryFactory.getRepository();
		SettingsService _settingsService = new SettingsService(context);
		EncoderService encodeService = new EncoderService(_settingsService);
		JalapenoHttpService jalapenoHttpService = new JalapenoHttpService(context, encodeService);
		JalapenoWebServiceWraper jalapenoWebServiceWraper = new JalapenoWebServiceWraper(jalapenoHttpService, _settingsService,
				encodeService);
		RequestQueue requestQueue = new RequestQueue(repository, jalapenoWebServiceWraper, _settingsService);

		return requestQueue;
	}
}
