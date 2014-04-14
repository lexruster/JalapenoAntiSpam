package su.Jalapeno.AntiSpam.Services.Sms;

import java.util.Date;

import su.Jalapeno.AntiSpam.DAL.Domain.Sms;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.telephony.SmsMessage;

import com.google.inject.Inject;

public class SmsService {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "SmsService";

	public static final String SMS_URI = "content://sms";

	public static final String ADDRESS = "address";
	public static final String PERSON = "person";
	public static final String DATE = "date";
	public static final String READ = "read";
	public static final String STATUS = "status";
	public static final int STATUS_NONE = -1;
	public static final int STATUS_COMPLETE = 0;
	public static final int STATUS_PENDING = 32;

	public static final String TYPE = "type";
	public static final String BODY = "body";
	public static final String SEEN = "seen";

	public static final int MESSAGE_IS_NOT_READ = 0;
	public static final int MESSAGE_IS_READ = 1;

	public static final int MESSAGE_IS_NOT_SEEN = 0;
	public static final int MESSAGE_IS_SEEN = 1;

	public static final int MESSAGE_TYPE_ALL = 0;
	public static final int MESSAGE_TYPE_INBOX = 1;
	public static final int MESSAGE_TYPE_SENT = 2;
	public static final int MESSAGE_TYPE_DRAFT = 3;
	public static final int MESSAGE_TYPE_OUTBOX = 4;
	public static final int MESSAGE_TYPE_FAILED = 5; // for failed outgoing
														// messages
	public static final int MESSAGE_TYPE_QUEUED = 6; // for messages to send
														// later

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

	public void PutSmsToDatabase(Sms sms, boolean wasRead) {
		ContentResolver contentResolver = _context.getContentResolver();
		// Create SMS row
		ContentValues values = new ContentValues();
		values.put(ADDRESS, sms.SenderId);
		values.put(DATE, sms.RecieveDate.getTime());
		values.put(READ, wasRead ? MESSAGE_IS_READ : MESSAGE_IS_NOT_READ);
		// stupid crutch (need status -1 for remove receivig status)
		values.put(STATUS, sms.Status == STATUS_COMPLETE ? STATUS_NONE : sms.Status);
		values.put(TYPE, MESSAGE_TYPE_INBOX);
		values.put(SEEN, wasRead ? MESSAGE_IS_SEEN : MESSAGE_IS_NOT_SEEN);
		values.put(BODY, sms.Text);
		// Push row into the SMS table
		contentResolver.insert(Uri.parse(SMS_URI), values);
		Logger.Debug(LOG_TAG, "inserted " + sms.SenderId);
	}
}
