package fr.info.antillesinfov2.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import fr.info.antillesinfov2.R;

public class WebViewActivity extends Activity {

	private WebView webView;
	private String urlToOpen;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_web_view);
		final Activity activity = this;
		// Get the url to open from the intent
		Intent intent = getIntent();
		urlToOpen = (String) intent
				.getSerializableExtra(DetailInfoActivity.URL_LINK);
				
		webView = new WebView(this);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);	      
		webView.getSettings().setDefaultZoom(ZoomDensity.FAR);
		
	      webView.setWebViewClient(new WebViewClient() {
	            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
	            }
	        });		
		webView.loadUrl(urlToOpen);
		setContentView(webView);
		

		setupActionBar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Deserialisation du menu
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_view, menu);
		return true;
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//simulation de l'action de retour 
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
	
}
