package su.Jalapeno.AntiSpam.Services.SmsReceive;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.Services.*;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.DebugMessage;


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


