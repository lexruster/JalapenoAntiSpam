package su.Jalapeno.AntiSpam.Services.WebService;

import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.PublicKeyResponse;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;

public class JalapenoHttpService {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "JalapenoHttpService";

	private Context _context;
	private Gson _gson;
	
	@Inject
	public JalapenoHttpService(Context context, EncoderService encoderService) {
		_context = context;
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("dd.MM.yy hh:mm:ss");
		_gson = gsonBuilder.serializeNulls().create();
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

	public PublicKeyResponse GetPublicKey(String domain) {
		Logger.Debug(LOG_TAG, "GetPublicKey");
		String requestString = WebClient.Get(GetPublicKeyUrl(domain));
		PublicKeyResponse response = _gson.fromJson(requestString, PublicKeyResponse.class);

		return response;
	}
	
	public static String GetPublicKeyUrl(String domain) {
		return domain + WebConstants.PUBLIC_KEY_URL;
	}
}