package fr.info.antillesinfov2.activity;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.model.DetailPanierCommande;
import fr.info.antillesinfov2.business.service.CaisseDao;
import fr.info.antillesinfov2.business.service.android.ArticleAdapter;
import fr.info.antillesinfov2.business.service.android.PanierCommandeAdapter;

public class DetailPanierActivity extends Activity {

    private ListView listeCommandesView;
    private ListView detailCommandeView;
    private CaisseDao caisseDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        caisseDao = CaisseDao.getInstance(this);
        setContentView(R.layout.activity_detail_panier);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.string_recap_caisse);
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
        PanierCommandeAdapter pca = new PanierCommandeAdapter(this, caisseDao.getDetailPanierCommandes());
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
