package su.Jalapeno.AntiSpam.Services.SmsReceive;

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

    public boolean Receive(String phone, String message, Context context) {
        Config config = _settingsService.LoadSettings();
        Log.i("Jalapeno", "Receive: ");
        if (!config.Enabled) {
            return true;
        }
        Boolean isClearFromSpam;
        isClearFromSpam = _smsReceiverLogic.Receive(phone, message);

        String messageOut = String.format("%s\n%s\nSpam: %s", phone, message, !isClearFromSpam);
        DebugMessage.Debug(context, messageOut);

        Log.i("Jalapeno", messageOut);
        return isClearFromSpam;
    }
}


