package su.Jalapeno.AntiSpam.Services.Sms;

import java.util.concurrent.ExecutionException;

import su.Jalapeno.AntiSpam.DAL.Domain.Sender;
import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.ContactsService;
import su.Jalapeno.AntiSpam.Services.NotifyService;
import su.Jalapeno.AntiSpam.Services.RequestQueue;
import su.Jalapeno.AntiSpam.Services.SenderService;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Kseny on 30.12.13.
 */
public class SmsReceiverLogic {
	private ContactsService _contactsService;
	private JalapenoWebServiceWraper _jalapenoWebServiceWraper;
	private SmsAnalyzerService _smsAnalyzerService;
	private SenderService _senderService;
	private SettingsService _settingsService;
	private NotifyService _notifyService;
	private SmsHashService _smsHashService;
	private TrashSmsService _trashSmsService;

	private final int MIN_MESSAGE_LENGTH = 50;
	private Context _context;
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "SmsReceiverLogic";

	public SmsReceiverLogic(Context context, ContactsService contactsService, JalapenoWebServiceWraper jalapenoWebServiceWraper,
			SmsAnalyzerService smsAnalyzerService, SenderService senderService, RequestQueue requestQueue, SettingsService settingsService,
			NotifyService notifyService, SmsHashService smsHashService, TrashSmsService trashSmsService) {
		_context = context;
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
		Config config = _settingsService.LoadSettings();
		//_context.startService(new Intent(_context, AppService.class));

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
				_trashSmsService.Add(sms);
				return false;
			}
		}

		String smsTexthash = null;
		if (sms.Text.length() > MIN_MESSAGE_LENGTH) {
			smsTexthash = _smsHashService.GetHash(sms.Text);
			boolean isSpam = _smsHashService.HashInSpamBase(smsTexthash);

			if (isSpam) {
				Log.i(LOG_TAG, "Known Hash spamer.");
				_trashSmsService.Add(sms);
				return false;
			}
		}

		boolean isSpamer = false;
		try {
			isSpamer = new TestIsSpamerAsync().execute(sms.SenderId, smsTexthash).get();
		} catch (InterruptedException e) {

			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		if (isSpamer) {
			Log.i(LOG_TAG, "Spamer from http.");
			_senderService.AddOrUpdateSender(sms.SenderId, true);
			if (smsTexthash != null) {
				_smsHashService.AddHash(smsTexthash);
			}

			_trashSmsService.Add(sms);
			return false;
		}

		Log.i(LOG_TAG, "Add sms to validate.");
		_smsAnalyzerService.AddSmsToValidate(sms);

		_notifyService.OnIncomeSms();

		return false;
	}

	class TestIsSpamerAsync extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = _jalapenoWebServiceWraper.IsSpamer(params[0], params[1]);

			return result;
		}
	}
}
