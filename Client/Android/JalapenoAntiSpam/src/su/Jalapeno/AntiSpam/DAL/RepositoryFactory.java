package su.Jalapeno.AntiSpam.DAL;

import android.content.Context;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class RepositoryFactory {

    private static Repository repository;

    public static Repository getRepository() {
        return repository;
    }

    public static void initRepository(Context context) {
        repository = OpenHelperManager.getHelper(context, Repository.class);
    }

    public static void releaseRepository() {
        OpenHelperManager.releaseHelper();
        repository = null;
    }
}
