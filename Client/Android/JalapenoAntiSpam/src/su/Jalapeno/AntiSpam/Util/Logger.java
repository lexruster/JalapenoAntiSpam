package su.Jalapeno.AntiSpam.Util;

import android.util.Log;

public class Logger {
	public static final boolean LOG_DEBUG = true;

	public static void Debug(String lOG_TAG, String string) {
		if (LOG_DEBUG) {
			Log.d(lOG_TAG, string);
		}
	}

	public static void Error(String lOG_TAG, String string) {
		Log.e(lOG_TAG, string);
	}

	public static void Error(String lOG_TAG, String string, Exception ex) {
		Log.e(lOG_TAG, string, ex);
	}
}
