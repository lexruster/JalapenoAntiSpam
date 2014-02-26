package su.Jalapeno.AntiSpam.Util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by alexander.kiryushkin on 13.01.14.
 */
public class DebugMessage {
    public final boolean InDebug=true;

    public static void Debug(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
