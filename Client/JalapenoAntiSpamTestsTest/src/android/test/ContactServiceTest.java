package android.test;

import junit.framework.Assert;
import su.Jalapeno.AntiSpam.Services.ContactsService;

public class ContactServiceTest extends AndroidTestCase {
	 
	@Override
	    protected void setUp() throws Exception {
	        super.setUp();
	    }

	    public void testContact() {
	        ContactsService contactService= new ContactsService();
	        boolean inContact = contactService.PhoneInContact("4523234324");

	        Assert.assertEquals(inContact, false);
	    }
}