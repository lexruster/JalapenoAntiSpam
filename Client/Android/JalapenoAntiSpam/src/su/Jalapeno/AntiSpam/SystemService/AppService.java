package su.Jalapeno.AntiSpam.SystemService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.Services.RequestQueue;
import su.Jalapeno.AntiSpam.Services.Sms.SmsQueueService;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.ServiceFactory;
import su.Jalapeno.AntiSpam.Util.UI.NotifyBuilder;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AppService extends Service {
	private final int NOTIFY_ID = 731957691;
	private ScheduledExecutorService scheduleTaskExecutor;
	Context _context;

	private SmsQueueService _smsQueueService;
	private RequestQueue _requestQueue;
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "AppService";

	public void onCreate() {
		super.onCreate();
		_context = this;

		_smsQueueService = new SmsQueueService(RepositoryFactory.getRepository());
		_requestQueue = ServiceFactory.GetRequestQueue(_context);

		StartSchedule();
		Log.d(LOG_TAG, "onCreate");
	}

	private void StartSchedule() {
		scheduleTaskExecutor = Executors.newSingleThreadScheduledExecutor();
		scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				_requestQueue.ProceedComplainRequests();
			}
		}, 0, Constants.COMPLAINS_INTERVAL_SECONDS, TimeUnit.SECONDS);
		Log.d(LOG_TAG, "StartSchedule");
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
