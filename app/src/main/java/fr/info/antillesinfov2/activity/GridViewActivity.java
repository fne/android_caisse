package fr.info.antillesinfov2.activity;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.model.News;
import fr.info.antillesinfov2.business.service.android.ImageAdapter;

public class GridViewActivity extends Activity {

	public static final String EXTRA_MESSAGE = "news";
	public static final String DEFAULT_RSS = "http://www.domactu.com/rss/actu/";
	public static final String GP_RSS = "http://www.domactu.com/rss/actu/guadeloupe/";
	public static final String MQ_RSS = "http://www.domactu.com/rss/actu/martinique/";
	public static String RSS_TO_OPEN;
	private ListView vue;
	private List<News> listNews;
	// declare the dialog as a member field of your activity
	private ProgressDialog mProgressDialog;

	// private HttpRequestTask httpRequestTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gridview);
		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new ImageAdapter(this));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Toast.makeText(getApplicationContext(), "" + position,
						Toast.LENGTH_SHORT).show();
			}
		});
		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(GridViewActivity.this);
		mProgressDialog.setMessage("Loading news !");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
		mProgressDialog
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						// httpRequestTask.cancel(true);
					}
				});
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		// rajout des ments via l'adapter

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_tab, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// NavUtils.navigateUpFromSameTask(this);
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
