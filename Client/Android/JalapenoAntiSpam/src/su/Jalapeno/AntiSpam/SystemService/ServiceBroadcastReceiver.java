package su.Jalapeno.AntiSpam.SystemService;

import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceBroadcastReceiver extends BroadcastReceiver {

	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "ServiceBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.Debug(LOG_TAG, "onReceive " + intent.getAction());
		context.startService(new Intent(context, AppService.class));
	}
}
