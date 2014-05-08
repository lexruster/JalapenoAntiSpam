package su.Jalapeno.AntiSpam.Util;

import java.util.UUID;

import su.Jalapeno.AntiSpam.Services.WebService.WebConstants;

public class Config {

	public String EnabledString = "Enabled";
	public boolean Enabled;
	public boolean EnabledDefault = false;
	
	public String UnknownSoundString = "UnknownSound";
	public String UnknownSound;
	public String UnknownSoundDefault = "";

	public String ClientIdString = "ClientId";
	public UUID ClientId;
	public String ClientIdDefault = "";

	public String ClientRegisteredString = "ClientRegistered";
	public boolean ClientRegistered;
	public boolean ClientRegisteredDefault = false;
	
	public String DomainUrlPrimaryString = "DomainUrlPrimary";
	public boolean DomainUrlPrimary;
	public boolean DomainUrlPrimaryDefault = true;
	
	
	public String GetDomain() {
		if (DomainUrlPrimary) {
			return WebConstants.DOMAIN_URL_PRIMARY;
		} else {
			return WebConstants.DOMAIN_URL_SECONDARY;
		}
	}
}
