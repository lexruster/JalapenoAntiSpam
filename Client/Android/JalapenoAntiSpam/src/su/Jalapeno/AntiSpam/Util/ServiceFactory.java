package su.Jalapeno.AntiSpam.Util;

import su.Jalapeno.AntiSpam.DAL.Repository;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.Services.ConfigService;
import su.Jalapeno.AntiSpam.Services.ContactsService;
import su.Jalapeno.AntiSpam.Services.RequestQueue;
import su.Jalapeno.AntiSpam.Services.NotifyService;
import su.Jalapeno.AntiSpam.Services.SenderService;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsAnalyzerService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsHashService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsQueueService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsReceiver;
import su.Jalapeno.AntiSpam.Services.Sms.SmsReceiverLogic;
import su.Jalapeno.AntiSpam.Services.Sms.SmsService;
import su.Jalapeno.AntiSpam.Services.Sms.TrashSmsService;
import su.Jalapeno.AntiSpam.Services.WebService.EncoderService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import android.content.Context;

public class ServiceFactory {

	public static SmsReceiverLogic GetSmsReceiverLogic(Context context) {
		Repository repository = RepositoryFactory.getRepository();
		SettingsService _settingsService = GetSettingsService(context);
		EncoderService encodeService = new EncoderService();
		
		
		ContactsService contactsService = new ContactsService(context);
		SmsHashService smsHashService = new SmsHashService(repository);
		SenderService senderService = new SenderService(repository);
		

		TrashSmsService _trashSmsService = new TrashSmsService(context,
				repository);
		SmsService smsService = new SmsService(context);
		SmsQueueService smsQueueService = new SmsQueueService(repository);
		NotifyService notifyService = new NotifyService(context);
		
		JalapenoHttpService jalapenoHttpService=new JalapenoHttpService(context, encodeService);
		JalapenoWebServiceWraper jalapenoWebServiceWraper = new JalapenoWebServiceWraper(
				jalapenoHttpService, _settingsService, encodeService,
				smsAnalyzerService);
		
		RequestQueue _requestQueue=new RequestQueue(repository, jalapenoWebServiceWraper, _settingsService);
		
		SmsAnalyzerService smsAnalyzerService = new SmsAnalyzerService(context,
				smsQueueService, _requestQueue, smsHashService, senderService,
				smsService, _settingsService, notifyService);
		
		SmsReceiverLogic _smsReceiverLogic = new SmsReceiverLogic(context,
				contactsService, jalapenoWebServiceWraper, smsAnalyzerService,
				senderService, _requestQueue, _settingsService, notifyService,
				smsHashService, _trashSmsService);

		return _smsReceiverLogic;
	}

	public static RequestQueue GetRequestQueue(Context context) {
		Repository repository = RepositoryFactory.getRepository();
		SettingsService _settingsService = GetSettingsService(context);
		EncoderService encodeService = new EncoderService();
		JalapenoHttpService jalapenoHttpService = new JalapenoHttpService(
				context, encodeService);
		JalapenoWebServiceWraper jalapenoWebServiceWraper = new JalapenoWebServiceWraper(
				jalapenoHttpService, _settingsService, encodeService);
		RequestQueue requestQueue = new RequestQueue(repository,
				jalapenoWebServiceWraper, _settingsService);

		return requestQueue;
	}

	public static SettingsService GetSettingsService(Context context) {
		ConfigService configService = new ConfigService(context);
		SettingsService settingsService = new SettingsService(configService);

		return settingsService;
	}

	public static SmsReceiver GetSmsReceiver(Context context) {
		SmsReceiver smsReceiver = new SmsReceiver(GetSettingsService(context),
				GetSmsReceiverLogic(context));
		return smsReceiver;
	}
}
