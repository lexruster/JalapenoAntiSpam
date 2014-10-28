package android.test;

import java.util.Date;

import junit.framework.Assert;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.RegisterClientResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.Response.WebErrorEnum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonTest extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testJson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("dd.MM.yy hh:mm:ss");
		Gson _gson = gsonBuilder.serializeNulls().create();

		RegisterClientResponse resp = new RegisterClientResponse();
		resp.ErrorMessage = WebErrorEnum.InvalidRequest;
		resp.ExpirationDate = new Date();
		resp.WasSuccessful = true;

		String json = _gson.toJson(resp);
		RegisterClientResponse fromJson = _gson.fromJson(json, RegisterClientResponse.class);
		String json2 = _gson.toJson(fromJson);

		Assert.assertEquals(json, json2);
	}
	
	public void testNullDateJson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("dd.MM.yy hh:mm:ss");
		Gson _gson = gsonBuilder.serializeNulls().create();

		RegisterClientResponse resp = new RegisterClientResponse();
		resp.ErrorMessage = WebErrorEnum.InvalidRequest;
		
		resp.WasSuccessful = true;

		String json = _gson.toJson(resp);
		RegisterClientResponse fromJson = _gson.fromJson(json, RegisterClientResponse.class);
		String json2 = _gson.toJson(fromJson);

		Assert.assertEquals(json, json2);
	}

}
