package su.Jalapeno.AntiSpam.Util;

import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.Services.ContactsService;
import su.Jalapeno.AntiSpam.Services.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.RequestQueue;
import su.Jalapeno.AntiSpam.Services.RingtoneService;
import su.Jalapeno.AntiSpam.Services.SenderService;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.SmsHashService;
import su.Jalapeno.AntiSpam.Services.SmsReceiverLogic;
import su.Jalapeno.AntiSpam.Services.UserValidateService;
import android.content.Context;

public class ServiceFactory {

	public static SmsReceiverLogic GetSmsService(Context context) {
		SettingsService _settingsService = new SettingsService(context);
		JalapenoHttpService jalapenoHttpService = new JalapenoHttpService(context);
		ContactsService contactsService = new ContactsService(context);
		SmsHashService smsHashService = new SmsHashService(RepositoryFactory.getRepository(), new CryptoService());
		SenderService senderService = new SenderService(RepositoryFactory.getRepository());

		SmsReceiverLogic _smsReceiverLogic = new SmsReceiverLogic(contactsService, jalapenoHttpService, new UserValidateService(), senderService,
				new RequestQueue(jalapenoHttpService), _settingsService, new RingtoneService(_settingsService), smsHashService);

		return _smsReceiverLogic;
	}
}
