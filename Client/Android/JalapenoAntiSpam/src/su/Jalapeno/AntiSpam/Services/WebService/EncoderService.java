package su.Jalapeno.AntiSpam.Services.WebService;

import su.Jalapeno.AntiSpam.Util.EncodingUtils;

import com.google.inject.Inject;

public class EncoderService {

	@Inject
	public EncoderService() {
	}

	public String Encode(String value) {
		String result = "";
			result = EncodingUtils.ToBase64String(value);

		return result;
	}

	public String Decode(String value) {
		String result = "";
		result = EncodingUtils.FromBase64String(value);

		return result;
	}
}
