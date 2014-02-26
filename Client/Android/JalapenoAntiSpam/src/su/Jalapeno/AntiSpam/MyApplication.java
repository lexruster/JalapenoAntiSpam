package su.Jalapeno.AntiSpam;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.SystemService.AppService;
import su.Jalapeno.AntiSpam.Util.Constants;

/**
 * Created by alexander.kiryushkin on 09.01.14.
 */
public class MyApplication extends Application {
	final String LOG_TAG = Constants.BEGIN_LOG_TAG + "MyApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(LOG_TAG, "onCreate ");
		startService(new Intent(this, AppService.class));
		RepositoryFactory.initRepository(getApplicationContext());
	}

	@Override
	public void onTerminate() {
		Log.d(LOG_TAG, "onTerminate ");
		RepositoryFactory.releaseRepository();
		super.onTerminate();
	}
}