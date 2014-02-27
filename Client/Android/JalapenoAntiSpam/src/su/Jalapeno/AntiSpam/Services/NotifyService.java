package su.Jalapeno.AntiSpam.Services;

import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.UI.DebugMessage;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.google.inject.Inject;

public class NotifyService {
	private SettingsService _settingsService;
	private Context _context;
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "NotifyService";

	@Inject
	public NotifyService(Context context, SettingsService settingsService) {
		_context = context;
		_settingsService = settingsService;
	}

	public void ContactRingtone() {

	}

	public void OnIncomeSms() {
		Log.i(LOG_TAG, "OnIncomeSms.");
		PlayRingtone();

		_context.startService(new Intent(_context, AppService.class));

		DebugMessage.Debug(_context, "Def ringtone");
	}

	private void PlayRingtone() {
		Uri notificationAlarm;
		notificationAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		if (notificationAlarm == null) {
			notificationAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		}
		if (notificationAlarm == null) {
			notificationAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		}

		Ringtone ringtone = RingtoneManager.getRingtone(_context, notificationAlarm);
		if (ringtone != null) {
			ringtone.play();
		}
	}
}
