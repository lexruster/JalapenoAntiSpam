package su.Jalapeno.AntiSpam.Util;

import android.util.Log;

public class Logger {
	public static final boolean LOG_DEBUG = true;

	public static void Debug(String LOG_TAG, String string) {
		if (LOG_DEBUG) {
			Log.d(LOG_TAG, string);
		}
	}

	public static void Error(String LOG_TAG, String string) {
		Log.e(LOG_TAG, string);
	}

	public static void Error(String LOG_TAG, String string, Exception ex) {
		Log.e(LOG_TAG, string, ex);
	}
}
