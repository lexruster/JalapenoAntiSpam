package su.Jalapeno.AntiSpam.Services.WebService;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;

import su.Jalapeno.AntiSpam.Services.WebService.Dto.IsSpammerRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.IsSpammerResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.RegisterClientRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.RegisterClientResponse;
import su.Jalapeno.AntiSpam.Util.Constants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Created by Kseny on 30.12.13.
 */
public class JalapenoHttpService {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "JalapenoHttpService";

	private Context _context;

	private EncoderService _encoderService;

	@Inject
	public JalapenoHttpService(Context context, EncoderService encoderService) {
		_context = context;
		_encoderService = encoderService;
	}

	public IsSpammerResponse IsSpamerRequest(IsSpammerRequest isSpammerRequest) {
		Log.d(LOG_TAG, "IsSpamerRequest " + isSpammerRequest.SenderId
				+ " CLientId " + isSpammerRequest.ClientId);
		return new IsSpammerResponse();
	}

	public boolean TryComplain(String phone) {
		return true;
	}

	public String SendLocalTestRequest() {

		return SendRequest("http://localhost/TestWeb/");
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

	private String SendRequest(String url) {
		String responseBody = "";

		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(new HttpGet(url));
			HttpEntity entity = response.getEntity();
			responseBody = EntityUtils.toString(entity);
		} catch (IOException ex) {

		}

		return responseBody;
	}

	public RegisterClientResponse RegisterClient(RegisterClientRequest request) {
		RegisterClientResponse response = new RegisterClientResponse();

		// Gson gson = new Gson();
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("M/d/yy hh:mm a");
		Gson gson = gsonBuilder.create();
		String json = gson.toJson(request);
		
		String post=_encoderService.Encode(json);
		

		return null;
	}
}
