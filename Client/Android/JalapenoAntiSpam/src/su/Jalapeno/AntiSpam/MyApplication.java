package su.Jalapeno.AntiSpam;

import android.app.Application;
import su.Jalapeno.AntiSpam.DAL.RepositoryFactory;

/**
 * Created by alexander.kiryushkin on 09.01.14.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RepositoryFactory.initRepository(getApplicationContext());
    }
    @Override
    public void onTerminate() {
        RepositoryFactory.releaseRepository();
        super.onTerminate();
    }
}