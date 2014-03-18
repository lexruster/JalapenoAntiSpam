package su.Jalapeno.AntiSpam.Util.UI;

import su.Jalapeno.AntiSpam.Util.Constants;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by alexander.kiryushkin on 13.01.14.
 */
public class DebugMessage {
	public static void Debug(Context context, String message) {
		if (Constants.IS_DEBUG) {
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		}
	}
}
