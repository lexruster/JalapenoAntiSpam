package su.Jalapeno.AntiSpam.AntiSpamTest.ServicesTests.RestTest;

import retrofit.http.GET;
import retrofit.http.Path;


import java.util.List;
//import retrofit;

/**
 * Created by alexander.kiryushkin on 14.01.14.
 */
public interface GitRest {
        @GET("/users/{user}/repos")
        List<Repo> listRepos(@Path("user") String user);
}
