package su.Jalapeno.AntiSpam.Services.WebService;

import su.Jalapeno.AntiSpam.Util.Constants;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.inject.Inject;

public class JalapenoHttpService {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "JalapenoHttpService";

	private Context _context;
	
	@Inject
	public JalapenoHttpService(Context context, EncoderService encoderService) {
		_context = context;
	}

	public boolean ServiceIsAvailable() {
		ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		}

		NetworkInfo ni = cm.getActiveNetworkInfo();

		if (ni != null && ni.isAvailable() && ni.isConnected()) {
			return true;
		}
		return false;
	}
}