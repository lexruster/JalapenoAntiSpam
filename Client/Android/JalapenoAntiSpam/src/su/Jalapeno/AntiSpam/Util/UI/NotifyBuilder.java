package su.Jalapeno.AntiSpam.Util.UI;

import su.Jalapeno.AntiSpam.Filter.R;
import su.Jalapeno.AntiSpam.Activities.SmsAnalyzerActivity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

public class NotifyBuilder {
	@SuppressWarnings("deprecation")
	public static Notification CreateNotifacation(Context context, long count, boolean withAlert) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.mailb);

		String title = context.getResources().getString(R.string.app_name);

		NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context).setOngoing(true).setLargeIcon(bm)
				.setWhen(System.currentTimeMillis()).setContentInfo(Long.toString(count)).setAutoCancel(false).setContentTitle(title);

		String smsStatus = "";
		int smallIcon = 0;
		if (count == 0) {
			smsStatus = context.getResources().getString(R.string.NotExistsUnknownSms);
			smallIcon = R.drawable.mail;
			notifBuilder.setSmallIcon(smallIcon);
		} else {
			notifBuilder.setNumber((int) count);
			smsStatus = context.getResources().getString(R.string.ExistsUnknownSms);
			smallIcon = R.drawable.spam_ico;
			notifBuilder.setSmallIcon(smallIcon);
		}
		notifBuilder.setContentText(smsStatus);

		Intent notificationIntent = new Intent(context, SmsAnalyzerActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 131, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notifBuilder.setContentIntent(pendingIntent);

		Notification notification = notifBuilder.getNotification();

		if (withAlert) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
		}

		notification.flags |= Notification.FLAG_NO_CLEAR;

		return notification;
	}
}
