package su.Jalapeno.AntiSpam.Services;

import java.text.Normalizer;
import java.util.ArrayList;

import com.google.inject.Inject;

import su.Jalapeno.AntiSpam.MyApplication;
import android.app.Application;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts.People;
import android.provider.Contacts.People.Phones;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

public class ContactsService {

	Context _context;
	//private PhoneNumberNormalizer _normalizer;

	@Inject
	public ContactsService(Context context, PhoneNumberNormalizer normalizer) {
		_context = context;
		//_normalizer = normalizer;
	}

	public boolean PhoneInContact(String phone) {
		if (FullPhoneInContact(phone)) {
			return true;
		}
		/*
		ArrayList<String> phones = GetAllPhonesFromContact();
		
		ArrayList<String> normalizePhones = _normalizer
				.GetNormalizePhones(phones);
*/
		/*
		if (PhoneListContainFullEqualPhone(normalizePhones, phone)) {
			return true;
		}
*/
	/*	if (!_normalizer.PhoneNumberInStandartForm(phone)) {
			return false;
		}
*/
	/*	String significantPart = _normalizer
				.GetSignificantPartOfStandartNumber(phone);
		ArrayList<String> nonStandartPhones = GetAllNonStandartPhones(phones);

		if (PhoneListContainPhone(nonStandartPhones, significantPart)) {
			return true;
		}
*/
		return false;
	}

	private boolean FullPhoneInContact(String phone) {
		boolean result = false;
		Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phone));

		String[] proj = new String[] { PhoneLookup.NUMBER };

		Cursor cursor = _context.getContentResolver().query(lookupUri, proj,
				null, null, null);

		while (cursor.moveToNext()) {
			result = true;
			break;
		}

		return result;
	}

	/*private ArrayList<String> GetAllPhonesFromContact() {
		ArrayList<String> phones = new ArrayList<String>();

		Cursor cursor = _context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				null, null, null, null);
		while (cursor.moveToNext()) {
			String name = cursor
					.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String phoneNumber = cursor
					.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			phones.add(phoneNumber);
		}
		cursor.close();

		return phones;
	}

	private ArrayList<String> GetAllNonStandartPhones(ArrayList<String> phones) {
		ArrayList<String> phonesStandart = new ArrayList<String>();

		for (String phone : phones) {
			if (!_normalizer.PhoneNumberInStandartForm(phone)) {
				phonesStandart.add(phone);
			}
		}

		return phonesStandart;
	}

	private boolean PhoneListContainPhone(ArrayList<String> phones,
			String phoneForFind) {
		for (String phone : phones) {
			if (phone.toLowerCase().contains(phoneForFind.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	private boolean PhoneListContainFullEqualPhone(ArrayList<String> phones,
			String phoneForFind) {
		for (String phone : phones) {
			if (phone.equalsIgnoreCase(phoneForFind)) {
				return true;
			}
		}

		return false;
	}*/
}
