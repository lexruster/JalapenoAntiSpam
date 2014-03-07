package android.test;

import java.util.UUID;

import android.content.Context;
import junit.framework.Assert;
import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Services.WebService.EncoderService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.WebService.JalapenoWebServiceWraper;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.PublicKeyResponse;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.RegisterClientRequest;
import su.Jalapeno.AntiSpam.Services.WebService.Dto.RegisterClientResponse;

public class HttpTest extends AndroidTestCase {
	private EncoderService encoding;
	private SettingsService settings;
	Context cntx;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cntx = getContext();
		settings = new SettingsService(cntx);
		encoding = new EncoderService(settings);
	}

	public void testRegisterClient() {
		JalapenoHttpService jalapenoHttpService = new JalapenoHttpService(cntx,
				encoding);
		// jalapenoHttpService.SendLocalTestRequest();
		JalapenoWebServiceWraper wrap = new JalapenoWebServiceWraper(
				jalapenoHttpService, settings);
		RegisterClientRequest request = new RegisterClientRequest();
		request.ClientId = UUID.randomUUID();
		request.Token = "TOKEN";
		RegisterClientResponse res = wrap.RegisterClient(request);

		Assert.assertEquals(res.WasSuccessful, true);
		// Assert.assertEquals(result, sourse);
	}
	
	public void testGetKey() {
		JalapenoHttpService jalapenoHttpService = new JalapenoHttpService(cntx,
				encoding);
		// jalapenoHttpService.SendLocalTestRequest();
		JalapenoWebServiceWraper wrap = new JalapenoWebServiceWraper(
				jalapenoHttpService, settings);
		PublicKeyResponse res = wrap.GetPublicKey();

		Assert.assertEquals(res.WasSuccessful, true);
	}
}
