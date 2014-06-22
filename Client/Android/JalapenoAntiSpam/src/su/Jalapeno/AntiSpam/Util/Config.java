package su.Jalapeno.AntiSpam.Util;

import java.util.Date;
import java.util.UUID;

import su.Jalapeno.AntiSpam.Services.WebService.WebConstants;

public class Config {

	public String EnabledString = "Enabled";
	public boolean Enabled;
	public boolean EnabledDefault = false;

	public String ClientRegisteredString = "ClientRegistered";
	public boolean ClientRegistered;
	public boolean ClientRegisteredDefault = false;

	public String UnlimitedAccessString = "UnlimitedAccess";
	public boolean UnlimitedAccess;
	public boolean UnlimitedAccessDefault = false;

	public String ClientIdString = "ClientId";
	public UUID ClientId;
	public String ClientIdDefault = "";

	public String DomainUrlPrimaryString = "DomainUrlPrimary";
	public boolean DomainUrlPrimary;
	public boolean DomainUrlPrimaryDefault = true;

	public String ExpirationDateString = "ExpirationDate";
	public Date ExpirationDate;
	public Date ExpirationDateDefault = DateUtil.addDays(new Date(), 30);

	public String GetDomain() {
		if (DomainUrlPrimary) {
			return WebConstants.DOMAIN_URL_PRIMARY;
		} else {
			return WebConstants.DOMAIN_URL_SECONDARY;
		}
	}

	
}
