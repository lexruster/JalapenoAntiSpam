package su.Jalapeno.AntiSpam.Util;

import java.util.UUID;

/**
 * Created by Alexander on 23.12.13.
 */
public class Config {

	public String EnabledString = "Enabled";
	public boolean Enabled;
	public boolean EnabledDefault = false;
	public String PublicKeyString = "PublicKey";
	public String PublicKey;
	public String PublicKeyDefault = "";
	public String UnknownSoundString = "UnknownSound";
	public String UnknownSound;
	public String UnknownSoundDefault = "";

	public String ClientIdString = "ClientId";
	public UUID ClientId;
	public String ClientIdDefault = "";

	public String ClientRegisteredString = "ClientRegistered";
	public boolean ClientRegistered;
	public boolean ClientRegisteredDefault = false;
}
