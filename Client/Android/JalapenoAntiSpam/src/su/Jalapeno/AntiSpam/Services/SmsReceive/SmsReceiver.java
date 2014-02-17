package su.Jalapeno.AntiSpam.Services.SmsReceive;

import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.SmsService;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.DebugMessage;
import android.content.Context;
import android.util.Log;


public class SmsReceiver {

    private SettingsService _settingsService;
    private SmsService _smsService;

    public SmsReceiver(SettingsService settingsService, SmsService smsService) {
        _settingsService = settingsService;
        _smsService = smsService;
    }

    public boolean Receive(String phone, String message, Context context) {
        Config config = _settingsService.LoadSettings();
        Log.i("Jalapeno", "Receive: ");
        if (!config.Enabled) {
            return true;
        }
        Boolean isClearFromSpam;
        isClearFromSpam = _smsService.Receive(phone, message);

        String messageOut = String.format("%s\n%s\nSpam: %s", phone, message, !isClearFromSpam);
        DebugMessage.Debug(context, messageOut);

        Log.i("Jalapeno", messageOut);
        return isClearFromSpam;
    }
}


