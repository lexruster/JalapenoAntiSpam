package su.Jalapeno.AntiSpam.Services.Sms;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import android.content.Context;

public class SmsReceiver {

	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "SmsReceiver";
	private SettingsService _settingsService;
	private SmsReceiverLogic _smsReceiverLogic;

	public SmsReceiver(SettingsService settingsService,
			SmsReceiverLogic smsReceiverLogic) {
		_settingsService = settingsService;
		_smsReceiverLogic = smsReceiverLogic;
	}

	public boolean Receive(Sms sms, Context context) {
		Config config = _settingsService.LoadSettings();
		if (!_settingsService.AntispamEnabled()) {
			return true;
		}
		Boolean isClearFromSpam;
		isClearFromSpam = _smsReceiverLogic.Receive(sms);

		String messageOut = String.format("%s\n%s\nSpam: %s", sms.SenderId,
				sms.Text, !isClearFromSpam);
		Logger.Debug(LOG_TAG, messageOut);
		return isClearFromSpam;
	}
}
