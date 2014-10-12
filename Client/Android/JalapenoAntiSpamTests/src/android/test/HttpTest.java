package android.test;

import java.util.UUID;

import junit.framework.Assert;
import su.Jalapeno.AntiSpam.Services.AccessService;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.EncoderService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Services.WebService.WebClient;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Request.RegisterClientRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.RegisterClientResponse;
import su.Jalapeno.AntiSpam.Util.ServiceFactory;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HttpTest extends AndroidTestCase {
	private EncoderService encoding;
	private SettingsService settings;
	private AccessService _accessService;
	Context cntx;
	Gson _gson;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cntx = getContext();
		settings = ServiceFactory.GetSettingsService(cntx);
		encoding = new EncoderService();

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("dd.MM.yy hh:mm:ss");
		_gson = gsonBuilder.create();
	}

	public void testRegisterClient() {
		JalapenoHttpService jalapenoHttpService = new JalapenoHttpService(cntx,
				encoding);
		EncoderService encoder = new EncoderService();
		JalapenoWebServiceWraper wrap = new JalapenoWebServiceWraper(
				jalapenoHttpService, settings, encoder, _accessService);
		RegisterClientRequest request = new RegisterClientRequest(UUID.randomUUID());
		request.Token = "TOKEN";
		RegisterClientResponse res = wrap.RegisterClient(request);

		Assert.assertEquals(res.WasSuccessful, true);
	}

	public void testRegisterClient2() {
		RegisterClientRequest request = new RegisterClientRequest(UUID.randomUUID());
		request.Token = "TOKEN";

		String json = _gson.toJson(request);
		String postData = PrepareJsonRequest(json);
		String requestString = WebClient.Post(
				"http://10.0.2.2:33500/AntispamService.svc/RegisterClient",
				postData);
		RegisterClientResponse response = _gson.fromJson(requestString,
				RegisterClientResponse.class);
		Assert.assertEquals(response.WasSuccessful, true);
	}

	private String PrepareJsonRequest(String request) {
		String postData = encoding.Encode(request);
		String jsonPostData = _gson.toJson(postData);

		return jsonPostData;
	}
}
