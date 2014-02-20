package su.Jalapeno.AntiSpam.SystemService;

/**
 * Created by alexander.kiryushkin on 13.01.14.
 */

import org.secure.sms.StringCryptor;

import su.Jalapeno.AntiSpam.Services.SmsReceive.SmsReceiverWrapper;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class SmsBroadcastReceiver extends BroadcastReceiver {
    public static final String SMS_EXTRA_NAME = "pdus";

    public SmsBroadcastReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Jalapeno", "SMS income.");
        Boolean isClearFromSpam = true;
        Bundle extras = intent.getExtras();
        //String messages = "";
        if (extras != null) {
            Log.i("Jalapeno", "SMS extras not null.");
            // Get received SMS array
            Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);
            SmsReceiverWrapper smsReceiver = new SmsReceiverWrapper();

            for (int i = 0; i < smsExtra.length; ++i) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
                String phone = sms.getOriginatingAddress();
                String message = sms.getMessageBody().toString();
                Log.i("Jalapeno", "SMS: " + message);
                boolean clearSms = smsReceiver.Receive(phone, message, context);
                //putSmsToDatabase( contentResolver, sms );
                if (!clearSms) {
                    isClearFromSpam = false;
                }
            }
        } else {
            Log.e("Jalapeno", "SMS extras NULL.");
        }

        if (!isClearFromSpam) {
            this.abortBroadcast();
        }

        Log.i("Jalapeno", "SMS received.");
    }
    /*
    public static Sms fromPdus(Object[] pdus, Context context)
    {
        Sms result = new Sms();
        for (int i = 0; i < pdus.length; i++)
        {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);
            result._body += sms.getMessageBody();
        }

        SmsMessage first = SmsMessage.createFromPdu((byte[]) pdus[0]);
        result._sender = new Sender(first.getOriginatingAddress(), context);
        result._timestamp = first.getTimestampMillis();

        return result;
    }
    sms-
    private String _body;
private Sender _sender;
private long _timestamp;
    */
    
    //public static final String SMS_EXTRA_NAME = "pdus";
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
    
    private void putSmsToDatabase( ContentResolver contentResolver, SmsMessage sms )
	{
		// Create SMS row
        ContentValues values = new ContentValues();
        values.put( ADDRESS, sms.getOriginatingAddress() );
        values.put( DATE, sms.getTimestampMillis() );
        values.put( READ, MESSAGE_IS_NOT_READ );
        values.put( STATUS, sms.getStatus() );
        values.put( TYPE, MESSAGE_TYPE_INBOX );
        values.put( SEEN, MESSAGE_IS_NOT_SEEN );
        try
        {
        	values.put( BODY, sms.getMessageBody().toString() );
        }
        catch ( Exception e ) 
        { 
        	e.printStackTrace(); 
    	}
        
        // Push row into the SMS table
        contentResolver.insert( Uri.parse( SMS_URI ), values );
	}
}
