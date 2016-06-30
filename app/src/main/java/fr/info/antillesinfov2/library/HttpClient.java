package fr.info.antillesinfov2.library;

import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class HttpClient {
    private static final String DEBUG_TAG = "HttpClient";
    private final OkHttpClient mhttpClient;
    private static HttpClient mInstance = null;
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("application/json; charset=utf-8");

    public HttpClient() {
        mhttpClient = new OkHttpClient();
    }

    public synchronized static HttpClient getInstance() {
        if (mInstance == null) {
            mInstance = new HttpClient();
        }
        return mInstance;
    }

    public Response get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        mhttpClient.setConnectTimeout(3000, TimeUnit.MILLISECONDS);
        Response response = mhttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return response;
    }

    public Response post(String url, String postBody) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody))
                .build();

        Response httpResponse = mhttpClient.newCall(request).execute();
        if (!httpResponse.isSuccessful())
            throw new IOException("Unexpected code " + httpResponse.code());
        return httpResponse;
    }

    public String readContentsOfUrl(String url) throws IOException {
        String result = null;
        //requete http avec set du proxy
        Response httpResponse = this.get(url);
        int response = httpResponse.code();
        if (response != 200) {
            throw new IOException("erreur sur le flux reçu");
        }
        Log.d(DEBUG_TAG, "The response is: " + response);
        //lecture de la requete http
        InputStream is = httpResponse.body().byteStream();
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
