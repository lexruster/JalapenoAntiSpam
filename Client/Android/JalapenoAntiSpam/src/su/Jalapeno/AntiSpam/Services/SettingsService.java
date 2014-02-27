package su.Jalapeno.AntiSpam.Services;

import java.util.UUID;

import com.google.inject.Inject;

import su.Jalapeno.AntiSpam.Util.Config;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Kseny on 30.12.13.
 */
public class SettingsService {
	public static final String APP_PREFERENCES = "jalapenoSettings";
	private Context _context;

	@Inject
	public SettingsService(Context context) {
		_context = context;
	}

	public Config LoadSettings() {
		Config config = new Config();
		SharedPreferences jalapenoSettings = _context.getSharedPreferences(
				APP_PREFERENCES, Context.MODE_PRIVATE);
		if (!jalapenoSettings.contains(config.EnabledString)) {
			SetDefault(new Config(), jalapenoSettings);
		}
		FillConfig(config, jalapenoSettings);

		return config;
	}

	private void FillConfig(Config config, SharedPreferences jalapenoSettings) {
		config.Enabled = jalapenoSettings.getBoolean(config.EnabledString,
				config.EnabledDefault);
		config.PublicKey = jalapenoSettings.getString(config.PublicKeyString,
				config.PublicKeyDefault);
		config.UnknownSound = jalapenoSettings.getString(
				config.UnknownSoundString, config.UnknownSoundDefault);

		String clientId = jalapenoSettings.getString(config.ClientIdString,
				config.ClientIdDefault);
		if (clientId.length() > 0) {
			config.ClientId = UUID.fromString(clientId);
		}

		config.ClientRegistered = jalapenoSettings.getBoolean(
				config.ClientRegisteredString, config.ClientRegisteredDefault);
	}

	public void SaveSettings(Config config) {
		SharedPreferences jalapenoSettings = _context.getSharedPreferences(
				APP_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = jalapenoSettings.edit();
		editor.putBoolean(config.EnabledString, config.Enabled);
		editor.putString(config.PublicKeyString, config.PublicKey);
		editor.putString(config.UnknownSoundString, config.UnknownSound);

		if (config.ClientId != null) {
			editor.putString(config.ClientIdString, config.ClientId.toString());
		}
		editor.putBoolean(config.ClientRegisteredString,
				config.ClientRegistered);

		editor.commit();
	}

	private void SetDefault(Config config, SharedPreferences jalapenoSettings) {
		SharedPreferences.Editor editor = jalapenoSettings.edit();
		editor.putBoolean(config.EnabledString, config.EnabledDefault);
		editor.putString(config.PublicKeyString, config.PublicKeyDefault);
		editor.putString(config.UnknownSoundString, config.UnknownSoundDefault);

		editor.putString(config.ClientIdString, config.ClientIdDefault);
		editor.putBoolean(config.ClientRegisteredString,
				config.ClientRegisteredDefault);

		editor.commit();
	}
}
