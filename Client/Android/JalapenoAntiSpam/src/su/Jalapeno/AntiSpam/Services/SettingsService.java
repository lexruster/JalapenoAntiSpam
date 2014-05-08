package su.Jalapeno.AntiSpam.Services;

import java.util.UUID;

import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.Inject;

public class SettingsService {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "SettingsService";
	public static final String APP_PREFERENCES = "jalapenoSettings";
	private Context _context;
	private int ContextMode = Context.MODE_PRIVATE | 0x4;

	@Inject
	public SettingsService(Context context) {
		_context = context;
	}

	public Config LoadSettings() {
		Config config = new Config();
		SharedPreferences jalapenoSettings = _context.getSharedPreferences(APP_PREFERENCES, ContextMode);
		if (!jalapenoSettings.contains(config.EnabledString)) {
			SetDefault(new Config(), jalapenoSettings);
		}
		FillConfig(config, jalapenoSettings);

		return config;
	}

	private void FillConfig(Config config, SharedPreferences jalapenoSettings) {
		config.Enabled = jalapenoSettings.getBoolean(config.EnabledString, config.EnabledDefault);
		config.UnknownSound = jalapenoSettings.getString(config.UnknownSoundString, config.UnknownSoundDefault);

		String clientId = jalapenoSettings.getString(config.ClientIdString, config.ClientIdDefault);
		if (clientId.length() > 0) {
			config.ClientId = UUID.fromString(clientId);
		}

		config.ClientRegistered = jalapenoSettings.getBoolean(config.ClientRegisteredString, config.ClientRegisteredDefault);

		config.DomainUrlPrimary = jalapenoSettings.getBoolean(config.DomainUrlPrimaryString, config.DomainUrlPrimaryDefault);
	}

	public void SaveSettings(Config config) {
		SharedPreferences jalapenoSettings = _context.getSharedPreferences(APP_PREFERENCES, ContextMode);
		SharedPreferences.Editor editor = jalapenoSettings.edit();
		editor.putBoolean(config.EnabledString, config.Enabled);
		editor.putString(config.UnknownSoundString, config.UnknownSound);

		if (config.ClientId != null) {
			editor.putString(config.ClientIdString, config.ClientId.toString());
		}
		editor.putBoolean(config.ClientRegisteredString, config.ClientRegistered);

		editor.putBoolean(config.DomainUrlPrimaryString, config.DomainUrlPrimary);

		editor.commit();
	}

	private void SetDefault(Config config, SharedPreferences jalapenoSettings) {
		SharedPreferences.Editor editor = jalapenoSettings.edit();
		editor.putBoolean(config.EnabledString, config.EnabledDefault);
		editor.putString(config.UnknownSoundString, config.UnknownSoundDefault);
		editor.putString(config.ClientIdString, config.ClientIdDefault);
		editor.putBoolean(config.ClientRegisteredString, config.ClientRegisteredDefault);

		editor.putBoolean(config.DomainUrlPrimaryString, config.DomainUrlPrimaryDefault);

		editor.commit();
	}

	public UUID GetClientId() {
		Config config = LoadSettings();

		return config.ClientId;
	}

	public String GetDomain() {
		Config config = LoadSettings();

		return config.GetDomain();
	}

	public boolean AntispamEnabled() {
		Config config = LoadSettings();

		return config.Enabled && config.ClientRegistered;
	}
}