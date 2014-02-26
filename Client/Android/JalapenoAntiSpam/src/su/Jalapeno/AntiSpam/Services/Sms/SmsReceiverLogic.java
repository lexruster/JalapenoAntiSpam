package su.Jalapeno.AntiSpam.Services.Sms;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import su.Jalapeno.AntiSpam.Util.Constants;

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
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "SmsReceiverLogic";

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
		Log.i(LOG_TAG, "Receive.");

		if (_contactsService.PhoneInContact(sms.SenderId)) {
			Log.i(LOG_TAG, "In contact.");
			_notifyService.ContactRingtone();
			return true;
		}

		Sender sender = _senderService.GetSender(sms.SenderId);
		if (sender != null) {
			if (!sender.IsSpammer) {
				Log.i(LOG_TAG, "Known spamer.");
				return true;
			} else {
				Log.i(LOG_TAG, "Known NOT spamer.");
				return false;
			}
		}

		String smsTexthash = null;
		if (sms.Text.length() > MIN_MESSAGE_LENGTH) {
			smsTexthash = _smsHashService.GetHash(sms.Text);
			boolean isSpam = _smsHashService.HashInSpamBase(smsTexthash);

			if (isSpam) {
				Log.i(LOG_TAG, "Known Hash spamer.");
				return false;
			}
		}

		if (_jalapenoHttpService.IsSpamer(sms.SenderId, smsTexthash)) {
			Log.i(LOG_TAG, "Spamer from http.");
			_senderService.AddOrUpdateSender(sms.SenderId, true);
			if (smsTexthash != null) {
				_smsHashService.AddHash(smsTexthash);
			}

			return false;
		}
		Log.i(LOG_TAG, "Add sms to validate.");
		_smsAnalyzerService.AddSmsToValidate(sms);

		_notifyService.OnIncomeSms();

		// return false;
		return false;// test value
	}
}
