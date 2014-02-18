package su.Jalapeno.AntiSpam.Services;

import su.Jalapeno.AntiSpam.DAL.Domain.Sender;
import su.Jalapeno.AntiSpam.Util.Config;

/**
 * Created by Kseny on 30.12.13.
 */
public class SmsReceiverLogic {
	private ContactsService _contactsService;
	private JalapenoHttpService _jalapenoHttpService;
	private UserValidateService _userValidateService;
	private SenderService _senderService;
	private RequestQueue _requestQueue;
	private SettingsService _settingsService;
	private RingtoneService _ringtoneService;
	private SmsHashService _smsHashService;

	private final int MIN_MESSAGE_LENGTH = 50;

	public SmsReceiverLogic(ContactsService contactsService,
			JalapenoHttpService jalapenoHttpService,
			UserValidateService userValidateService,
			SenderService localSpamBaseService, RequestQueue requestQueue,
			SettingsService settingsService, RingtoneService ringtoneService,
			SmsHashService smsHashService) {
		_contactsService = contactsService;
		_jalapenoHttpService = jalapenoHttpService;
		_userValidateService = userValidateService;
		_senderService = localSpamBaseService;
		_requestQueue = requestQueue;
		_settingsService = settingsService;
		_ringtoneService = ringtoneService;
		_smsHashService = smsHashService;
	}

	public boolean Receive(String phone, String message) {
		Config config = _settingsService.LoadSettings();

		if (!config.Enabled) {
			return true;
		}

		if (_contactsService.PhoneInContact(phone)) {
			_ringtoneService.ContactRingtone();
			return true;
		}

		Sender sender = _senderService.GetSender(phone);
		if (sender != null) {
			if (!sender.IsSpammer) {
				return true;
			} else {
				return false;
			}
		}

		String smsTexthash = null;
		if (message.length() > MIN_MESSAGE_LENGTH) {
			smsTexthash = _smsHashService.GetHash(message);
			boolean isSpam = _smsHashService.HashInSpamBase(smsTexthash);

			if (isSpam) {
				return false;
			}
		}

		if (_jalapenoHttpService.IsSpamer(phone, smsTexthash)) {
			_senderService.AddOrUpdateSender(phone, true);
			if (smsTexthash != null) {
				_smsHashService.AddHash(smsTexthash);
			}
			
			return false;
		}
		
		_ringtoneService.EmulateIncomeSms();
		
		_userValidateService.AddSmsToValidate(phone, message);
		
		//return false;
		return true;//test value
	}
}
