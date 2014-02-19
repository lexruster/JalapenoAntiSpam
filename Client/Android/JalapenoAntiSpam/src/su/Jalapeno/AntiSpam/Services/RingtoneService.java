package su.Jalapeno.AntiSpam.Services;

import android.content.Context;

import com.google.inject.Inject;

import su.Jalapeno.AntiSpam.Util.DebugMessage;

public class RingtoneService {
	private SettingsService _settingsService;
	private Context _context;

	@Inject
	public RingtoneService(Context context, SettingsService settingsService) {
		_context = context;
		_settingsService = settingsService;
	}

	public void ContactRingtone() {

	}

	public void EmulateIncomeSms() {
		DebugMessage.Debug(_context, "Def ringtone");
	}
}
