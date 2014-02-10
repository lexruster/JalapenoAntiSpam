package su.Jalapeno.AntiSpam.AntiSpamTest;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import junit.framework.Assert;
import su.Jalapeno.AntiSpam.Activities.Settings;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class su.Jalapeno.AntiSpam.Activities.SettingsTest \
 * su.Jalapeno.AntiSpam.tests/android.test.InstrumentationTestRunner
 */
public class SettingsTest extends ActivityInstrumentationTestCase2<Settings> {

    private Settings activity;

    public SettingsTest() {
        super("com.Jalapeno.AntiSpam", Settings.class);
        //super(Settings.class);
    }

    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();
        activity = getActivity();

    }

    public void testControlsCreated() {
        assertNotNull(activity);
    }

    public void testSettingsEx() throws Exception {

        Settings activity = getActivity();
        assertEquals(true, true);
    }

    public void testYettingsTest() {
        Settings activity = getActivity();
        assertEquals(true, true);
    }

}
