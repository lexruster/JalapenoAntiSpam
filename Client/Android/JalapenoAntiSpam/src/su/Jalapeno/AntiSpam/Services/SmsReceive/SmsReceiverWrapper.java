package su.Jalapeno.AntiSpam.Services.SmsReceive;

import android.content.Context;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.Services.*;
import su.Jalapeno.AntiSpam.Util.ServiceFactory;

public class SmsReceiverWrapper {

    public SmsReceiverWrapper() {
    }

    public boolean Receive(String phone, String message, Context context) {
        SettingsService settingsService = new SettingsService(context);
        SmsReceiverLogic smsService = ServiceFactory.GetSmsService(context);

        SmsReceiver smsReceiver = new SmsReceiver(settingsService, smsService);
        return smsReceiver.Receive(phone, message, context);
    }
}
