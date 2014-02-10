package su.Jalapeno.AntiSpam.AntiSpamTest.ServicesTests.RestTest;

import android.test.AndroidTestCase;
import junit.framework.Assert;
import retrofit.RestAdapter;

import java.util.List;

public class RestTest extends AndroidTestCase {

    public RestTest() {
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testRest() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer("https://api.github.com")
                .build();

        GitRest service = restAdapter.create(GitRest.class);

        List<Repo> repos = service.listRepos("octocat");
        Assert.assertEquals(repos.size(), 2);
    }
}
