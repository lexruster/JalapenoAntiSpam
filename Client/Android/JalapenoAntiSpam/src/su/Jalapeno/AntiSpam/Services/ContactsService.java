package su.Jalapeno.AntiSpam.Services;

import android.app.Application;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.provider.ContactsContract.PhoneLookup;

/**
 * Created by Kseny on 30.12.13.
 */
public class ContactsService {
    public ContactsService()
    {

    }

    public boolean PhoneInContact(String phone)
    {
    	 Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
  //  	 lookupUri.
    	 
    	 
    	 /*Cursor c = getContentResolver().query(Contacts.CONTENT_URI, new String[] {Contacts._ID}, 
    			    Contacts.DISPLAY_NAME + " = 'Robert Smith'", null, null); 
    			                 
    			if(c.getCount() > 0) { 
  
    			} else { 
  
    			}
*/
    			
    			
    	/*Uri personUri = ContentUris.withAppendedId(People.CONTENT_URI, personId);
    	Uri phonesUri = Uri.withAppendedPath(personUri, People.Phones.CONTENT_DIRECTORY);
    	String[] proj = new String[] {Phones._ID, Phones.TYPE, Phones.NUMBER, Phones.LABEL}*/
    	
    	Cursor cursor = contentResolver.query(phonesUri, proj, null, null, null);
        return true;
    }
}
