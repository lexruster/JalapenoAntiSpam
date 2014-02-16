package android.test;

import su.Jalapeno.AntiSpam.Services.JalapenoHttpService;

public class HttpTest extends AndroidTestCase {
	@Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testSendRequest() {
        JalapenoHttpService jalapenoHttpService = new JalapenoHttpService(getContext());
        jalapenoHttpService.SendLocalTestRequest();

        //Assert.assertEquals(result, sourse);
    }
}


