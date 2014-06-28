package su.Jalapeno.AntiSpam.Services.Sms;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Util.ServiceFactory;
import android.content.Context;

public class SmsReceiverWrapper {

	public SmsReceiverWrapper() {
	}

	public boolean Receive(Sms sms, Context context) {
		SmsReceiver smsReceiver = ServiceFactory.GetSmsReceiver(context);
		return smsReceiver.Receive(sms, context);
	}
}
