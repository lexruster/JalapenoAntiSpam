package su.Jalapeno.AntiSpam.Services.Sms;

import java.util.Date;

import com.google.inject.Inject;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Util.Constants;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsService {
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

	@Inject
	public SmsService(Context context) {
		_context = context;
	}

	public Sms SmsFromPdus(Object[] pdus, Context context) {
		Sms sms = new Sms();
		for (int i = 0; i < pdus.length; i++) {
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
			sms.Text += smsMessage.getMessageBody();
		}

		SmsMessage first = SmsMessage.createFromPdu((byte[]) pdus[0]);

		sms.SenderId = first.getOriginatingAddress();
		Date receiveDate = new Date(first.getTimestampMillis());
		sms.RecieveDate = receiveDate;
		sms.Status = first.getStatus();

		return sms;
	}

	public void PutSmsToDatabase(Sms sms) {
		ContentResolver contentResolver = _context.getContentResolver();
		// Create SMS row
		ContentValues values = new ContentValues();
		values.put(ADDRESS, sms.SenderId);
		values.put(DATE, sms.RecieveDate.getTime());
		values.put(READ, MESSAGE_IS_NOT_READ);
		values.put(STATUS, sms.Status);
		values.put(TYPE, MESSAGE_TYPE_INBOX);
		values.put(SEEN, MESSAGE_IS_NOT_SEEN);
		values.put(BODY, sms.Text);
		// Push row into the SMS table
		contentResolver.insert(Uri.parse(SMS_URI), values);
		Log.d(LOG_TAG, "inserted " + sms.SenderId);
	}

}
