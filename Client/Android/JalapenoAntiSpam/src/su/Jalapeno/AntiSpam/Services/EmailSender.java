package su.Jalapeno.AntiSpam.Services;

import su.Jalapeno.AntiSpam.Util.UI.AlertMessage;
import android.app.Activity;
import android.content.Intent;

/**
 * Created by alexander.kiryushkin on 21.01.14.
 */
public class EmailSender {

    private Activity _activity;

    public EmailSender(Activity activity)
    {
        _activity = activity;
    }

    public void SendEmail(String recipient, String subject, String text)
    {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{recipient});
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT   , text);
        try {
            _activity.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            AlertMessage.Alert(_activity, "There are no email clients installed.");
        }
    }
}
