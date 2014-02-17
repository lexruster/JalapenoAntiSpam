package su.Jalapeno.AntiSpam.SystemService;

/**
 * Created by alexander.kiryushkin on 13.01.14.
 */

import su.Jalapeno.AntiSpam.Services.SmsReceive.SmsReceiverWrapper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class SmsBroadcastReceiver extends BroadcastReceiver {
    public static final String SMS_EXTRA_NAME = "pdus";

    public SmsBroadcastReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Jalapeno", "SMS income.");
        Boolean isClearFromSpam = true;
        Bundle extras = intent.getExtras();
        String messages = "";
        if (extras != null) {
            Log.i("Jalapeno", "SMS extras not null.");
            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);
            SmsReceiverWrapper smsReceiver = new SmsReceiverWrapper();

            for (int i = 0; i < smsExtra.length; ++i) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                String phone = sms.getOriginatingAddress();
                String message = sms.getMessageBody().toString();
                Log.i("Jalapeno", "SMS: " + message);
                boolean clearSms = smsReceiver.Receive(phone, message, context);
                //putSmsToDatabase( contentResolver, sms );
                if (!clearSms) {
                    isClearFromSpam = false;
                }
            }
        } else {
            Log.e("Jalapeno", "SMS extras NULL.");
        }

        if (!isClearFromSpam) {
            this.abortBroadcast();
        }

        Log.i("Jalapeno", "SMS received.");
    }
}
