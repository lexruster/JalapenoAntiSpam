package android.test;

import java.util.ArrayList;

import junit.framework.Assert;
import su.Jalapeno.AntiSpam.Services.ContactsService;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactServiceTest extends AndroidTestCase {

	private Context _context;
	ContactsService contactService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		_context = getContext();
		contactService = new ContactsService(_context);
	}

	public void testContact() {

		boolean inContact = contactService.PhoneInContact("4523234324");

		Assert.assertEquals(inContact, false);
	}

	public void testReceiveCont() {

		ArrayList<String> phones = new ArrayList<String>();

		Cursor cursor = _context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		while (cursor.moveToNext()) {
			/*String name = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));*/
			String phoneNumber = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

			phones.add(phoneNumber);

		}
		cursor.close();
	}

	public void testReceiveCont1() {
		boolean res1 = contactService.PhoneInContact("3242");// false
		boolean res2 = contactService.PhoneInContact("sdfs");// false
		boolean res3 = contactService.PhoneInContact("+79689264552");// true - full equal
		boolean res4 = contactService.PhoneInContact("89689264552");// true
		boolean res5 = contactService.PhoneInContact("8544564544");// false
		boolean res6 = contactService.PhoneInContact("*102#");// true - full equal
		boolean res7 = contactService.PhoneInContact("+7 988 497-26-66");// true - full equal
		boolean res8 = contactService.PhoneInContact("+79884972666");// true
		boolean res9 = contactService.PhoneInContact("89884972666");// true
		boolean res10 = contactService.PhoneInContact("8(927)524-30-09");// true - full equal
		boolean res11 = contactService.PhoneInContact("89275243009");// true
		boolean res12 = contactService.PhoneInContact("+79275243009");// true
		boolean res13 = contactService.PhoneInContact("+491778826937");// false
		boolean res14 = contactService.PhoneInContact("+499275243009");// true
		boolean res15 = contactService.PhoneInContact("CITILINK");// true - full equal

		Assert.assertEquals(res1, false);
		Assert.assertEquals(res2, false);
		Assert.assertEquals(res3, true);
		Assert.assertEquals(res4, true);
		Assert.assertEquals(res5, false);
		Assert.assertEquals(res6, true);
		Assert.assertEquals(res7, true);
		Assert.assertEquals(res8, true);
		Assert.assertEquals(res9, true);
		Assert.assertEquals(res10, true);
		Assert.assertEquals(res11, true);
		Assert.assertEquals(res12, true);
		Assert.assertEquals(res13, false);
		Assert.assertEquals(res14, true);
		Assert.assertEquals(res15, true);
	}
}