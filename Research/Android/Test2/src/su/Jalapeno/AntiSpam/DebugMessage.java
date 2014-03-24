package su.Jalapeno.AntiSpam;

import android.content.Context;
import android.widget.Toast;


	public class DebugMessage {
	    public final boolean InDebug=true;

	    public static void Debug(Context context, String message)
	    {
	        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	    }
	}
