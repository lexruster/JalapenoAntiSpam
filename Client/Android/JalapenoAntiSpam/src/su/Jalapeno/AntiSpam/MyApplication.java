package su.Jalapeno.AntiSpam;

import android.app.Application;
import android.content.Intent;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;
import su.Jalapeno.AntiSpam.SystemService.AppService;

/**
 * Created by alexander.kiryushkin on 09.01.14.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, AppService.class));
        RepositoryFactory.initRepository(getApplicationContext());
    }
    @Override
    public void onTerminate() {
        RepositoryFactory.releaseRepository();
        super.onTerminate();
    }
}