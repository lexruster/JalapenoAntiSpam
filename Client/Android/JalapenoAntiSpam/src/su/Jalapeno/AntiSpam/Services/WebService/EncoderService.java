package su.Jalapeno.AntiSpam.Services.WebService;

import com.google.inject.Inject;

import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.CryptoService;
import su.Jalapeno.AntiSpam.Util.EncodingUtils;
import su.Jalapeno.AntiSpam.Util.PublicKeyInfo;

public class EncoderService {

	private SettingsService _settingsService;
	
	@Inject
	public EncoderService(SettingsService settingsService) {
		_settingsService = settingsService;
	}

	public String Encode(String value) {
		PublicKeyInfo publicKey = _settingsService.GetPublicKey();
		String result = "";
		if (Constants.ENABLE_ENCRYPTION) {
			result = CryptoService.EncryptRsa(value, publicKey);
		} else {
			result = EncodingUtils.ToBase64String(value);
		}

		return result;
	}

	public String Decode(String value) {
		String result = "";
		/*
		 * if(_enableCrypto) {
		 * 
		 * } else { result=EncodingUtils.FromBase64String(value); }
		 */
		result = EncodingUtils.FromBase64String(value);

		return result;
	}

}
