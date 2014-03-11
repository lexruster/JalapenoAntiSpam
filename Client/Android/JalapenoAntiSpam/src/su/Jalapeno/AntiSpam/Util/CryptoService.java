package su.Jalapeno.AntiSpam.Util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class CryptoService {

	public CryptoService() {
	}

	protected static PublicKey GetPublicKey(String key) {
		PublicKey pubKey = null;
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = (Document) builder.build(key);
			Element rootNode = document.getRootElement();
			Element modulusElem = rootNode.getChild("Modulus");
			Element exponentElem = rootNode.getChild("Exponent");

			byte[] expBytes = EncodingUtils.FromBase64(exponentElem.getText()
					.trim());
			byte[] modBytes = EncodingUtils.FromBase64(modulusElem.getText()
					.trim());

			BigInteger modules = new BigInteger(1, modBytes);
			BigInteger exponent = new BigInteger(1, expBytes);

			KeyFactory factory = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(modules, exponent);

			pubKey = factory.generatePublic(pubSpec);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return pubKey;
	}

	public static String EncryptRsaToBase64(String input, String key) {
		String base64String = "";
		PublicKey pubKey = GetPublicKey(key);
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] cipherData = cipher.doFinal(input
					.getBytes(Constants.DEFAULT_ENCODING));
			base64String = EncodingUtils.ToBase64String(cipherData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return base64String;
	}

	public static String GetHash(String text) {
		String hash = "MD5";
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(text.getBytes("UTF8"), 0, text.length());
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
