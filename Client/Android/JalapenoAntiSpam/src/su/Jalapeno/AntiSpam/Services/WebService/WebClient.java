package su.Jalapeno.AntiSpam.Services.WebService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import su.Jalapeno.AntiSpam.Util.Constants;
import su.Jalapeno.AntiSpam.Util.Logger;

public class WebClient {
	final static String LOG_TAG = Constants.BEGIN_LOG_TAG + "WebClient";

	public static String Get(String url) {
		String result = "";

		try {
			Logger.Debug(LOG_TAG, "Get from: " + url);
			HttpClient client = GetHttpClient();

			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();

				try {
					result = convertStreamToString(content);
					content.close();
					Logger.Debug(LOG_TAG, "Get complete with result: " + result);
					return result;

				} catch (Exception ex) {
					Logger.Error(LOG_TAG, "Failed to parse JSON due to: " + ex);

				}
			} else {
				Logger.Error(LOG_TAG, "Server responded with status code: " + statusLine.getStatusCode());
			}
		} catch (Exception ex) {
			Logger.Error(LOG_TAG, "Failed to send HTTP POST request due to: " + ex);
		}

		return null;
	}

	public static String Post(String url, String postData) {
		String result = "";

		try {
			Logger.Debug(LOG_TAG, "Post to: " + url + " with data " + postData);
			// Create an HTTP client
			HttpClient client = GetHttpClient();
			HttpPost post = new HttpPost(url);
			post.setEntity(CreateEntity(postData));
			post.setHeader("Content-Type", "application/json");

			// Perform the request and check the status code
			HttpResponse response = client.execute(post);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();

				try {
					result = convertStreamToString(content);
					content.close();
					Logger.Debug(LOG_TAG, "Post complete with result: " + result);
					return result;

				} catch (Exception ex) {
					Logger.Error(LOG_TAG, "Failed to parse JSON due to: " + ex);

				}
			} else {
				Logger.Error(LOG_TAG, "Server responded with status code: " + statusLine.getStatusCode());

			}
		} catch (Exception ex) {
			Logger.Error(LOG_TAG, "Failed to send HTTP POST request due to: " + ex);

		}
		return null;
	}

	private static DefaultHttpClient GetHttpClient() {

		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			HttpConnectionParams.setConnectionTimeout(params, WebConstants.CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, WebConstants.SOCKET_TIMEOUT);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
			Logger.Debug(LOG_TAG, "GetHttpClient set params");

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	/*
	 * private static void SetSSLPolicy() { // Create a trust manager that does
	 * not validate certificate chains TrustManager[] trustAllCerts = new
	 * TrustManager[] { new X509TrustManager() { public
	 * java.security.cert.X509Certificate[] getAcceptedIssuers() { return null;
	 * }
	 * 
	 * public void checkClientTrusted(java.security.cert.X509Certificate[]
	 * certs, String authType) { Logger.Debug(LOG_TAG, "checkClientTrusted"); }
	 * 
	 * public void checkServerTrusted(java.security.cert.X509Certificate[]
	 * certs, String authType) { Logger.Debug(LOG_TAG, "checkServerTrusted"); }
	 * } };
	 * 
	 * // Install the all-trusting trust manager try { SSLContext sc =
	 * SSLContext.getInstance("SSL"); sc.init(null, trustAllCerts, new
	 * java.security.SecureRandom());
	 * HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	 * Logger.Debug(LOG_TAG, "SetSSLPolicy"); } catch (GeneralSecurityException
	 * e) { } }
	 */
	private static HttpEntity CreateEntity(String value) {
		StringEntity se = null;
		try {
			se = new StringEntity(value, Constants.DEFAULT_ENCODING);

			se.setContentType("application/json; charset=" + Constants.DEFAULT_ENCODING);
			// se.setContentType("application/json");
		} catch (UnsupportedEncodingException e) {
			Logger.Error(LOG_TAG, "Failed to create StringEntity", e);
		}
		return se;
	}

	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
