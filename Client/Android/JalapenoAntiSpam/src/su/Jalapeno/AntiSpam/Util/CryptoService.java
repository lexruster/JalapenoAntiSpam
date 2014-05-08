package su.Jalapeno.AntiSpam.Util;

import java.math.BigInteger;
import java.security.MessageDigest;

public class CryptoService {

	public CryptoService() {
	}

	public static String GetHash(String text) {
		String hash = "MD5";
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(text.getBytes(Constants.DEFAULT_ENCODING), 0, text.length());
			hash = new BigInteger(1, m.digest()).toString(16);

			while (hash.length() < 32) {
				hash = "0" + hash;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return hash;
	}
}