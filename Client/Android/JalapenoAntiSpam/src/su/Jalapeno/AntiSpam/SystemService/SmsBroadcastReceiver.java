package su.Jalapeno.AntiSpam.SystemService;

/**
 * Created by alexander.kiryushkin on 13.01.14.
 */

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.SmsService;
import su.Jalapeno.AntiSpam.Services.SmsReceive.SmsReceiverWrapper;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsBroadcastReceiver extends BroadcastReceiver {

	public SmsBroadcastReceiver() {

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		SmsService smsService = new SmsService(context);
		Log.i("Jalapeno", "SMS income.");

		Boolean isClearFromSpam = true;
		Bundle bundle = intent.getExtras();
		Object[] pdus = (Object[]) bundle.get("pdus");

		if (pdus.length == 0) {
			Log.e("Jalapeno", "SMS extras NULL.");
			return;
		}

		Log.i("Jalapeno", "SMS extras not null.");

		Sms sms = smsService.SmsFromPdus(pdus, context);
		SmsReceiverWrapper smsReceiver = new SmsReceiverWrapper();

		Log.i("Jalapeno", "Sender: " + sms.SenderId + " SMS: " + sms.Text);
		isClearFromSpam = smsReceiver.Receive(sms, context);

		if (!isClearFromSpam) {
			Log.i("Jalapeno", "SMS aborted.");
			abortBroadcast();
			return;
		}

		Log.i("Jalapeno", "SMS received.");
	}
}
