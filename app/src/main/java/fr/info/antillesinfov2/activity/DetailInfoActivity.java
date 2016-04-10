package fr.info.antillesinfov2.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.model.News;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class DetailInfoActivity extends Activity {

	private TextView titreInfo;
	private TextView descriptionInfo;

	private ImageView imageInfo;
	private News myNews;
	
	public static final String URL_LINK = "http://www.google.com";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail_info);
		// Show the Up button in the action bar.
		// Get the message from the intent
		Intent intent = getIntent();
		myNews = (News) intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE);

		// Create the text view
		titreInfo = (TextView) findViewById(R.id.text_view_detail_info);
		descriptionInfo = (TextView) findViewById(R.id.text_view_description_info);
		imageInfo = (ImageView) findViewById(R.id.img_detail_info);
		titreInfo.setText(myNews.getTitle());
		descriptionInfo.setText(myNews.getDescription());
		if (myNews.getImageUrl() != null)
			UrlImageViewHelper.setUrlDrawable(imageInfo, myNews.getImageUrl());

		// Prise en charge de l action bar
		setupActionBar();

	}

	public void onClick(View arg0) {	
		//Création de la webview
		Intent intent = new Intent(getApplicationContext(),
				WebViewActivity.class);
		intent.putExtra(DetailInfoActivity.URL_LINK, myNews.getLink());
		intent.putExtra(MainActivity.EXTRA_MESSAGE, myNews);
		startActivity(intent);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail_info, menu);

		MenuItem item = menu.findItem(R.id.menu_item_share);
		ShareActionProvider myShareActionProvider = (ShareActionProvider) item
				.getActionProvider();
		Intent myIntent = new Intent();
		myIntent.setAction(Intent.ACTION_SEND);
		myIntent.putExtra(Intent.EXTRA_TEXT, myNews.getLink());
		myIntent.setType("text/plain");
		myShareActionProvider.setShareIntent(myIntent);

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
