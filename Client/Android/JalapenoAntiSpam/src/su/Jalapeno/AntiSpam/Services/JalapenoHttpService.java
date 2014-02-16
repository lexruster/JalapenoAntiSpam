package su.Jalapeno.AntiSpam.Services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Created by Kseny on 30.12.13.
 */
public class JalapenoHttpService {

    private Context _context;

    public JalapenoHttpService(Context context) {

        _context = context;
    }

    public boolean IsSpamer(String address) {
        return false;
    }

    public boolean TryComplain(String phone) {
        return true;
    }

    public String SendLocalTestRequest()
    {
        return SendRequest("http://localhost/TestWeb/");
    }

    public boolean ServiceIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }

        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni != null && ni.isAvailable() && ni.isConnected()) {
            return true;
        }
        return false;
    }

    private String SendRequest(String url) {
        String responseBody = "";

        try {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(new HttpGet(url));
            HttpEntity entity = response.getEntity();
            responseBody = EntityUtils.toString(entity);
        } catch (IOException ex) {

        }

        return responseBody;
    }
}
