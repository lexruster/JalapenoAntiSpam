package su.Jalapeno.AntiSpam.Util;

import java.io.UnsupportedEncodingException;

import android.util.Base64;

public class EncodingUtils {

	public static String ToBase64String(String input) {
		String base64 = "";
		try {
			base64 = Base64.encodeToString(input.getBytes(Constants.DEFAULT_ENCODING), Base64.NO_WRAP);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return base64;
	}

	public static String FromBase64String(String input) {
		String string = "";
		try {
			byte[] decode = Base64.decode(input, Base64.NO_WRAP);
			string = new String(decode, Constants.DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return string;
	}
}
