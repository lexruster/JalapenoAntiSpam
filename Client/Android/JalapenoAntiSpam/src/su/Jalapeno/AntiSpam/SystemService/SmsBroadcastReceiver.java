package su.Jalapeno.AntiSpam.SystemService;

/**
 * Created by alexander.kiryushkin on 13.01.14.
 */

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.Sms.SmsReceiverWrapper;
import su.Jalapeno.AntiSpam.Services.Sms.SmsService;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class SmsBroadcastReceiver extends BroadcastReceiver {

	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "SmsBroadcastReceiver";

	public SmsBroadcastReceiver() {

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.Debug(LOG_TAG, "SMS income version " + Build.VERSION.SDK_INT);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Logger.Debug(LOG_TAG, "SMS income in KitKat.");
			return;
		}

		SmsService smsService = new SmsService(context);
		Logger.Debug(LOG_TAG, "SMS income.");

		Boolean isClearFromSpam = true;
		Bundle bundle = intent.getExtras();
		Object[] pdus = (Object[]) bundle.get("pdus");

		if (pdus.length == 0) {
			Logger.Error(LOG_TAG, "SMS extras NULL.");
			return;
		}

		Logger.Debug(LOG_TAG, "SMS extras not null.");

		Sms sms = smsService.SmsFromPdus(pdus, context);
		SmsReceiverWrapper smsReceiver = new SmsReceiverWrapper();

		Logger.Debug(LOG_TAG, "Sender: " + sms.SenderId + " SMS: " + sms.Text);
		isClearFromSpam = smsReceiver.Receive(sms, context);

		if (!isClearFromSpam) {
			Logger.Debug(LOG_TAG, "SMS aborted.");
			abortBroadcast();
			return;
		}

		Logger.Debug(LOG_TAG, "SMS received.");
	}
}
