package su.Jalapeno.AntiSpam.SystemService;

import java.util.concurrent.TimeUnit;

import su.Jalapeno.AntiSpam.R;
import su.Jalapeno.AntiSpam.Activities.SmsAnalyzerActivity;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.Services.Sms.SmsQueueService;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.UI.NotifyBuilder;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AppService extends Service {
	private final int NOTIFY_ID = 731957691;

	private SmsQueueService _smsQueueService;
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "AppService";
	Context _context;

	public void onCreate() {
		super.onCreate();
		_context = this;
		_smsQueueService = new SmsQueueService(
				RepositoryFactory.getRepository());
		Log.d(LOG_TAG, "onCreate");
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(LOG_TAG, "onStartCommand flag " + flags + " start " + startId);

		if (_smsQueueService != null) {
			long count = _smsQueueService.Count();
			Notification notification = NotifyBuilder.CreateNotifacation(_context, count);
			Log.d(LOG_TAG, "Start notify count " + count);
			startForeground(NOTIFY_ID, notification);
		} else {
			Log.d(LOG_TAG, "onStartCommand _smsQueueService = null ");
		}
		someTask();

		return START_STICKY;
		// return super.onStartCommand(intent, flags, startId);
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
		/*
		 * new Thread(new Runnable() { public void run() { for (int i = 1; i <=
		 * 5; i++) { Log.d(LOG_TAG, "i = " + i); try {
		 * TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) {
		 * e.printStackTrace(); } } stopSelf(); } }).start();
		 */
	}
}
