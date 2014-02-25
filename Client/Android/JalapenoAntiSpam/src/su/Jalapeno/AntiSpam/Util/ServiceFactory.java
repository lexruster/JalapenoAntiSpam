package su.Jalapeno.AntiSpam.Util;

import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.Services.ContactsService;
import su.Jalapeno.AntiSpam.Services.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.RequestQueue;
import su.Jalapeno.AntiSpam.Services.NotifyService;
import su.Jalapeno.AntiSpam.Services.SenderService;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsAnalyzerService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsHashService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsQueueService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsReceiverLogic;
import su.Jalapeno.AntiSpam.Services.Sms.SmsService;
import android.content.Context;

public class ServiceFactory {

	public static SmsReceiverLogic GetSmsService(Context context) {
		Repository repository = RepositoryFactory.getRepository();
		SettingsService _settingsService = new SettingsService(context);
		JalapenoHttpService jalapenoHttpService = new JalapenoHttpService(context);
		ContactsService contactsService = new ContactsService(context);
		SmsHashService smsHashService = new SmsHashService(repository, new CryptoService());
		SenderService senderService = new SenderService(repository);
		RequestQueue _requestQueue = new RequestQueue(jalapenoHttpService);
		SmsQueueService smsQueueService = new SmsQueueService(repository);
		SmsService smsService = new SmsService(context);
		SmsAnalyzerService smsAnalyzerService = new SmsAnalyzerService(context, smsQueueService, _requestQueue, smsHashService,
				senderService, smsService);

		SmsReceiverLogic _smsReceiverLogic = new SmsReceiverLogic(context, contactsService, jalapenoHttpService, smsAnalyzerService,
				senderService, _requestQueue, _settingsService, new NotifyService(context, _settingsService), smsHashService);

		return _smsReceiverLogic;
	}
}
