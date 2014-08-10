package su.Jalapeno.AntiSpam.Services.Sms;

import java.util.List;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.NotifyService;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Util.AccessInfo;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import android.content.Context;

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

	public boolean AccessCheck() {
		AccessInfo accessInfo = _settingsService.GetAccessInfo();
		if (!accessInfo.AccessIsAllowed) {
			Logger.Debug(LOG_TAG, "Init ProceedAccessCheck with FALSE result");
			HandleAccessNotAllowed(false);
			return false;
		}
		return true;
	}

	public void HandleAccessNotAllowed(boolean needSet) {
		if (needSet) {
			_settingsService.DropUnlimitedAccess();
		}
		SaveUncheckedSms();
		_notifyService.OnAccessNotAllowed();
	}
	
	public void HandleUnlimitedAccessEnabled() {
		_settingsService.ActivateUnlimitedAccess();
		_notifyService.OnAccessAllowed();
	}

	public void SaveUncheckedSms() {
		List<Sms> smsList = _smsQueueService.GetAll();
		_smsService.SaveSmsToPhoneBase(smsList);
		_smsQueueService.Clear();
	}
}
