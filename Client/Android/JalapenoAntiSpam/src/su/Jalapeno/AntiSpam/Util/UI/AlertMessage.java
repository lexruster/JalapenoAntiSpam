package su.Jalapeno.AntiSpam.Util.UI;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by alexander.kiryushkin on 13.01.14.
 */
public class AlertMessage {
	public static void Alert(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
