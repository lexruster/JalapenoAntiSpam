package su.Jalapeno.AntiSpam.Services;

import java.util.Date;
import java.util.UUID;

import su.Jalapeno.AntiSpam.Util.Config;
import su.Jalapeno.AntiSpam.Util.Constants;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.Inject;

public class ConfigService {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "SettingsService";
	public static final String APP_PREFERENCES = "jalapenoSettings";
	private Context _context;
	private int ContextMode = Context.MODE_PRIVATE | 0x4;

	@Inject
	public ConfigService(Context context) {
		_context = context;
	}

	public Config LoadSettings() {
		Config config = new Config();
		SharedPreferences jalapenoSettings = _context.getSharedPreferences(APP_PREFERENCES, ContextMode);
		if (!jalapenoSettings.contains(config.NAME_Enabled)) {
			SetDefault(new Config(), jalapenoSettings);
		}
		FillConfig(config, jalapenoSettings);

		return config;
	}

	private void FillConfig(Config config, SharedPreferences jalapenoSettings) {
		config.Enabled = jalapenoSettings.getBoolean(config.NAME_Enabled, config.DEFAULT_Enabled);
		config.UnlimitedAccess = jalapenoSettings.getBoolean(config.NAME_UnlimitedAccess, config.DEFAULT_UnlimitedAccess);
		config.ExpirationDate = new Date(jalapenoSettings.getLong(config.NAME_ExpirationDate, config.DEFAULT_ExpirationDate.getTime()));

		String clientId = jalapenoSettings.getString(config.NAME_ClientId, config.DEFAULT_ClientId);
		if (clientId.length() > 0) {
			config.ClientId = UUID.fromString(clientId);
		}

		config.ClientRegistered = jalapenoSettings.getBoolean(config.NAME_ClientRegistered, config.DEFAULT_ClientRegistered);
		config.DomainUrlPrimary = jalapenoSettings.getBoolean(config.NAME_DomainUrlPrimary, config.DEFAULT_DomainUrlPrimary);
	}

	public void SaveSettings(Config config) {
		SharedPreferences jalapenoSettings = _context.getSharedPreferences(APP_PREFERENCES, ContextMode);
		SharedPreferences.Editor editor = jalapenoSettings.edit();
		editor.putBoolean(config.NAME_Enabled, config.Enabled);
		editor.putBoolean(config.NAME_UnlimitedAccess, config.UnlimitedAccess);
		editor.putLong(config.NAME_ExpirationDate, config.ExpirationDate.getTime());

		if (config.ClientId != null) {
			editor.putString(config.NAME_ClientId, config.ClientId.toString());
		}
		editor.putBoolean(config.NAME_ClientRegistered, config.ClientRegistered);

		editor.putBoolean(config.NAME_DomainUrlPrimary, config.DomainUrlPrimary);

		editor.commit();
	}

	private void SetDefault(Config config, SharedPreferences jalapenoSettings) {
		SharedPreferences.Editor editor = jalapenoSettings.edit();
		editor.putBoolean(config.NAME_Enabled, config.DEFAULT_Enabled);
		editor.putBoolean(config.NAME_UnlimitedAccess, config.DEFAULT_UnlimitedAccess);
		editor.putString(config.NAME_ClientId, config.DEFAULT_ClientId);
		editor.putBoolean(config.NAME_ClientRegistered, config.DEFAULT_ClientRegistered);
		editor.putBoolean(config.NAME_DomainUrlPrimary, config.DEFAULT_DomainUrlPrimary);
		editor.putLong(config.NAME_ExpirationDate, config.DEFAULT_ExpirationDate.getTime());

		editor.commit();
	}
}
