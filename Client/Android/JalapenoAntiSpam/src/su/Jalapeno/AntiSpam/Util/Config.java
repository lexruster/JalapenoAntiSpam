package su.Jalapeno.AntiSpam.Util;

import java.util.UUID;

public class Config {

	public String NAME_Enabled = "Enabled";
	public boolean Enabled;
	public boolean DEFAULT_Enabled = false;

	public String NAME_ClientRegistered = "ClientRegistered";
	public boolean ClientRegistered;
	public boolean DEFAULT_ClientRegistered = false;

	public String NAME_ClientId = "ClientId";
	public UUID ClientId;
	public String DEFAULT_ClientId = "";

	public String NAME_DomainUrlPrimary = "DomainUrlPrimary";
	public boolean DomainUrlPrimary;
	public boolean DEFAULT_DomainUrlPrimary = true;
}
