package su.Jalapeno.AntiSpam.Services;

import java.util.UUID;

import su.Jalapeno.AntiSpam.Services.WebService.WebConstants;
import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;

import com.google.inject.Inject;

public class SettingsService {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "SettingsService";
	private ConfigService _configService;

	@Inject
	public SettingsService(ConfigService configService) {
		_configService = configService;
	}

	private void SaveSettings(Config config) {
		_configService.SaveSettings(config);
	}

	private Config LoadSettings() {
		return _configService.LoadSettings();
	}

	public UUID GetClientId() {
		Config config = LoadSettings();
		return config.ClientId;
	}

	public String GetDomain() {
		Config config = LoadSettings();
		return GetDomain(config);
	}

	private String GetDomain(Config config) {
		if (config.DomainUrlPrimary) {
			return WebConstants.DOMAIN_URL_PRIMARY;
		} else {
			return WebConstants.DOMAIN_URL_SECONDARY;
		}
	}

	public boolean AntispamEnabled() {
		Config config = LoadSettings();

		return config.Enabled && config.ClientRegistered;
	}

	public void ChangeDomain() {
		Config config = LoadSettings();
		config.DomainUrlPrimary = !config.DomainUrlPrimary;
		SaveSettings(config);
	}

	public boolean ClientIsRegistered() {
		Config config = LoadSettings();
		return config.ClientRegistered;
	}

	public void RegisterClient(UUID uuid) {
		Config config = LoadSettings();
		config.ClientId = uuid;
		RegisterClient(config);
		SaveSettings(config);
	}

	public void RegisterClient() {
		Config config = LoadSettings();
		SaveSettings(config);
	}

	private void RegisterClient(Config config) {
		config.ClientRegistered = true;
		config.Enabled = true;
	}

	public void PrepareClientForRegister(UUID uuid) {
		Config config = LoadSettings();
		config.ClientId = uuid;
		SaveSettings(config);
	}

	public void DropRegistration() {
		Config config = LoadSettings();
		DropRegistration(config);
		SaveSettings(config);
	}

	public void HandleClientNotRegistered() {
		Config config = LoadSettings();
		DropRegistration(config);
		SaveSettings(config);
	}

	private void DropRegistration(Config config) {
		config.ClientRegistered = false;
		config.ClientId = null;
		config.Enabled = false;
	}

	public void SetAntispamEnabled(boolean enabled) {
		Config config = LoadSettings();
		config.Enabled = enabled;
		SaveSettings(config);
	}

	public boolean ToggleAntispamEnabled() {
		Config config = LoadSettings();
		config.Enabled = !config.Enabled;
		SaveSettings(config);
		return config.Enabled;
	}
}
