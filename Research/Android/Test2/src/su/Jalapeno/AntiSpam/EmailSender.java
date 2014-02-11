package su.Jalapeno.AntiSpam;

import android.app.Activity;
import android.content.Intent;

public class EmailSender {

    private Activity _activity;

    public EmailSender(Activity activity)
    {
        _activity = activity;
    }

    public EmailSender(MainActivity activity) {
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
            DebugMessage.Debug(_activity, "There are no email clients installed.");
        }
    }
}
