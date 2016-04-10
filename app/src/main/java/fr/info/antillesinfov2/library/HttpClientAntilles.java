package fr.info.antillesinfov2.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class HttpClientAntilles {
	private static final String DEBUG_TAG = "HttpClientAntilles";
	public HttpClientAntilles() {
	}

	public HttpResponse get(String url) throws IOException {
		HttpGet request = new HttpGet(url);
		HttpResponse httpResponse = null;

			// utilisation du proxy
			DefaultHttpClient httpclient = new DefaultHttpClient();
			// execution de la requete
			final HttpParams httpParameters = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
			HttpConnectionParams.setSoTimeout(httpParameters, 3000);
			return httpclient.execute(request);
	}
	
	public HttpResponse post(String url, String requestBody) throws IOException {
		HttpPost request = new HttpPost(url);

			DefaultHttpClient httpclient = new DefaultHttpClient();
			StringEntity se = new StringEntity(requestBody);
			request.setEntity(se);
			request.setHeader("Content-Type","application/json");
			HttpResponse httpResponse = httpclient.execute(request);
			return httpResponse;

	}

	public String readContentsOfUrl(String url) throws IOException {
		String result = null;

		//requete http avec set du proxy

		HttpResponse httpResponse = this.get(url);

		int response = httpResponse.getStatusLine().getStatusCode();
        if(response != 200){
            throw new IOException("erreur sur le flux reçu");
        }
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
