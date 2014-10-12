package su.Jalapeno.AntiSpam.Util;

import java.math.BigInteger;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class CryptoService {
	static String algorithm = "DESede";
	private SecretKey key;

	public CryptoService() {
		try {
			String someS = BillingConstants.Hi + BillingConstants.Down + BillingConstants.Left
					+ BillingConstants.Lower;
			byte[] keyBytes = someS.getBytes(Constants.DEFAULT_ENCODING);
			DESedeKeySpec keySpec = new DESedeKeySpec(keyBytes);
			SecretKeyFactory factory = SecretKeyFactory.getInstance("DESede");
			key = factory.generateSecret(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String GetHash(String text) {
		String hash = "MD5";
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(text.getBytes(Constants.DEFAULT_ENCODING), 0,
					text.length());
			hash = new BigInteger(1, m.digest()).toString(16);

			while (hash.length() < 32) {
				hash = "0" + hash;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return hash;
	}

	public String Encrypt(String text) {
		String base64String = "";
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] inputBytes = text.getBytes(Constants.DEFAULT_ENCODING);
			byte[] doFinal = cipher.doFinal(inputBytes);
			base64String = EncodingUtils.ToBase64String(doFinal);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return base64String;
	}

	public String Decrypt(String encryptedText) {
		String decrypted = "";
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(algorithm);
			cipher.init(Cipher.DECRYPT_MODE, key);

			byte[] encryptedBytes = EncodingUtils.FromBase64(encryptedText);
			byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
			decrypted = new String(decryptedBytes, Constants.DEFAULT_ENCODING);

			return decrypted;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return decrypted;
	}
}