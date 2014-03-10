package android.test;

import java.util.UUID;

import junit.framework.Assert;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.EncoderService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Services.WebService.WebClient;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.PublicKeyResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.RegisterClientRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.RegisterClientResponse;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HttpTest extends AndroidTestCase {
	private EncoderService encoding;
	private SettingsService settings;
	Context cntx;
	Gson _gson;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cntx = getContext();
		settings = new SettingsService(cntx);
		encoding = new EncoderService(settings);

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("dd.MM.yy hh:mm:ss");
		_gson = gsonBuilder.create();
	}

	public void testRegisterClient() {
		JalapenoHttpService jalapenoHttpService = new JalapenoHttpService(cntx, encoding);
		// jalapenoHttpService.SendLocalTestRequest();
		JalapenoWebServiceWraper wrap = new JalapenoWebServiceWraper(jalapenoHttpService, settings);
		RegisterClientRequest request = new RegisterClientRequest();
		request.ClientId = UUID.randomUUID();
		request.Token = "TOKEN";
		RegisterClientResponse res = wrap.RegisterClient(request);

		Assert.assertEquals(res.WasSuccessful, true);
		// Assert.assertEquals(result, sourse);
	}

	public void testRegisterClient2() {
		RegisterClientRequest request = new RegisterClientRequest();
		request.ClientId = UUID.randomUUID();
		request.Token = "TOKEN";

		RegisterClientResponse response;
		String json = _gson.toJson(request);
		String postData = PrepareJsonRequest(json);
		String requestString = WebClient.Post("http://10.0.2.2:33500/AntispamService.svc/RegisterClient", postData);
		response = _gson.fromJson(requestString, RegisterClientResponse.class);
	}

	private String PrepareJsonRequest(String request) {
		String postData = encoding.Encode(request);
		String jsonPostData = _gson.toJson(postData);

		return jsonPostData;
	}

	public void testGetKey() {
		JalapenoHttpService jalapenoHttpService = new JalapenoHttpService(cntx, encoding);
		// jalapenoHttpService.SendLocalTestRequest();
		JalapenoWebServiceWraper wrap = new JalapenoWebServiceWraper(jalapenoHttpService, settings);
		PublicKeyResponse res = wrap.GetPublicKey();

		Assert.assertEquals(res.WasSuccessful, true);
	}
}
