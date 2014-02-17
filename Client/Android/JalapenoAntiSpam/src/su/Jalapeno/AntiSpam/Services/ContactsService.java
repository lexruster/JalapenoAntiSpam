package su.Jalapeno.AntiSpam.Services;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

import com.google.inject.Inject;

public class ContactsService {

	Context _context;

	@Inject
	public ContactsService(Context context) {
		_context = context;
	}

	public boolean PhoneInContact(String phone) {
		if (FullPhoneInContact(phone)) {
			return true;
		}
		return false;
	}

	private boolean FullPhoneInContact(String phone) {
		boolean result = false;
		Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));

		String[] proj = new String[] { PhoneLookup.NUMBER };

		Cursor cursor = _context.getContentResolver().query(lookupUri, proj, null, null, null);

		while (cursor.moveToNext()) {
			result = true;
			break;
		}

		return result;
	}
}
