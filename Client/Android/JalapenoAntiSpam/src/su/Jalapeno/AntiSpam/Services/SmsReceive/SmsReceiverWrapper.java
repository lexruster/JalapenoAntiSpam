package su.Jalapeno.AntiSpam.Services.SmsReceive;

import android.content.Context;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.Services.*;

public class SmsReceiverWrapper {

    public SmsReceiverWrapper() {
    }

    public boolean Receive(String phone, String message, Context context) {
        SettingsService settingsService = new SettingsService(context);
        JalapenoHttpService jalapenoHttpService = new JalapenoHttpService(context);
        
        ContactsService contactsService=new ContactsService(context);
        SmsService smsService = new SmsService(contactsService, jalapenoHttpService, new UserValidateService(),
                new LocalSpamBaseService(RepositoryFactory.getRepository()), new RequestQueue(jalapenoHttpService), settingsService);

        SmsReceiver smsReceiver = new SmsReceiver(settingsService, smsService);
        return smsReceiver.Receive(phone, message, context);
    }
}
