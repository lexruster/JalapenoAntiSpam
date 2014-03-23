package su.Jalapeno.AntiSpam.SystemService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import roboguice.service.RoboService;
import su.Jalapeno.AntiSpam.Services.RequestQueue;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.Sms.SmsQueueService;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import su.Jalapeno.AntiSpam.Util.UI.NotifyBuilder;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.google.inject.Inject;

public class AppService extends RoboService {
	private final int NOTIFY_ID = 731957691;
	private ScheduledExecutorService scheduleTaskExecutor;

	@Inject
	Context _context;
	@Inject
	private SmsQueueService _smsQueueService;
	@Inject
	private RequestQueue _requestQueue;

	@Inject
	private SettingsService _settingsService;

	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "AppService";

	public void onCreate() {
		super.onCreate();
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
		if (_settingsService.AntispamEnabled()) {
			boolean needAlarm = intent.getIntExtra("Alarm", 0) == 1;
			StartNotify(needAlarm);
		} else {
			StopNotify();
		}

		return START_STICKY;
	}

	private void StopNotify() {
		stopForeground(true);
	}

	private void StartNotify(boolean needAlarm) {
		Logger.Debug(LOG_TAG, "StartNotify");
		if (_smsQueueService != null) {
			long count = _smsQueueService.Count();
			Notification notification = NotifyBuilder.CreateNotifacation(_context, count, needAlarm);
			Logger.Debug(LOG_TAG, "Start notify count " + count);
			startForeground(NOTIFY_ID, notification);
		} else {
			Logger.Debug(LOG_TAG, "onStartCommand _smsQueueService = null ");
		}
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
}
