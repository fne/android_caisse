package fr.info.antillesinfov2.activity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.activity.dialog.DialogHandler;
import fr.info.antillesinfov2.business.model.Article;
import fr.info.antillesinfov2.business.model.PanierCommande;
import fr.info.antillesinfov2.business.model.Produit;
import fr.info.antillesinfov2.business.service.CSVFileWriter;
import fr.info.antillesinfov2.business.service.CaisseDao;
import fr.info.antillesinfov2.fragment.PanierFragment;
import fr.info.antillesinfov2.fragment.ProduitsFragment;
import fr.info.antillesinfov2.fragment.ProduitsFragment.OnProduitsFragmentInteractionListener;

public class CaisseActivity extends Activity implements OnProduitsFragmentInteractionListener, PanierFragment.OnFragmentPanierInteractionListener {

    private List<Produit> produits = new ArrayList<Produit>();
    private Map<String, Integer> articlesMap = new HashMap<String, Integer>();
    private PanierFragment panierFragment;
    private ProduitsFragment produitsFragment;
    private DialogHandler dialogHandler;
    private double total = 0.00;
    private TextView tv;
    private CaisseDao caisseDao;
    private String caisseId;
    private String sessionId;


    //@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)

    @Override
    /**
     * initialisation du panier
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //change title
        /*getActionBar().setLogo(R.drawable.fresca);
        getActionBar().setDisplayUseLogoEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setTitle("");*/
        getActionBar().setDisplayUseLogoEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);

        setContentView(R.layout.activity_caisse);
        //plug des methodes de bouton
        //action réalisée validation de panier via CB
        Button validerCBButton = (Button) findViewById(R.id.button_cb);
        validerCBButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("CaisseActivity", "valider" + produits.size());
                validerCommande(1);
            }
        });
        //action réalisée validation de panier via espece
        Button validerEspeceButton = (Button) findViewById(R.id.button_espece);
        validerEspeceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("CaisseActivity", "valider" + produits.size());
                validerCommande(2);
            }
        });

        //TODO
        //action réalisée validation de panier via chèque

        //action réalisée suppression des informations présentes dans le panier
        Button viderButton = (Button) findViewById(R.id.button_vider);
        viderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("CaisseActivity", "vider");
                //modification du panierFragment pour supprimer tous les éléments
                reset();
            }
        });

        //récupération du handler permettant d'enregistrer les informations dans le panier
        //soit sur la tablette
        //soit directement en base de données
        caisseDao = CaisseDao.getInstance(this);
        //caisseDao.setSp(getPreferences(MODE_APPEND));

        //création d'un handler permettant de gérer les boites de dialogues
        dialogHandler = new DialogHandler(CaisseActivity.this);

        caisseDao.setSp(getSharedPreferences("fr.salsafresca.caisse.test_fresca", MODE_APPEND));
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        caisseDao.initData();
        caisseDao.setCsv(new CSVFileWriter());
        if ((networkInfo != null) && networkInfo.isConnected()) {
            caisseDao.sendOldData();
        }
        tv = (TextView) findViewById(R.id.total_caisse);
        tv.setText(Double.toString(total));

        //initialisation des fragments de l'applicaton
        panierFragment = (PanierFragment) getFragmentManager().findFragmentById(R.id.panier_fragment);
        produitsFragment = (ProduitsFragment) getFragmentManager().findFragmentById(R.id.produits_fragment);

        //affichage de la popup permettant de gérer les infos de session et de caisse
    }

    /**
     * RAZ du panier
     */
    private void reset() {
        panierFragment.viderPanier();
        produits.clear();
        articlesMap.clear();
        total = 0.0;
        tv.setText(Double.toString(total));
    }

    /**
     * validation de commande pour envoie au webservice
     */
    private void validerCommande(Integer typePaiement) {
        if (produits.size() > 0) {
            PanierCommande pc = new PanierCommande();
            pc.setArticles(buildArticles());
            pc.setPrixTotal(total);
            pc.setNbArticles(produits.size());
            pc.setMoyenPaiement(typePaiement);
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.FRANCE);
            pc.setDate(sdf.format(date));
            caisseDao.saveMouvementPaiement(typePaiement, total);
            caisseDao.saveDataPanier(pc);
            reset();
            Toast.makeText(this, "Panier validé !!!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Pas de produits dans le panier", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * mise à jour du panier quand il y a ajout ou retrait du produit
     *
     * @param p
     * @param produitAdded
     */
    private void updateMapArticles(Produit p, Boolean produitAdded) {
        //vérifie que l'article n'est pas dé jà présent
        if (produitAdded) {
            if (articlesMap.get(p.getProductCode()) != null) {
                articlesMap.put(p.getProductCode(), articlesMap.get(p.getProductCode()) + 1);
            } else {
                articlesMap.put(p.getProductCode(), 1);
            }
        } else {
            if (articlesMap.get(p.getProductCode()) != null && articlesMap.get(p.getProductCode()) != 1) {
                articlesMap.put(p.getProductCode(), articlesMap.get(p.getProductCode()) - 1);
            } else {
                articlesMap.remove(p.getProductCode());
            }
        }

    }

    /**
     * construction de l objet panier à envoyer au niveau de la ressource ws-stock/commandes
     *
     * @return
     */
    private List<Article> buildArticles() {
        List<Article> articles = new ArrayList<Article>();
        for (Map.Entry<String, Integer> entry : articlesMap.entrySet()) {
            Article a = new Article();
            a.setIdProduit(entry.getKey());
            a.setQuantite(entry.getValue());
            articles.add(a);
        }
        return articles;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.caisse_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_a_propos:
                dialogHandler.showDialogAppInfo();
                break;
            case R.id.action_reinit:
                dialogHandler.showDialogReinitCaisse();
                break;
            case R.id.action_config:
                dialogHandler.showDialogConfig();
                break;
            case R.id.action_boisson:
                produitsFragment.afficherProduitsBar();
                break;
            case R.id.action_snack:
                produitsFragment.afficherProduitsSnack();
                break;
            case R.id.action_caisse:
                dialogHandler.showDialogMenuCaisse();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    /**
     * action réalisée lorsqu'on clique sur un des items du produit
     */
    public void onProduitsFragmentInteraction(Produit p) {
        //rajout du produit au panier
        produits.add(p);
        //mise à jour de la map d'article
        updateMapArticles(p, true);
        //Mise à jour de la somme totale
        total = total + p.getProductPrice();
        tv.setText(Double.toString(total));
        if (panierFragment != null) {
            //rajout du produit au panier fragment
            panierFragment.updatePanierView(p);
        }
    }

    @Override
    /**
     * action réalisée au niveau du fragment panier lorsqu'on clique sur un des items du panier
     */
    public void onFragmentPanierInteraction(Integer indexProduit) {
        //suppression du produit du paniers
        Produit p = produits.get(indexProduit);
        updateMapArticles(p, false);
        total = total - p.getProductPrice();
        tv.setText(Double.toString(total));
        produits.remove(p);
        Log.i("caisseActivity", "suppression du produit de la liste" + indexProduit.toString() + " " + p.getProductPrice() + " total " + total);
    }
}
