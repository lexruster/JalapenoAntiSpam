package su.Jalapeno.AntiSpam.Util;

import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ResponseData;

public class StrictPolicy implements Policy {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "StrictPolicy";
	 public static final int NotSpam = 512;
     public static final int Spam = 1122;
	 public static final int Network = 582;

	private int smsResp;
	public ResponseData Response;

	public StrictPolicy() {
		smsResp = Network;
	}

	public void processServerResponse(int response, ResponseData rawData) {
		String info = String.format("processServerResponse ResponseCode=%s extras=%s, String=%s",
				response, 
				rawData != null ? rawData.extra : "null",
				rawData != null ? rawData.toString() : "null");
		Logger.Debug(LOG_TAG, info);
		if (rawData != null) {
			Logger.Debug(LOG_TAG, rawData.toString());
		}
		Logger.Debug(LOG_TAG, info);
		smsResp = response;
		Response = rawData;
	}

	public boolean allowAccess() {
		return (smsResp*2 == NotSpam);
	}
}