package su.Jalapeno.AntiSpam.AntiSpamTest.ServicesTests;

import android.test.AndroidTestCase;
import junit.framework.Assert;
import su.Jalapeno.AntiSpam.Services.JalapenoHttpService;
import su.Jalapeno.AntiSpam.Services.PhoneNumberNormalizer;
import su.Jalapeno.AntiSpam.Services.SettingsService;

/**
 * Created by alexander.kiryushkin on 10.01.14.
 */

//@Test
public class JalapenoHttpServiceTests extends AndroidTestCase {
    public JalapenoHttpServiceTests() {
    }

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



