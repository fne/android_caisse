package fr.info.antillesinfov2.activity;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.model.DetailPanierCommande;
import fr.info.antillesinfov2.business.model.PanierCommande;
import fr.info.antillesinfov2.business.service.CaisseDao;
import fr.info.antillesinfov2.business.service.android.ArticleAdapter;
import fr.info.antillesinfov2.business.service.android.PanierCommandeAdapter;

public class DetailPanierActivity extends AppCompatActivity {

    private ListView listeCommandesView;
    private ListView detailCommandeView;
    private CaisseDao caisseDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        caisseDao = CaisseDao.getInstance(this);
        setContentView(R.layout.activity_detail_panier);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle(R.string.string_recap_caisse);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //initialisation des listview
        listeCommandesView = (ListView) findViewById(R.id.liste_commandes);
        detailCommandeView = (ListView) findViewById(R.id.detail_commande);
        List<DetailPanierCommande> dpcs = caisseDao.getDetailPanierCommandes();
        Log.i(DetailPanierCommande.class.getName(), Integer.toString(dpcs.size()));
        buildPanierCommandeListe();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void buildPanierCommandeListe() {

        Type listType = new TypeToken<ArrayList<DetailPanierCommande>>() {
        }.getType();
        ArrayList<DetailPanierCommande> pcs = new Gson().fromJson(caisseDao.getData(getString(R.string.key_detail_panier)), listType);
        PanierCommandeAdapter pca = new PanierCommandeAdapter(this, pcs);
        listeCommandesView.setAdapter(pca);
        listeCommandesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                buildDetailCommande(caisseDao.getDetailPanierCommandes().get(position));
            }
        });
    }

    private void buildDetailCommande(DetailPanierCommande dpc) {
        Log.i(DetailPanierCommande.class.getName(), "detail de la commande");
        ArticleAdapter aa = new ArticleAdapter(this, dpc.getProduits());
        detailCommandeView.setAdapter(aa);
    }

}
