package fr.info.antillesinfov2.activity;

import java.io.IOException;
import java.util.List;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.model.News;
import fr.info.antillesinfov2.business.service.NewsManager;
import fr.info.antillesinfov2.business.service.NewsManagerImpl;
import fr.info.antillesinfov2.business.service.android.NewsAdapter;
import fr.info.antillesinfov2.fragment.impl.GuadeloupeSectionFragment;

public class MainActivity extends FragmentActivity {

	public static final String EXTRA_MESSAGE = "news";
	public static final String DEFAULT_RSS = "http://www.domactu.com/rss/actu/";
	public static final String GP_RSS = "http://www.domactu.com/rss/actu/guadeloupe/";
	public static final String MQ_RSS = "http://www.domactu.com/rss/actu/martinique/";
	public static String RSS_TO_OPEN;
	private ListView vue;
	private List<News> listNews;
	// declare the dialog as a member field of your activity
	private ProgressDialog mProgressDialog;
	//private HttpRequestTask httpRequestTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(MainActivity.this);
		mProgressDialog.setMessage("Loading news !");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
		mProgressDialog
		.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//httpRequestTask.cancel(true);
			}
		});
		
		//rajout d'un fragment dans le cas où le layout utilisé correspond
		// Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            GuadeloupeSectionFragment firstFragment = new GuadeloupeSectionFragment();
            
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }		
	}
}
