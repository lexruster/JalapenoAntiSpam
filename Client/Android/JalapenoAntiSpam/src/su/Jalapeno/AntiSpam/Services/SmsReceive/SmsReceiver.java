package su.Jalapeno.AntiSpam.Services.SmsReceive;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.SmsReceiverLogic;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.DebugMessage;
import android.content.Context;
import android.util.Log;


public class SmsReceiver {

    private SettingsService _settingsService;
    private SmsReceiverLogic _smsReceiverLogic;

    public SmsReceiver(SettingsService settingsService, SmsReceiverLogic smsReceiverLogic) {
        _settingsService = settingsService;
        _smsReceiverLogic = smsReceiverLogic;
    }

    public boolean Receive(Sms sms, Context context) {
        Config config = _settingsService.LoadSettings();
        if (!config.Enabled) {
            return true;
        }
        Boolean isClearFromSpam;
        isClearFromSpam = _smsReceiverLogic.Receive(sms);

        String messageOut = String.format("%s\n%s\nSpam: %s", sms.SenderId, sms.Text, !isClearFromSpam);
        DebugMessage.Debug(context, messageOut);

        Log.i("Jalapeno", messageOut);
        return isClearFromSpam;
    }
}


