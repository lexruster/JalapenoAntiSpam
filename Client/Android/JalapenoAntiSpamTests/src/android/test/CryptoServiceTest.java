package android.test;

import junit.framework.Assert;
import su.Jalapeno.AntiSpam.Util.CryptoService;

public class CryptoServiceTest extends AndroidTestCase {
	
	CryptoService _cryptoService; 
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		_cryptoService = new CryptoService();
	}

	public void testHashes() {

		String[] texts=new String[] {
				"dsfsf989f sfjksdf 9sdf89sdf skdfj;klsdjf 9fsdf",
				"sdfgsdfsdf",
				"mfdsfgdgkiujgfkldsjklsdjfksdkjfksldjfkljwkj4rksdjfklj3kl4rjwekfljralkj34rlksjdflksajw34ikljskflsji34jwlskfjsw34klsjdfkljrlk3j4rlksjrkljw3",
				"mfdsfgdgkiujgfkldsjklsdjfksdkjfksldj5345834548353495834-5893485-9324859034850jdflksajw34ikljskflsji34jwlskfjsw34klsjdfkljrlk3j4rlksjrkljw3",
				"—уществуют две основных трактовки пон€ти€ текст: имманентна€ (расширенна€, философски нагруженна€) и репрезентативна€ (более частна€). »мманентный подход подразумевает отношение к тексту как к автономной реальности, нацеленность на вы€вление его внутренней структуры. –епрезентативный Ч рассмотрение текста как особой формы представлени€ знаний о внешней тексту действительности.",
				"пайми дело не в тебе а ва мне!"
		};
		String[] hashes=new String[] {
				"d812bc065165d42acc768c6526ae2799",
				"20d149c23fea2b17836989bfd3b2fc8d",
				"8189af1c0046a830efc4246bf6c13084",
				"efa2f97248e0476a48bed0efeb2a92b2",
				"ab050008807a22957dc5a8bcd4e7aa78",
				"800929f0445713f075cc166fefa63f1f"
		};
		
		for(int i =0; i < texts.length;i++)
		{
			String hash= _cryptoService.GetHash(texts[i]);
			Assert.assertEquals(hash, hashes[i]);
		}
	}
}
