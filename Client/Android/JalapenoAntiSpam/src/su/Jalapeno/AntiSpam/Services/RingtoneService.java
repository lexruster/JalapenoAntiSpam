package su.Jalapeno.AntiSpam.Services;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.google.inject.Inject;

import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Activities.SmsAnalyzerActivity;
import su.Jalapeno.AntiSpam.Util.DebugMessage;

public class RingtoneService {
	private SettingsService _settingsService;
	private Context _context;

	@Inject
	public RingtoneService(Context context, SettingsService settingsService) {
		_context = context;
		_settingsService = settingsService;
	}

	public void ContactRingtone() {

	}

	public void EmulateIncomeSms() {
		PlayRingtone();

		Bitmap bm = BitmapFactory.decodeResource(_context.getResources(), R.drawable.mailb);

		Builder notifBuilder = new Notification.Builder(_context).setContentTitle("New sms").setContentText("sms received")
				.setSmallIcon(R.drawable.mail).setLargeIcon(bm).setContentInfo("3").setAutoCancel(false);

		Intent notificationIntent = new Intent(_context, SmsAnalyzerActivity.class);

		PendingIntent pendingIntent = PendingIntent.getActivity(_context, 131, notificationIntent, 12);
		notifBuilder.setContentIntent(pendingIntent);

		Notification notification = notifBuilder.getNotification();

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;

		NotificationManager notificationManager = (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, notification);

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
