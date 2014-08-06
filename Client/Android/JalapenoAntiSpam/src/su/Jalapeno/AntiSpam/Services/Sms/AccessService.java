package su.Jalapeno.AntiSpam.Services.Sms;

import java.util.ArrayList;
import java.util.List;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.NotifyService;
import su.Jalapeno.AntiSpam.Services.RequestQueue;
import su.Jalapeno.AntiSpam.Services.SenderService;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import android.content.Context;
import android.content.Intent;

import com.google.inject.Inject;

public class AccessService {

	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "AccessService";

	private SmsQueueService _smsQueueService;
	private SettingsService _settingsService;
	private NotifyService _notifyService;
	private SmsService _smsService;

	@Inject
	public AccessService(Context context, SmsQueueService smsQueueService,
			SettingsService settingsService, NotifyService notifyService,
			SmsService smsService) {
		_smsQueueService = smsQueueService;
		_settingsService = settingsService;
		_notifyService = notifyService;
		_smsService = smsService;
	}

	public void HandleAccessNotAllowed(boolean needSet) {
		if (needSet) {
			_settingsService.DropUnlimitedAccess();
		}
		SaveUncheckedSms();
		_notifyService.OnAccessNotAllowed();
	}

	public void SaveUncheckedSms() {
		List<Sms> smsList = _smsQueueService.GetAll();
		_smsService.SaveSmsToPhoneBase(smsList);
		_smsQueueService.Clear();
	}
}
