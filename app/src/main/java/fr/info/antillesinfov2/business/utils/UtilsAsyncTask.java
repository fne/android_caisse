package fr.info.antillesinfov2.business.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;

import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.library.HttpClient;

/**
 * Created by qjrs8151 on 24/06/2016.
 */
public class UtilsAsyncTask extends AsyncTask<String, Void, String> {

    private OnTaskCompleted listener;

    public UtilsAsyncTask(OnTaskCompleted task) {
        listener = task;
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    @Override
    protected String doInBackground(String... params) {
        String reponse = null;
        try {
            reponse =  HttpClient.getInstance().readContentsOfUrl(params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reponse;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onPreExecuteTask();
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String response) {
        listener.onTaskCompleted(response);
    }
}
