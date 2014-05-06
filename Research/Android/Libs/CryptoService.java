package su.Jalapeno.AntiSpam.Util;

import android.annotation.SuppressLint;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

@SuppressLint("TrulyRandom")
public class CryptoService {

	public CryptoService() {
	}

	public static PublicKeyInfo GetPublicKeyInfo(String publicKeyXml) {
		PublicKeyInfo publicKeyInfo = null;

		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = (Document) builder.build(new StringReader(publicKeyXml));
			Element rootNode = document.getRootElement();
			Element modulusElem = rootNode.getChild("Modulus");
			Element exponentElem = rootNode.getChild("Exponent");

			publicKeyInfo = new PublicKeyInfo(modulusElem.getText(), exponentElem.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return publicKeyInfo;
	}

	protected static PublicKey GetPublicKey(PublicKeyInfo key) {
		PublicKey pubKey = null;

		try {
			byte[] expBytes = EncodingUtils.FromBase64(key.Exponent);
			byte[] modBytes = EncodingUtils.FromBase64(key.Modulus);

			BigInteger modules = new BigInteger(1, modBytes);
			BigInteger exponent = new BigInteger(1, expBytes);

			KeyFactory factory = KeyFactory.getInstance(Constants.KEY_ALGORITM);
			RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(modules, exponent);

			pubKey = factory.generatePublic(pubSpec);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return pubKey;
	}

	protected static PrivateKey GetPrivateKey(PrivateKeyInfo key) {
		PrivateKey pubKey = null;

		try {
			byte[] modBytes = EncodingUtils.FromBase64(key.Modulus);
			byte[] dBytes = EncodingUtils.FromBase64(key.D);

			BigInteger modules = new BigInteger(1, modBytes);
			BigInteger d = new BigInteger(1, dBytes);

			KeyFactory factory = KeyFactory.getInstance(Constants.KEY_ALGORITM);
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(modules, d);

			pubKey = factory.generatePrivate(keySpec);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return pubKey;
	}

	public static String EncryptRsa(String input, PublicKeyInfo key) {
		PublicKey pubKey = GetPublicKey(key);
		return EncryptRsa(input, pubKey);
	}

	@SuppressLint("TrulyRandom")
	public static String EncryptRsa(String request, PublicKey publicKey) {
		String base64String = "";
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(Constants.CRYPTO_ALGORITM);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryptedBytes = request.getBytes(Constants.DEFAULT_ENCODING);
			byte[] cipherData = cipher.doFinal(encryptedBytes);
			base64String = EncodingUtils.ToBase64String(cipherData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return base64String;
	}

	public static String DecryptRsa(String input, PrivateKeyInfo key) {
		PrivateKey privateKey = GetPrivateKey(key);
		return DecryptRsa(input, privateKey);
	}

	public static String DecryptRsa(String input, PrivateKey key) {
		String decrypted = "";
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(Constants.CRYPTO_ALGORITM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] encryptedBytes = EncodingUtils.FromBase64(input);
			byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
			decrypted = new String(decryptedBytes, Constants.DEFAULT_ENCODING);

			return decrypted;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return decrypted;
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
