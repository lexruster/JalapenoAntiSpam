package su.Jalapeno.AntiSpam.SystemService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;

import roboguice.service.RoboService;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.Services.RequestQueue;
import su.Jalapeno.AntiSpam.Services.Sms.SmsQueueService;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.ServiceFactory;
import su.Jalapeno.AntiSpam.Util.UI.NotifyBuilder;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class AppService extends RoboService{
	private final int NOTIFY_ID = 731957691;
	private ScheduledExecutorService scheduleTaskExecutor;
	
	@Inject
	Context _context;
	@Inject
	private SmsQueueService _smsQueueService;
	@Inject
	private RequestQueue _requestQueue;
	
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "AppService";

	public void onCreate() {
		super.onCreate();
		//_context = this;

		//_smsQueueService = new SmsQueueService(RepositoryFactory.getRepository());
		//_requestQueue = ServiceFactory.GetRequestQueue(_context);

		StartSchedule();
		Logger.Debug(LOG_TAG, "onCreate");
	}

	private void StartSchedule() {
		scheduleTaskExecutor = Executors.newSingleThreadScheduledExecutor();
		scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				_requestQueue.ProceedComplainRequests();
			}
		}, 0, Constants.COMPLAINS_INTERVAL_SECONDS, TimeUnit.SECONDS);
		Logger.Debug(LOG_TAG, "StartSchedule");
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.Debug(LOG_TAG, "onStartCommand flag " + flags + " start " + startId);

		if (_smsQueueService != null) {
			long count = _smsQueueService.Count();
			boolean needAlarm = intent.getIntExtra("Alarm", 0) == 1;
			Notification notification = NotifyBuilder.CreateNotifacation(_context, count, needAlarm);
			Logger.Debug(LOG_TAG, "Start notify count " + count);
			startForeground(NOTIFY_ID, notification);
		} else {
			Logger.Debug(LOG_TAG, "onStartCommand _smsQueueService = null ");
		}
		someTask();

		return START_STICKY;
	}

	public void onDestroy() {
		super.onDestroy();
		Logger.Debug(LOG_TAG, "onDestroy");
		stopForeground(false);
	}

	public IBinder onBind(Intent intent) {
		Logger.Debug(LOG_TAG, "onBind");
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
