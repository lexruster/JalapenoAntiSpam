package su.Jalapeno.AntiSpam.SystemService;

import java.util.concurrent.TimeUnit;

import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Activities.SmsAnalyzerActivity;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.Services.Sms.SmsQueueService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.Log;

public class AppService extends Service {
	private final int NOTIFY_ID = 731957691;

	private SmsQueueService _smsQueueService;
	final String LOG_TAG = "AppService";
	Context _context;

	public void onCreate() {
		super.onCreate();
		_context = this;
		_smsQueueService = new SmsQueueService(RepositoryFactory.getRepository());
		Log.d(LOG_TAG, "onCreate");
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(LOG_TAG, "onStartCommand");

		long count = _smsQueueService.Count();
		Notification notification = CreateNotifacation(count);
		startForeground(NOTIFY_ID, notification);

		someTask();

		return START_STICKY;
		// return super.onStartCommand(intent, flags, startId);
	}

	private Notification CreateNotifacation(long count) {
		Bitmap bm = BitmapFactory.decodeResource(_context.getResources(), R.drawable.mailb);

		Builder notifBuilder = new Notification.Builder(_context).setOngoing(true).setContentTitle("New sms")
				.setContentText("sms received").setSmallIcon(R.drawable.mail).setLargeIcon(bm).setWhen(System.currentTimeMillis())
				.setContentInfo(Long.toString(count)).setAutoCancel(false).setNumber((int) count);

		Intent notificationIntent = new Intent(_context, SmsAnalyzerActivity.class);

		PendingIntent pendingIntent = PendingIntent.getActivity(_context, 131, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notifBuilder.setContentIntent(pendingIntent);

		Notification notification = notifBuilder.getNotification();

		// notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		notification.flags |= Notification.FLAG_NO_CLEAR;

		// NotificationManager notificationManager = (NotificationManager)
		// _context.getSystemService(Context.NOTIFICATION_SERVICE);
		// notificationManager.notify(0, notification);

		return notification;
	}

	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "onDestroy");
		stopForeground(false);
	}

	public IBinder onBind(Intent intent) {
		Log.d(LOG_TAG, "onBind");
		return null;
	}

	void someTask() {
		new Thread(new Runnable() {
			public void run() {
				for (int i = 1; i <= 5; i++) {
					Log.d(LOG_TAG, "i = " + i);
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				stopSelf();
			}
		}).start();
	}
}
