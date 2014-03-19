package su.Jalapeno.AntiSpam.Services.WebService;

import su.Jalapeno.AntiSpam.Services.WebService.Dto.ComplainRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.ComplainResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.IsSpammerRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.IsSpammerResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.PublicKeyResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.RegisterClientRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.RegisterClientResponse;
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
	private EncoderService _encoderService;

	@Inject
	public JalapenoHttpService(Context context, EncoderService encoderService) {
		_context = context;
		_encoderService = encoderService;

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("dd.MM.yy hh:mm:ss");
		_gson = gsonBuilder.create();
	}

	public boolean ServiceIsAvailable() {
		ConnectivityManager cm = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		}

		NetworkInfo ni = cm.getActiveNetworkInfo();

		if (ni != null && ni.isAvailable() && ni.isConnected()) {
			return true;
		}
		return false;
	}

	public IsSpammerResponse IsSpamerRequest(IsSpammerRequest request) {
		IsSpammerResponse response;
		String json = _gson.toJson(request);
		Logger.Debug(LOG_TAG, "IsSpamerRequest " + json);
		String postData = PrepareJsonRequest(json);
		String requestString = WebClient.Post(WebConstants.IS_SPAMMER_URL,
				postData);
		response = _gson.fromJson(requestString, IsSpammerResponse.class);

		return response;
	}

	public RegisterClientResponse RegisterClient(RegisterClientRequest request) {
		RegisterClientResponse response;
		String json = _gson.toJson(request);
		Logger.Debug(LOG_TAG, "RegisterClient " + json);
		String postData = PrepareJsonRequest(json);
		String requestString = WebClient.Post(WebConstants.REGISTER_CLIENT_URL,
				postData);
		response = _gson.fromJson(requestString, RegisterClientResponse.class);

		return response;
	}

	public PublicKeyResponse GetPublicKey() {
		Logger.Debug(LOG_TAG, "GetPublicKey");
		String requestString = WebClient.Get(WebConstants.PUBLIC_KEY_URL);
		PublicKeyResponse response = _gson.fromJson(requestString,
				PublicKeyResponse.class);

		return response;
	}

	public ComplainResponse Complain(ComplainRequest request) {
		ComplainResponse response;
		String json = _gson.toJson(request);
		Logger.Debug(LOG_TAG, "Complain " + json);
		String postData = PrepareJsonRequest(json);
		String requestString = WebClient.Post(WebConstants.COMPLAIN_URL,
				postData);
		response = _gson.fromJson(requestString, ComplainResponse.class);

		return response;
	}

	private String PrepareJsonRequest(String request) {
		String postData = _encoderService.Encode(request);
		String jsonPostData = _gson.toJson(postData);

		return jsonPostData;
	}
}