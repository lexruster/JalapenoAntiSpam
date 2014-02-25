package su.Jalapeno.AntiSpam.Services.Sms;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import su.Jalapeno.AntiSpam.DAL.Domain.Sender;
import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.ContactsService;
import su.Jalapeno.AntiSpam.Services.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.RequestQueue;
import su.Jalapeno.AntiSpam.Services.NotifyService;
import su.Jalapeno.AntiSpam.Services.SenderService;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.Util.Config;

/**
 * Created by Kseny on 30.12.13.
 */
public class SmsReceiverLogic {
	private ContactsService _contactsService;
	private JalapenoHttpService _jalapenoHttpService;
	private SmsAnalyzerService _smsAnalyzerService;
	private SenderService _senderService;
	private SettingsService _settingsService;
	private NotifyService _notifyService;
	private SmsHashService _smsHashService;

	private final int MIN_MESSAGE_LENGTH = 50;
	private Context _context;

	public SmsReceiverLogic(Context context, ContactsService contactsService, JalapenoHttpService jalapenoHttpService,
			SmsAnalyzerService smsAnalyzerService, SenderService senderService, RequestQueue requestQueue, SettingsService settingsService,
			NotifyService notifyService, SmsHashService smsHashService) {
		_context = context;
		_contactsService = contactsService;
		_jalapenoHttpService = jalapenoHttpService;
		_smsAnalyzerService = smsAnalyzerService;
		_senderService = senderService;
		_settingsService = settingsService;
		_notifyService = notifyService;
		_smsHashService = smsHashService;
	}

	public boolean Receive(Sms sms) {
		Config config = _settingsService.LoadSettings();
		_context.startService(new Intent(_context, AppService.class));

		if (!config.Enabled) {
			return true;
		}

		if (_contactsService.PhoneInContact(sms.SenderId)) {
			_notifyService.ContactRingtone();
			return true;
		}

		Sender sender = _senderService.GetSender(sms.SenderId);
		if (sender != null) {
			if (!sender.IsSpammer) {
				return true;
			} else {
				return false;
			}
		}

		String smsTexthash = null;
		if (sms.Text.length() > MIN_MESSAGE_LENGTH) {
			smsTexthash = _smsHashService.GetHash(sms.Text);
			boolean isSpam = _smsHashService.HashInSpamBase(smsTexthash);

			if (isSpam) {
				return false;
			}
		}

		if (_jalapenoHttpService.IsSpamer(sms.SenderId, smsTexthash)) {
			_senderService.AddOrUpdateSender(sms.SenderId, true);
			if (smsTexthash != null) {
				_smsHashService.AddHash(smsTexthash);
			}

			return false;
		}

		_smsAnalyzerService.AddSmsToValidate(sms);

		_notifyService.OnIncomeSms();

		// return false;
		return false;// test value
	}
}
