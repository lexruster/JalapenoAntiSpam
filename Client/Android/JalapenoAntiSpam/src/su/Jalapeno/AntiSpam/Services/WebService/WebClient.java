package su.Jalapeno.AntiSpam.Services.WebService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import su.Jalapeno.AntiSpam.Util.Constants;
import android.util.Log;

public class WebClient {
	final static String LOG_TAG = Constants.BEGIN_LOG_TAG + "WebClient";

	public static String Get(String url) {
		String result = "";

		try {
			//
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();

				try {
					result = convertStreamToString(content);
					content.close();
					return result;

				} catch (Exception ex) {
					Log.e(LOG_TAG, "Failed to parse JSON due to: " + ex);

				}
			} else {
				Log.e(LOG_TAG, "Server responded with status code: "
						+ statusLine.getStatusCode());

			}
		} catch (Exception ex) {
			Log.e(LOG_TAG, "Failed to send HTTP POST request due to: " + ex);

		}
		return null;
	}

	public static String Post(String url, String postData) {
		String result = "";

		try {
			// Create an HTTP client
			HttpClient client = new DefaultHttpClient();
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
					return result;

				} catch (Exception ex) {
					Log.e(LOG_TAG, "Failed to parse JSON due to: " + ex);

				}
			} else {
				Log.e(LOG_TAG, "Server responded with status code: "
						+ statusLine.getStatusCode());

			}
		} catch (Exception ex) {
			Log.e(LOG_TAG, "Failed to send HTTP POST request due to: " + ex);

		}
		return null;

	}

	private static HttpEntity CreateEntity(String value) {
		StringEntity se = null;
		try {
			se = new StringEntity(value, "UTF-16");
			
			se.setContentType("application/json; charset=UTF-16");
			// se.setContentType("application/json");
		} catch (UnsupportedEncodingException e) {
			Log.e(LOG_TAG, "Failed to create StringEntity", e);
		}
		return se;
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
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
