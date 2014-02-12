package su.Jalapeno.AntiSpam.AntiSpamTest.ServicesTests;

import java.io.Console;

import android.test.AndroidTestCase;
import junit.framework.Assert;
import su.Jalapeno.AntiSpam.Services.PhoneNumberNormalizer;
import su.Jalapeno.AntiSpam.Services.SettingsService;

/**
 * Created by alexander.kiryushkin on 10.01.14.
 */

//@Test
public class PhoneNumberNormalizerTests extends AndroidTestCase {

    SettingsService settingsMock;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public PhoneNumberNormalizerTests() {
    }

    public void testNormalize() {
        PhoneNumberNormalizer phoneNumberNormalizer = new PhoneNumberNormalizer();
        String sourse = "dsfsdf";
        String result = phoneNumberNormalizer.NormalizePhoneNumber("dsfsdf");

        Assert.assertEquals(result, sourse);
        
    }
}


