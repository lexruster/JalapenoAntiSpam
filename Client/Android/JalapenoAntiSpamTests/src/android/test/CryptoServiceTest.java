package android.test;

import junit.framework.Assert;
import su.Jalapeno.AntiSpam.Util.CryptoService;
import android.annotation.SuppressLint;

@SuppressLint("TrulyRandom")
public class CryptoServiceTest extends AndroidTestCase {

	CryptoService _cryptoService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		_cryptoService = new CryptoService();
	}

	public void testHashes() {

		String[] texts = new String[] {
				"dsfsf989f sfjksdf 9sdf89sdf skdfj;klsdjf 9fsdf",
				"sdfgsdfsdf",
				"mfdsfgdgkiujgfkldsjklsdjfksdkjfksldjfkljwkj4rksdjfklj3kl4rjwekfljralkj34rlksjdflksajw34ikljskflsji34jwlskfjsw34klsjdfkljrlk3j4rlksjrkljw3",
				"mfdsfgdgkiujgfkldsjklsdjfksdkjfksldj5345834548353495834-5893485-9324859034850jdflksajw34ikljskflsji34jwlskfjsw34klsjdfkljrlk3j4rlksjrkljw3",
				"���������� ��� �������� ��������� ������� �����: ����������� (�����������, ���������� �����������) � ���������������� (����� �������). ����������� ������ ������������� ��������� � ������ ��� � ���������� ����������, ������������ �� ��������� ��� ���������� ���������. ���������������� � ������������ ������ ��� ������ ����� ������������� ������ � ������� ������ ����������������.",
				"����� ���� �� � ���� � �� ���!" };
		String[] hashes = new String[] { "d812bc065165d42acc768c6526ae2799",
				"20d149c23fea2b17836989bfd3b2fc8d",
				"8189af1c0046a830efc4246bf6c13084",
				"efa2f97248e0476a48bed0efeb2a92b2",
				"ab050008807a22957dc5a8bcd4e7aa78",
				"800929f0445713f075cc166fefa63f1f" };

		for (int i = 0; i < texts.length; i++) {
			String hash = CryptoService.GetHash(texts[i]);
			Assert.assertEquals(hash, hashes[i]);
		}
	}

	public void testEncrypt() {
		String test = "dsf 456 fdg 45";
		String encrypted = _cryptoService.Encrypt(test);
		String decrypted = _cryptoService.Decrypt(encrypted);

		Assert.assertEquals(decrypted, test);
	}
	
	public void testDecrLicEncrypt() {
		String License = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA31loC7u9orA2NwKKnZiQp3Qmq/kFRrP9oPSnd3Y8X6aYsmFbov1+P632Tlme1j7yXt5mX34JtAeTVV/dkpE6pp3dJwaoIPtUAWncn10I6gaUVKuBK/mzgu5MvdWQnCSpMJyBtQoKL+0dMJ9i3rmJz9OYII8lwOnOkbxs4SDVspxaVGtsIaFhFWYNp7mTQR7uWIddLHMHjmPSYEzexg2mAuoXn2rr3ze7daRsJfeET5/5/5/2F+0hBjZyieJKQZsCBKRNxeJa0BtaSk88JwVD8XnTuDIUp858fAcI/m8tb7C7ZQc+zCzGc/wahN4Vfa/GEtM1LRfU97cvYJFR4ielRQIDAQAB";
		String EncrLicense ="XMj5ozZukFaHtFe5tkpt0gmJqkoMLw3ZzyX5YMyTVh2ouwONi62LpivYx0NJBcPJ9ZPqlG961Wql08HlbqLOWdjkp2yuw3FOeXF+7n4Zv+/o34+oB6BvEXEP/6/X8UanlCeXHIZ7JLxTI+6zdmrnRiso0AZZsZOuixofUZuCDzPNX2E3xUT5/rKnSiVnVuDJq/eoFc3okFqclwT+Migiujn5RL4Yq+84g9Jfw3XLm04bUyyZebq+A8i7wQlcrBGW9niKVISfhkXQvcr3ak1yrt1Y3KYM9/ogxxYzsp/zfYGB1qx4xZMX5CtkjbFU4TFDlcIklSY7j0DYueUXyMh8rJ6Z5Y3NhhN1Lntl55T+k9/FGPShKaUQLNskv35Bl0ANxAgCLLlvLoCg2+rBezVzKGxy/QxajKWvAkVukTbSMyi+kvkfOj5NKt7+CsIUeP5jBtYJWZpuVUhwiwrizBs/N6LoCmtTqx/C48EiUZojhlyohEj8YlgBuqhp0G4WcpmyEe0MUIN3DxddIz1llX4UYw==";
		
		String decrypted = _cryptoService.Decrypt(EncrLicense);

		Assert.assertEquals(decrypted, License);
	}

	/*
	 * public void testParsePublicKey() { String key =
	 * "<RSAKeyValue><Modulus>0fYCDz998w7i7Q6uENUinup+qCX1WrEtBaWZv4vhvr74q75yt5kezX/f9la9fP2uq9VQaRhj4c/RMjjxzRSi46l4/Pr5p5qv/7bRI4IeuzdUibZqZkUNbjpHp4JF5380OXMsSQRu5MjVUZ3fJaCwAwC+OmeovjX4EowonerGlxgscx798oxnU3DK3L7eMWykL/hIGgZ5A42VSIIY23/Ma9i/co8broR7MU58AFgRT2HStzOZjF6+70CiTpMBqAqxJGULVYNJImxl+UFWwFkckNUj/fYlng51QeNv07Wj6YxHt5Bwez1Hp4bE/iulmAoTjefn606bayxVV9LBy3tjiw==</Modulus><Exponent>AQAB</Exponent></RSAKeyValue>"
	 * ;
	 * 
	 * PublicKeyInfo getPublicKeyInfo = CryptoService.GetPublicKeyInfo(key);
	 * Assert.assertTrue(getPublicKeyInfo.Modulus .equals(
	 * "0fYCDz998w7i7Q6uENUinup+qCX1WrEtBaWZv4vhvr74q75yt5kezX/f9la9fP2uq9VQaRhj4c/RMjjxzRSi46l4/Pr5p5qv/7bRI4IeuzdUibZqZkUNbjpHp4JF5380OXMsSQRu5MjVUZ3fJaCwAwC+OmeovjX4EowonerGlxgscx798oxnU3DK3L7eMWykL/hIGgZ5A42VSIIY23/Ma9i/co8broR7MU58AFgRT2HStzOZjF6+70CiTpMBqAqxJGULVYNJImxl+UFWwFkckNUj/fYlng51QeNv07Wj6YxHt5Bwez1Hp4bE/iulmAoTjefn606bayxVV9LBy3tjiw=="
	 * )); Assert.assertTrue(getPublicKeyInfo.Exponent.equals("AQAB")); }
	 * 
	 * public void testEncrypt() { String key =
	 * "<RSAKeyValue><Modulus>uwGVg3bEtkgfqhOB+yPuP5P2/w868ihF8Td4WT/njyGtYP0qY/pz9HjWls0GFC36MjAZgy7p0X7A3g1CmvrSMLeXEml9ITrsGQTl9bvQNj8oUlqepd9O/PjK/+IqZMk+NimHr1pFymIU8k8BZyA3bDzoxg3bzZkNnhWdACBXfM3I8frFLnWsEniDXgBSeftDMyIafCNXN3DxDqxxT7FMl1QX+IdwozxIT+n2c+Ke1MVg+6QGgv0VBmd8G2vOyDARtkEF28SvtaD7ruhhhOE1KJVvHJvquEU9DcyfHhfPumIyO5gDdyGGQZMW7pUBVfMhM5VDoUY9Bn3e/C/t6JtfUw==</Modulus><Exponent>AQAB</Exponent></RSAKeyValue>"
	 * ;
	 * 
	 * String request =
	 * "{\"ClientId\":\"5609fd8a-3938-4746-8764-45c517568c76\",\"Token\":\"\"}";
	 * PublicKeyInfo getPublicKeyInfo = CryptoService.GetPublicKeyInfo(key);
	 * 
	 * String encryptRsaToBase64 = CryptoService.EncryptRsa(request,
	 * getPublicKeyInfo); Assert.assertTrue(encryptRsaToBase64.length() > 0); }
	 * 
	 * public void testDecrypt() { String pbkey =
	 * "<RSAKeyValue><Modulus>uwGVg3bEtkgfqhOB+yPuP5P2/w868ihF8Td4WT/njyGtYP0qY/pz9HjWls0GFC36MjAZgy7p0X7A3g1CmvrSMLeXEml9ITrsGQTl9bvQNj8oUlqepd9O/PjK/+IqZMk+NimHr1pFymIU8k8BZyA3bDzoxg3bzZkNnhWdACBXfM3I8frFLnWsEniDXgBSeftDMyIafCNXN3DxDqxxT7FMl1QX+IdwozxIT+n2c+Ke1MVg+6QGgv0VBmd8G2vOyDARtkEF28SvtaD7ruhhhOE1KJVvHJvquEU9DcyfHhfPumIyO5gDdyGGQZMW7pUBVfMhM5VDoUY9Bn3e/C/t6JtfUw==</Modulus><Exponent>AQAB</Exponent></RSAKeyValue>"
	 * ; String prkM =
	 * "uwGVg3bEtkgfqhOB+yPuP5P2/w868ihF8Td4WT/njyGtYP0qY/pz9HjWls0GFC36MjAZgy7p0X7A3g1CmvrSMLeXEml9ITrsGQTl9bvQNj8oUlqepd9O/PjK/+IqZMk+NimHr1pFymIU8k8BZyA3bDzoxg3bzZkNnhWdACBXfM3I8frFLnWsEniDXgBSeftDMyIafCNXN3DxDqxxT7FMl1QX+IdwozxIT+n2c+Ke1MVg+6QGgv0VBmd8G2vOyDARtkEF28SvtaD7ruhhhOE1KJVvHJvquEU9DcyfHhfPumIyO5gDdyGGQZMW7pUBVfMhM5VDoUY9Bn3e/C/t6JtfUw=="
	 * ; String prkD =
	 * "iXY6ny44nv0ZOX2peCALS3TKhOZ/H+32q79omcJdxlqtBbRS6rb5cWU56BPBOigp16Z/umxLt5EC2LbKlP12cBUj2YbtUfXsAuAU4bMy9A0s6IxwPeGOQGnpXnqzSu6a6llQwdcLoh7C4vKsR0/pLYkAWkVpoxG0btXYWHVtmfCwiPrwPPI3xzXKtCQIr7qvIL7KDvZIqEJR/XgrZYupy8S+qR794bJvGOLNf+v8sgh9BFljMqx4QmXi8UB+kG8+SFRNNyFmHmzOxJkT+qb03mrYDWi0GzFrTqGTLaB9x4A6SSrFNeR9iywmBwZJNjnb61cK1q6TS+su00sML3USQQ=="
	 * ; PrivateKeyInfo prkey = new PrivateKeyInfo(prkM, prkD);
	 * 
	 * String request =
	 * "{\"ClientId\":\"5609fd8a-3938-4746-8764-45c517568c76\",\"Token\":\"\"}";
	 * PublicKeyInfo getPublicKeyInfo = CryptoService.GetPublicKeyInfo(pbkey);
	 * 
	 * String encryptRsaToBase64 = CryptoService.EncryptRsa(request,
	 * getPublicKeyInfo);
	 * 
	 * String encryptRsaToBase64Decr =
	 * CryptoService.DecryptRsa(encryptRsaToBase64, prkey);
	 * Assert.assertTrue(request.equals(encryptRsaToBase64Decr)); }
	 * 
	 * @SuppressLint("TrulyRandom") public void testDecrypt2() {
	 * 
	 * KeyPairGenerator kpg; try { kpg =
	 * KeyPairGenerator.getInstance(Constants.KEY_ALGORITM);
	 * 
	 * kpg.initialize(2048); KeyPair kp = kpg.genKeyPair(); PublicKey publicKey
	 * = kp.getPublic(); PrivateKey privateKey = kp.getPrivate();
	 * 
	 * String request =
	 * "{\"ClientId\":\"5609fd8a-3938-4746-8764-45c517568c76\",\"Token\":\"\"}";
	 * String encryptRsaToBase64 = CryptoService.EncryptRsa(request, publicKey);
	 * 
	 * String encryptRsaToBase64Decr =
	 * CryptoService.DecryptRsa(encryptRsaToBase64, privateKey);
	 * Assert.assertTrue(request.equals(encryptRsaToBase64Decr));
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * }
	 */
}
