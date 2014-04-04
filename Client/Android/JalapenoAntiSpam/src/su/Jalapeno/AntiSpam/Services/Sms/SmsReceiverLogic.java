package su.Jalapeno.AntiSpam.Services.Sms;

import su.Jalapeno.AntiSpam.DAL.Domain.Sender;
import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.ContactsService;
import su.Jalapeno.AntiSpam.Services.NotifyService;
import su.Jalapeno.AntiSpam.Services.RequestQueue;
import su.Jalapeno.AntiSpam.Services.SenderService;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import android.content.Context;
import android.os.AsyncTask;

public class SmsReceiverLogic {
	private ContactsService _contactsService;
	private JalapenoWebServiceWraper _jalapenoWebServiceWraper;
	private SmsAnalyzerService _smsAnalyzerService;
	private SenderService _senderService;
	private SettingsService _settingsService;
	private NotifyService _notifyService;
	private SmsHashService _smsHashService;
	private TrashSmsService _trashSmsService;

	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "SmsReceiverLogic";

	public SmsReceiverLogic(Context context, ContactsService contactsService, JalapenoWebServiceWraper jalapenoWebServiceWraper,
			SmsAnalyzerService smsAnalyzerService, SenderService senderService, RequestQueue requestQueue, SettingsService settingsService,
			NotifyService notifyService, SmsHashService smsHashService, TrashSmsService trashSmsService) {
		_contactsService = contactsService;
		_jalapenoWebServiceWraper = jalapenoWebServiceWraper;
		_smsAnalyzerService = smsAnalyzerService;
		_senderService = senderService;
		_settingsService = settingsService;
		_notifyService = notifyService;
		_smsHashService = smsHashService;
		_trashSmsService = trashSmsService;
	}

	public boolean Receive(Sms sms) {
		if (!_settingsService.AntispamEnabled()) {
			return true;
		}
		Logger.Debug(LOG_TAG, "Receive.");

		if (_contactsService.PhoneInContact(sms.SenderId)) {
			Logger.Debug(LOG_TAG, "In contact.");
			_notifyService.ContactRingtone();
			return true;
		}

		Sender sender = _senderService.GetSender(sms.SenderId);
		if (sender != null) {
			if (!sender.IsSpammer) {
				Logger.Debug(LOG_TAG, "Known spamer.");
				return true;
			} else {
				Logger.Debug(LOG_TAG, "Known NOT spamer.");
				_trashSmsService.Add(sms);
				return false;
			}
		}

		String smsTexthash = null;
		if (sms.Text.length() > Constants.MIN_MESSAGE_LENGTH) {
			smsTexthash = _smsHashService.GetHash(sms.Text);
			boolean isSpam = _smsHashService.HashInSpamBase(smsTexthash);

			if (isSpam) {
				Logger.Debug(LOG_TAG, "Known Hash spamer.");
				_trashSmsService.Add(sms);
				return false;
			}
		}

		// next need async
		TestIsSpamerAsync testIsSpamerAsync = new TestIsSpamerAsync();
		testIsSpamerAsync.Sms = sms;
		testIsSpamerAsync.execute(sms.SenderId, smsTexthash);

		return false;
	}

	class TestIsSpamerAsync extends AsyncTask<String, Void, Boolean> {
		public Sms Sms;

		@Override
		protected Boolean doInBackground(String... params) {
			String smsSenderId = params[0];
			String smsTexthash = params[1];
			boolean isSpamer = _jalapenoWebServiceWraper.IsSpamer(smsSenderId, smsTexthash);

			if (isSpamer) {
				Logger.Debug(LOG_TAG, "Spamer from http.");
				_senderService.AddOrUpdateSender(smsSenderId, true);
				if (smsTexthash != null) {
					_smsHashService.AddHash(smsTexthash);
				}

				_trashSmsService.Add(Sms);
				return false;
			}

			Logger.Debug(LOG_TAG, "Add sms to validate.");
			_smsAnalyzerService.AddSmsToValidate(Sms);
			_notifyService.OnIncomeSms();

			return isSpamer;
		}
	}
}
