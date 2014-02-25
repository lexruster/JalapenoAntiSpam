package su.Jalapeno.AntiSpam.SystemService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceBroadcastReceiver extends BroadcastReceiver {

	  final String LOG_TAG = "myLogs";

	  @Override
	  public void onReceive(Context context, Intent intent) {
	    Log.d(LOG_TAG, "onReceive " + intent.getAction());
	    context.startService(new Intent(context, AppService.class));
	  }
	}
