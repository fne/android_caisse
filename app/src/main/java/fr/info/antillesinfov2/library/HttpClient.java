package fr.info.antillesinfov2.library;

import android.app.Activity;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpClient {
    private static final String DEBUG_TAG = "HttpClient";
    private final DefaultHttpClient mhttpClient;
    private static HttpClient mInstance = null;

    public HttpClient() {
        mhttpClient = new DefaultHttpClient();
    }

    public synchronized static HttpClient getInstance() {
        if (mInstance == null) {
            mInstance = new HttpClient();
        }
        return mInstance;
    }

    public HttpResponse get(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        // execution de la requete
        final HttpParams httpParameters = mhttpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
        HttpConnectionParams.setSoTimeout(httpParameters, 3000);
        return mhttpClient.execute(request);
    }

    public HttpResponse post(String url, String requestBody) throws IOException {
        HttpPost request = new HttpPost(url);
        //DefaultHttpClient httpclient = new DefaultHttpClient();
        StringEntity se = new StringEntity(requestBody);
        request.setEntity(se);
        request.setHeader("Content-Type", "application/json");
        HttpResponse httpResponse = mhttpClient.execute(request);
        return httpResponse;
    }

    public String readContentsOfUrl(String url) throws IOException {
        try {
            String result = null;
            //requete http avec set du proxy
            HttpResponse httpResponse = this.get(url);
            int response = httpResponse.getStatusLine().getStatusCode();
            if (response != 200) {
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
        } finally {
            mhttpClient.getConnectionManager().closeExpiredConnections();
        }
    }

}
