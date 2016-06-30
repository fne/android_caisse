package fr.info.antillesinfov2.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.activity.dialog.DialogHandler;
import fr.info.antillesinfov2.business.Constant;
import fr.info.antillesinfov2.business.model.Article;
import fr.info.antillesinfov2.business.model.DetailPanierCommande;
import fr.info.antillesinfov2.business.model.PanierCommande;
import fr.info.antillesinfov2.business.model.Produit;
import fr.info.antillesinfov2.business.model.Session;
import fr.info.antillesinfov2.business.service.CaisseDao;
import fr.info.antillesinfov2.business.service.FileWriter;
import fr.info.antillesinfov2.business.utils.OnTaskCompleted;
import fr.info.antillesinfov2.business.utils.UtilsAsyncTask;
import fr.info.antillesinfov2.fragment.PanierFragment;
import fr.info.antillesinfov2.fragment.PanierFragment.OnFragmentPanierInteractionListener;
import fr.info.antillesinfov2.fragment.ProduitsFragment;
import fr.info.antillesinfov2.fragment.ProduitsFragment.OnProduitsFragmentInteractionListener;

public class CaisseActivity extends AppCompatActivity implements OnProduitsFragmentInteractionListener, OnFragmentPanierInteractionListener, OnTaskCompleted {

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
        /*getActionBar().setLogo(R.drawable.fresca);
        getActionBar().setDisplayUseLogoEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);*/
        /*getActionBar().setTitle("");
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5e9c00")));
        getActionBar().setDisplayUseLogoEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);*/

        //setSupportActionBar(toolbar);
        setContentView(R.layout.activity_caisse);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        //récupération du handler permettant d'enregistrer les informations dans le panier
        //soit sur la tablette
        //soit directement en base de données
        caisseDao = CaisseDao.getInstance(this);
        //caisseDao.setSp(getPreferences(MODE_APPEND));

        //création d'un handler permettant de gérer les boites de dialogues
        dialogHandler = new DialogHandler(CaisseActivity.this);
        caisseDao.setSp(getSharedPreferences("fr.salsafresca.caisse.test_fresca", MODE_APPEND));
        caisseDao.initData();
        caisseDao.setFileWriter(new FileWriter());
        tv = (TextView) findViewById(R.id.total_caisse);
        tv.setText(Double.toString(total));

        //affichage de la popup permettant de gérer les infos de session et de caisse
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if ((networkInfo != null) && networkInfo.isConnected()) {
            //synchronisation des paniers enregistrés
            caisseDao.sendOldData();
            //récupération de la session
            new UtilsAsyncTask(this).execute(Constant.URL_SESSION);
        } else {
            //affichage de la popup de configuration
            dialogHandler.showDialogConfig();
        }

        //plug des methodes de bouton
        //action réalisée validation de panier via CB
        Button validerCBButton = (Button) findViewById(R.id.button_cb);
        validerCBButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("CaisseActivity", "valider CB" + produits.size());
                validerCommande("CB");
            }
        });
        //action réalisée validation de panier via espece
        Button validerEspeceButton = (Button) findViewById(R.id.button_espece);
        validerEspeceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("CaisseActivity", "valider ESP" + produits.size());
                validerCommande("ESP");
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

        //initialisation des fragments de l'applicaton
        panierFragment = (PanierFragment) getFragmentManager().findFragmentById(R.id.panier_fragment);
        produitsFragment = (ProduitsFragment) getFragmentManager().findFragmentById(R.id.produits_fragment);
        Log.i("CaisseActivity", "onCreate");
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
    private void validerCommande(String typePaiement) {
        if (produits.size() > 0) {
            PanierCommande pc = new PanierCommande();
            pc.setArticles(buildArticles());
            pc.setPrixTotal(total);
            pc.setNbArticles(produits.size());
            pc.setMoyenPaiement(typePaiement);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.FRANCE);
            pc.setDate(sdf.format(new Date()));
            caisseDao.saveMouvementPaiement(typePaiement, total);
            caisseDao.saveDataPanier(pc);
            buildDetailPanierCommande(typePaiement);
            reset();
            Toast.makeText(this, "Panier validé !!!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Pas de produits dans le panier", Toast.LENGTH_SHORT).show();
        }
    }

    private void buildDetailPanierCommande(String typePaiement) {
        DetailPanierCommande dpc = new DetailPanierCommande();
        ArrayList<Produit> dpcs = new ArrayList<Produit>();
        for (Produit p : produits) {
            dpcs.add(p);
        }
        dpc.setProduits(dpcs);
        dpc.setPrixTotal(total);
        dpc.setNbArticles(produits.size());
        dpc.setMoyenPaiement(typePaiement);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.FRANCE);
        dpc.setDate(sdf.format(new Date()));
        //rajout de données dans detail panier commande
        caisseDao.saveDetailPanier(dpc);
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
            for (Produit p : produits) {
                if (p.getProductCode() == entry.getKey()) {
                    a.setPrice(p.getProductPrice());
                    break;
                }
            }
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
            case R.id.action_json:
                caisseDao.writeData();
                break;
            case R.id.action_recap_caisse:
                Intent intent = new Intent(this, DetailPanierActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    /**
     * action réalisée lorsqu'on clique sur un des items du produit
     */
    public void onProduitsFragmentInteraction(Produit p) {
        if (!TextUtils.isEmpty(p.getProductCode())) {
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

    private void setSessionId(String response) {
        Session s = new Gson().fromJson(response, Session.class);
        sessionId = s.getSessionId();
        caisseDao.saveData(getString(R.string.key_id_session), sessionId);
        //if (caisseDao.getData(getString(R.string.key_id_caisse)) == null) {
        dialogHandler.showDialogConfig();
        //}
        Log.i(CaisseActivity.class.getName(), "log onTaskCompleted setSessionId");
    }

    @Override
    public void onTaskCompleted(String response) {
        if (response != null) {
            setSessionId(response);
        } else {
            //affichage de la boite de dialogue de config
            dialogHandler.showDialogConfig();
        }
        Log.i(CaisseActivity.class.getName(), "log onTaskCompleted");
    }

    @Override
    public void onPreExecuteTask() {
        //do nothing
    }
}
