package su.Jalapeno.AntiSpam.Util;

import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ResponseData;

public class StrictPolicy implements Policy {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "StrictPolicy";

	private int mLastResponse;
	public ResponseData Response;

	public StrictPolicy() {
		// Set default policy. This will force the application to check the
		// policy on launch.
		mLastResponse = Policy.RETRY;
	}

	/**
	 * Process a new response from the license server. Since we aren't
	 * performing any caching, this equates to reading the LicenseResponse. Any
	 * ResponseData provided is ignored.
	 * 
	 * @param response
	 *            the result from validating the server response
	 * @param rawData
	 *            the raw server response data
	 */
	public void processServerResponse(int response, ResponseData rawData) {
		String info = String.format("processServerResponse response=%s extras=%s", response, rawData != null ? rawData.extra : "null");
		Logger.Debug(LOG_TAG, info);
		mLastResponse = response;
		Response = rawData;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This implementation allows access if and only if a LICENSED response was
	 * received the last time the server was contacted.
	 */
	public boolean allowAccess() {
		return (mLastResponse == Policy.LICENSED);
	}
}