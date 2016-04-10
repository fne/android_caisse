package fr.info.antillesinfov2.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;

import android.util.Log;
import fr.info.antillesinfov2.library.HttpClientAntilles;

public class DaoNews {
	private static final String DEBUG_TAG = "DaoNews";
	private HttpClientAntilles httpClient;
	/**
	 * Lecture des donnï¿½es sur un serveur distant
	 * 
	 * @param url
	 * @return
	 */
	public String readContentsOfUrl(String url) throws IOException {
		String result = null;

			//requete http avec set du proxy
			httpClient = new HttpClientAntilles();
			HttpResponse httpResponse = httpClient.get(url);
			
			int response = httpResponse.getStatusLine().getStatusCode();
			Log.d(DEBUG_TAG, "The response is: " + response);
			//lecture de la requete http
			InputStream is = httpResponse.getEntity().getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String line = reader.readLine();
			result = line;
			while ((line = reader.readLine()) != null) {
				result += line;
			}			

		return result;
	}	
}
