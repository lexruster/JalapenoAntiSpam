package su.Jalapeno.AntiSpam.Services.WebService;

import com.google.inject.Inject;

import su.Jalapeno.AntiSpam.Services.SettingsService;
import su.Jalapeno.AntiSpam.Util.EncodingUtils;

public class EncoderService {
	
	private SettingsService _settingsService;
	private Boolean _enableCrypto=false; 

	@Inject
	public EncoderService(SettingsService settingsService)
	{
		_settingsService = settingsService;
	}
	
	public String Encode(String value)
	{
		String result="";
		if(_enableCrypto)
		{
			
		}
		else
		{
			result=EncodingUtils.ToBase64String(value);
		}
		
		return result;
	}
	
	
	public String Decode(String value)
	{
		String result="";
		if(_enableCrypto)
		{
			
		}
		else
		{
			result=EncodingUtils.FromBase64String(value);
		}
		
		return result;
	}

}
