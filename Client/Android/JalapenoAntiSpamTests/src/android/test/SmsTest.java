package android.test;

import java.util.Date;

import su.Jalapeno.AntiSpam.Services.Sms.SmsService;
import su.Jalapeno.AntiSpam.Util.Constants;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public class SmsTest extends AndroidTestCase {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "SmsService";

	public static final String SMS_URI = "content://sms";

	public static final String ADDRESS = "address";
	public static final String PERSON = "person";
	public static final String DATE = "date";
	public static final String READ = "read";
	public static final String STATUS = "status";
	public static final String TYPE = "type";
	public static final String BODY = "body";
	public static final String SEEN = "seen";

	public static final int MESSAGE_TYPE_INBOX = 1;
	public static final int MESSAGE_TYPE_SENT = 2;

	public static final int MESSAGE_IS_NOT_READ = 0;
	public static final int MESSAGE_IS_READ = 1;

	public static final int MESSAGE_IS_NOT_SEEN = 0;
	public static final int MESSAGE_IS_SEEN = 1;

	private Context _context;
	SmsService smsService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		_context = getContext();
		smsService = new SmsService(_context);
	}

	public void testSmsSave() {

		PutSmsToDatabase("Sender", "1", 0, 0, 0, 1);
		PutSmsToDatabase("Sender", "2", 1, 0, 0, 1);
		PutSmsToDatabase("Sender", "3", 0, 1, 0, 1);
		PutSmsToDatabase("Sender", "4", 1, 1, 0, 1);
		PutSmsToDatabase("Sender", "5", 0, 0, 0, 2);
		PutSmsToDatabase("Sender", "6", 0, 0, -1, 1);
		PutSmsToDatabase("Sender", "7", 0, 0, 32, 1);
		PutSmsToDatabase("Sender", "7", 0, 0, 32, 1);
		PutSmsToDatabase("Sender", "7", 0, 0, 32, 1);
		PutSmsToDatabase("Sender", "7", 0, 0, 32, 1);
		PutSmsToDatabase("Sender", "7", 0, 0, 32, 1);
		
		PutSmsToDatabase("Sender2", "1", 1, 1, -1, 1);
		PutSmsToDatabase("Sender2", "2", 0, 0, -1, 1);
		PutSmsToDatabase("Sender2", "3", 1, 1, -1, 1);
		
		PutSmsToDatabase("Sender3", "1", 1, 1, -1, 1);
		PutSmsToDatabase("Sender3", "2", 1, 1, -1, 1);
		
		
		
	}

	public void PutSmsToDatabase(String sender, String text, int read, int seen, int status, int inbox) {
		ContentResolver contentResolver = _context.getContentResolver();
		// Create SMS row
		ContentValues values = new ContentValues();
		values.put(ADDRESS, sender);
		values.put(DATE, new Date().getTime());
		values.put(READ, read);
		values.put(STATUS, status);
		values.put(TYPE, inbox);
		values.put(SEEN, seen);
		values.put(BODY, text);
		// Push row into the SMS table
		contentResolver.insert(Uri.parse(SMS_URI), values);

	}
}
