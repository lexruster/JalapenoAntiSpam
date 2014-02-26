package su.Jalapeno.AntiSpam.Services.Sms;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Util.ServiceFactory;
import android.content.Context;

public class SmsReceiverWrapper {

    public SmsReceiverWrapper() {
    }

    public boolean Receive(Sms sms, Context context) {
        SettingsService settingsService = new SettingsService(context);
        SmsReceiverLogic smsService = ServiceFactory.GetSmsService(context);

        SmsReceiver smsReceiver = new SmsReceiver(settingsService, smsService);
        return smsReceiver.Receive(sms, context);
    }
}
