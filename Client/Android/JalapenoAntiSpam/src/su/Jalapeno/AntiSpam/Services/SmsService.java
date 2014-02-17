package su.Jalapeno.AntiSpam.Services;

import su.Jalapeno.AntiSpam.Util.Config;

/**
 * Created by Kseny on 30.12.13.
 */
public class SmsService {
	private ContactsService contactsService;
	private JalapenoHttpService jalapenoHttpService;
	private UserValidateService userValidateService;
	private SenderService localSpamBaseService;
	private RequestQueue requestQueue;
	private SettingsService _settingsService;

	public SmsService(ContactsService contactsService, JalapenoHttpService jalapenoHttpService, UserValidateService userValidateService,
			SenderService localSpamBaseService, RequestQueue requestQueue, SettingsService settingsService) {
		this.contactsService = contactsService;
		this.jalapenoHttpService = jalapenoHttpService;
		this.userValidateService = userValidateService;
		this.localSpamBaseService = localSpamBaseService;
		this.requestQueue = requestQueue;
		_settingsService = settingsService;
	}

	public boolean Receive(String phone, String message) {
		Config config = _settingsService.LoadSettings();

		if (!config.Enabled) {
			return true;
		}

		if (localSpamBaseService.PhoneInLocalSpamBase(phone)) {
			return false;
		}

		if (contactsService.PhoneInContact(phone)) {
			return true;
		}

		if (jalapenoHttpService.IsSpamer(phone)) {
			localSpamBaseService.AddOrUpdateSender(phone, true);
			return false;
		}

		boolean validationResult = userValidateService.SmsIsValid(message, phone);
		if (!validationResult) {
			localSpamBaseService.AddOrUpdateSender(phone, true);
			requestQueue.ComplainRequest(phone);

			return false;
		}

		return true;
	}
}
