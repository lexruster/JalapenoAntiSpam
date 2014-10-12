package su.Jalapeno.AntiSpam.Util;

public class Constants {
	public final static String BEGIN_LOG_TAG = "JalLog ";

	public final static String BROADCAST_SMS_ANALYZER_ACTION = "su.Jalapeno.AntiSpam.BROADCAST_SMS_ANALYZER_ACTION";
	public final static String BROADCAST_TRASH_SMS_ACTION = "su.Jalapeno.AntiSpam.BROADCAST_TRASH_SMS_ACTION";

	// for server interaction
	public static final String DATE_TIME_FORMAT = "yyyy.MM.dd HH:mm:ss";
	public final static String DEFAULT_ENCODING = "UTF-8";
	public final static long COMPLAINS_INTERVAL_SECONDS = 30;
	public static final long COMPLAIN_BATCH_SIZE = 5;
	public final static int MIN_MESSAGE_LENGTH = 50;

	// debug configs
	public static final boolean FULL_DEBUG = true;
	public static final boolean LOG_DEBUG = FULL_DEBUG;
	public static final boolean VIEW_DEBUG_UI = FULL_DEBUG;
}
