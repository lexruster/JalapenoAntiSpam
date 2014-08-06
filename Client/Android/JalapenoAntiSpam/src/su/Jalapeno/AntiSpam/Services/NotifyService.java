package su.Jalapeno.AntiSpam.Services;

import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.google.inject.Inject;

public class NotifyService {
	private Context _context;
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "NotifyService";

	@Inject
	public NotifyService(Context context) {
		_context = context;
	}

	public void ContactRingtone() {
	}

	public void OnIncomeSms() {
		Logger.Debug(LOG_TAG, "OnIncomeSms.");
		PlayRingtone();
		_context.startService(new Intent(_context, AppService.class).putExtra(
				"Alarm", 1));
	}

	private void PlayRingtone() {
		Uri notificationAlarm;
		notificationAlarm = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		if (notificationAlarm == null) {
			notificationAlarm = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_ALARM);
		}
		if (notificationAlarm == null) {
			notificationAlarm = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		}

		Ringtone ringtone = RingtoneManager.getRingtone(_context,
				notificationAlarm);
		if (ringtone != null) {
			ringtone.play();
		}
	}

	public void OnAccessNotAllowed() {
		Logger.Debug(LOG_TAG, "OnAccessNotAllowed.");
		PlayRingtone();
		_context.startService(new Intent(_context, AppService.class).putExtra(
				"Alarm", 1));
	}
}
