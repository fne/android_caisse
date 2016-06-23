package fr.info.antillesinfov2.activity;

import android.os.Bundle;
import android.app.Activity;

import fr.info.antillesinfov2.R;

public class DetailPanierActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_panier);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
