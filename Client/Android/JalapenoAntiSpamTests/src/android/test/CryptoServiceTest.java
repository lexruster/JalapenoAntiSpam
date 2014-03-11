package android.test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import junit.framework.Assert;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.CryptoService;
import su.Jalapeno.AntiSpam.Util.PrivateKeyInfo;
import su.Jalapeno.AntiSpam.Util.PublicKeyInfo;

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
				"—уществуют две основных трактовки пон€ти€ текст: имманентна€ (расширенна€, философски нагруженна€) и репрезентативна€ (более частна€). »мманентный подход подразумевает отношение к тексту как к автономной реальности, нацеленность на вы€вление его внутренней структуры. –епрезентативный Ч рассмотрение текста как особой формы представлени€ знаний о внешней тексту действительности.",
				"пайми дело не в тебе а ва мне!" };
		String[] hashes = new String[] { "d812bc065165d42acc768c6526ae2799", "20d149c23fea2b17836989bfd3b2fc8d",
				"8189af1c0046a830efc4246bf6c13084", "efa2f97248e0476a48bed0efeb2a92b2", "ab050008807a22957dc5a8bcd4e7aa78",
				"800929f0445713f075cc166fefa63f1f" };

		for (int i = 0; i < texts.length; i++) {
			String hash = _cryptoService.GetHash(texts[i]);
			Assert.assertEquals(hash, hashes[i]);
		}
	}

	public void testParsePublicKey() {
		String key = "<RSAKeyValue><Modulus>0fYCDz998w7i7Q6uENUinup+qCX1WrEtBaWZv4vhvr74q75yt5kezX/f9la9fP2uq9VQaRhj4c/RMjjxzRSi46l4/Pr5p5qv/7bRI4IeuzdUibZqZkUNbjpHp4JF5380OXMsSQRu5MjVUZ3fJaCwAwC+OmeovjX4EowonerGlxgscx798oxnU3DK3L7eMWykL/hIGgZ5A42VSIIY23/Ma9i/co8broR7MU58AFgRT2HStzOZjF6+70CiTpMBqAqxJGULVYNJImxl+UFWwFkckNUj/fYlng51QeNv07Wj6YxHt5Bwez1Hp4bE/iulmAoTjefn606bayxVV9LBy3tjiw==</Modulus><Exponent>AQAB</Exponent></RSAKeyValue>";

		PublicKeyInfo getPublicKeyInfo = CryptoService.GetPublicKeyInfo(key);
		Assert.assertTrue(getPublicKeyInfo.Modulus
				.equals("0fYCDz998w7i7Q6uENUinup+qCX1WrEtBaWZv4vhvr74q75yt5kezX/f9la9fP2uq9VQaRhj4c/RMjjxzRSi46l4/Pr5p5qv/7bRI4IeuzdUibZqZkUNbjpHp4JF5380OXMsSQRu5MjVUZ3fJaCwAwC+OmeovjX4EowonerGlxgscx798oxnU3DK3L7eMWykL/hIGgZ5A42VSIIY23/Ma9i/co8broR7MU58AFgRT2HStzOZjF6+70CiTpMBqAqxJGULVYNJImxl+UFWwFkckNUj/fYlng51QeNv07Wj6YxHt5Bwez1Hp4bE/iulmAoTjefn606bayxVV9LBy3tjiw=="));
		Assert.assertTrue(getPublicKeyInfo.Exponent.equals("AQAB"));
	}

	public void testEncrypt() {
		String key = "<RSAKeyValue><Modulus>uwGVg3bEtkgfqhOB+yPuP5P2/w868ihF8Td4WT/njyGtYP0qY/pz9HjWls0GFC36MjAZgy7p0X7A3g1CmvrSMLeXEml9ITrsGQTl9bvQNj8oUlqepd9O/PjK/+IqZMk+NimHr1pFymIU8k8BZyA3bDzoxg3bzZkNnhWdACBXfM3I8frFLnWsEniDXgBSeftDMyIafCNXN3DxDqxxT7FMl1QX+IdwozxIT+n2c+Ke1MVg+6QGgv0VBmd8G2vOyDARtkEF28SvtaD7ruhhhOE1KJVvHJvquEU9DcyfHhfPumIyO5gDdyGGQZMW7pUBVfMhM5VDoUY9Bn3e/C/t6JtfUw==</Modulus><Exponent>AQAB</Exponent></RSAKeyValue>";

		String request = "{\"ClientId\":\"5609fd8a-3938-4746-8764-45c517568c76\",\"Token\":\"\"}";
		PublicKeyInfo getPublicKeyInfo = CryptoService.GetPublicKeyInfo(key);

		String encryptRsaToBase64 = CryptoService.EncryptRsa(request, getPublicKeyInfo);
		Assert.assertTrue(encryptRsaToBase64.length() > 0);
	}

	public void testDecrypt() {
		String pbkey = "<RSAKeyValue><Modulus>uwGVg3bEtkgfqhOB+yPuP5P2/w868ihF8Td4WT/njyGtYP0qY/pz9HjWls0GFC36MjAZgy7p0X7A3g1CmvrSMLeXEml9ITrsGQTl9bvQNj8oUlqepd9O/PjK/+IqZMk+NimHr1pFymIU8k8BZyA3bDzoxg3bzZkNnhWdACBXfM3I8frFLnWsEniDXgBSeftDMyIafCNXN3DxDqxxT7FMl1QX+IdwozxIT+n2c+Ke1MVg+6QGgv0VBmd8G2vOyDARtkEF28SvtaD7ruhhhOE1KJVvHJvquEU9DcyfHhfPumIyO5gDdyGGQZMW7pUBVfMhM5VDoUY9Bn3e/C/t6JtfUw==</Modulus><Exponent>AQAB</Exponent></RSAKeyValue>";
		String prkM = "uwGVg3bEtkgfqhOB+yPuP5P2/w868ihF8Td4WT/njyGtYP0qY/pz9HjWls0GFC36MjAZgy7p0X7A3g1CmvrSMLeXEml9ITrsGQTl9bvQNj8oUlqepd9O/PjK/+IqZMk+NimHr1pFymIU8k8BZyA3bDzoxg3bzZkNnhWdACBXfM3I8frFLnWsEniDXgBSeftDMyIafCNXN3DxDqxxT7FMl1QX+IdwozxIT+n2c+Ke1MVg+6QGgv0VBmd8G2vOyDARtkEF28SvtaD7ruhhhOE1KJVvHJvquEU9DcyfHhfPumIyO5gDdyGGQZMW7pUBVfMhM5VDoUY9Bn3e/C/t6JtfUw==";
		String prkD = "iXY6ny44nv0ZOX2peCALS3TKhOZ/H+32q79omcJdxlqtBbRS6rb5cWU56BPBOigp16Z/umxLt5EC2LbKlP12cBUj2YbtUfXsAuAU4bMy9A0s6IxwPeGOQGnpXnqzSu6a6llQwdcLoh7C4vKsR0/pLYkAWkVpoxG0btXYWHVtmfCwiPrwPPI3xzXKtCQIr7qvIL7KDvZIqEJR/XgrZYupy8S+qR794bJvGOLNf+v8sgh9BFljMqx4QmXi8UB+kG8+SFRNNyFmHmzOxJkT+qb03mrYDWi0GzFrTqGTLaB9x4A6SSrFNeR9iywmBwZJNjnb61cK1q6TS+su00sML3USQQ==";
		PrivateKeyInfo prkey = new PrivateKeyInfo(prkM, prkD);

		String request = "{\"ClientId\":\"5609fd8a-3938-4746-8764-45c517568c76\",\"Token\":\"\"}";
		PublicKeyInfo getPublicKeyInfo = CryptoService.GetPublicKeyInfo(pbkey);

		String encryptRsaToBase64 = CryptoService.EncryptRsa(request, getPublicKeyInfo);

		String encryptRsaToBase64Decr = CryptoService.DecryptRsa(encryptRsaToBase64, prkey);
		Assert.assertTrue(request.equals(encryptRsaToBase64Decr));
	}

	public void testDecrypt2() {

		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance(Constants.KEY_ALGORITM);

			kpg.initialize(2048);
			KeyPair kp = kpg.genKeyPair();
			PublicKey publicKey = kp.getPublic();
			PrivateKey privateKey = kp.getPrivate();

			String request = "{\"ClientId\":\"5609fd8a-3938-4746-8764-45c517568c76\",\"Token\":\"\"}";
			String encryptRsaToBase64 = CryptoService.EncryptRsa(request, publicKey);

			String encryptRsaToBase64Decr = CryptoService.DecryptRsa(encryptRsaToBase64, privateKey);
			Assert.assertTrue(request.equals(encryptRsaToBase64Decr));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
