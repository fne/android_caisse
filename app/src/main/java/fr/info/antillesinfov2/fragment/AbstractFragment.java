package fr.info.antillesinfov2.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.io.IOException;
import java.util.List;

import fr.info.antillesinfov2.activity.DetailInfoActivity;
import fr.info.antillesinfov2.activity.MainActivity;
import fr.info.antillesinfov2.business.model.News;
import fr.info.antillesinfov2.business.service.NewsManager;
import fr.info.antillesinfov2.business.service.NewsManagerImpl;
import fr.info.antillesinfov2.business.service.android.NewsAdapter;

public abstract class AbstractFragment extends Fragment {
	public static final String DEFAULT_RSS = "http://www.domactu.com/rss/actu/";
	public static final String GP_RSS = "http://www.domactu.com/rss/actu/guadeloupe/";
	public static final String MQ_RSS = "http://www.domactu.com/rss/actu/martinique/";


	protected HttpRequestTask httpRequestTask;
	protected List<News> listNews;
	protected ListView listView;
	protected int idListView;
	protected int idView;
	protected String url;

	/**
	 * méthode permettant de spécifier les proprietes de la classe
	 */
	protected abstract void initProperties();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initProperties();
		View rootView = inflater.inflate(idView,
				container, false);
		listView = (ListView) rootView.findViewById(idListView);
		httpRequestTask = new HttpRequestTask();
		httpRequestTask.execute(url);
		return listView;
	}

	/**
	 * Tache permettant de recuperer les infos via un flux et de completer le
	 * layout
	 * 
	 * @author NEBLAI
	 * 
	 */
	protected class HttpRequestTask extends AsyncTask<String, Integer, String> {
		private NewsManager newsManager = new NewsManagerImpl();;

		@Override
		protected String doInBackground(String... params) {
			String rssSource = newsManager.getNewsChannel(params[0]);
			return rssSource;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// mProgressDialog.show();
		}

		protected void onPostExecute(String rssSource) {
			String result = null;
			try {
				newsManager = new NewsManagerImpl();
				List<News> arrayListNews = newsManager.getListNews(rssSource);
				setListNews(arrayListNews);

				NewsAdapter adapter = new NewsAdapter(getActivity(),
						arrayListNews);
				// Pour finir association du simpleadapter a la vue
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
//						Intent intent = new Intent(getActivity(),
//								DetailInfoActivity.class);
//						intent.putExtra(MainActivity.EXTRA_MESSAGE,
//								getListNews().get(arg2));
//						startActivity(intent);

					}
				});
			} catch (IOException e) {
				Log.i("mainActivity", "network exception");
				result = e.getMessage();
				e.printStackTrace();
			} catch (Exception e) {
				Log.i("mainActivity", "no news");
				result = e.getMessage();
				e.printStackTrace();
			} finally {
				/*
				 * mProgressDialog.dismiss(); if (result != null)
				 * Toast.makeText(getApplicationContext(), "Download error: " +
				 * result, Toast.LENGTH_LONG) .show(); else
				 * Toast.makeText(getApplicationContext(), "News downloaded !",
				 * Toast.LENGTH_SHORT).show();
				 */
			}

		}
	}

	protected List<News> getListNews() {
		return listNews;
	}

	protected void setListNews(List<News> listNews) {
		this.listNews = listNews;
	}

	protected ListView getListView() {
		return listView;
	}

	protected void setListView(ListView listView) {
		this.listView = listView;
	}

	protected int getIdListView() {
		return idListView;
	}

	protected void setIdListView(int idListView) {
		this.idListView = idListView;
	}

	protected String getUrl() {
		return url;
	}

	protected void setUrl(String url) {
		this.url = url;
	}

	protected int getIdView() {
		return idView;
	}

	protected void setIdView(int idView) {
		this.idView = idView;
	}

}
