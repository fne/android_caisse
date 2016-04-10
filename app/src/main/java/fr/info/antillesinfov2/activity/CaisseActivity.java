package fr.info.antillesinfov2.activity;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
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
import fr.info.antillesinfov2.business.model.Article;
import fr.info.antillesinfov2.business.model.PanierCommande;
import fr.info.antillesinfov2.business.model.Produit;
import fr.info.antillesinfov2.business.model.Retrait;
import fr.info.antillesinfov2.business.service.CSVFileWriter;
import fr.info.antillesinfov2.business.service.PanierDao;
import fr.info.antillesinfov2.fragment.PanierFragment;
import fr.info.antillesinfov2.fragment.ProduitsFragment;
import fr.info.antillesinfov2.fragment.ProduitsFragment.OnProduitsFragmentInteractionListener;

import static fr.info.antillesinfov2.R.id.recap_liste_mouvements;

public class CaisseActivity extends Activity implements OnProduitsFragmentInteractionListener, PanierFragment.OnFragmentPanierInteractionListener {

    private List<Produit> produits = new ArrayList<Produit>();
    private Map<Integer, Integer> articlesMap = new HashMap<Integer, Integer>();
    private PanierFragment panierFragment;
    private ProduitsFragment produitsFragment;
    private double total = 0.00;
    private TextView tv;
    private PanierDao panierDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //change title
        getActionBar().setIcon(R.drawable.fresca);
        getActionBar().setTitle("");

        setContentView(R.layout.activity_caisse);
        //plug des methodes de bouton
        Button validerCBButton = (Button) findViewById(R.id.button_cb);
        validerCBButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("CaisseActivity", "valider" + produits.size());
                validerCommande(1);
            }
        });
        Button validerEspeceButton = (Button) findViewById(R.id.button_espece);
        validerEspeceButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("CaisseActivity", "valider" + produits.size());
                validerCommande(2);
            }
        });
        Button viderButton = (Button) findViewById(R.id.button_vider);
        viderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("CaisseActivity", "vider");
                //modification du panierFragment pour supprimer tous les éléments
                reset();
            }
        });
        panierDao = new PanierDao();
        //panierDao.setSp(getPreferences(MODE_APPEND));
        panierDao.setSp(getSharedPreferences("fr.salsafresca.caisse.test_fresca",MODE_APPEND));
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        panierDao.initData();
        panierDao.setCsv(new CSVFileWriter());
        if ((networkInfo != null) && networkInfo.isConnected()) {
            panierDao.sendOldData();
        }
        tv = (TextView) findViewById(R.id.total_caisse);
        tv.setText(Double.toString(total));
        panierFragment = (PanierFragment) getFragmentManager().findFragmentById(R.id.panier_fragment);
        produitsFragment = (ProduitsFragment) getFragmentManager().findFragmentById(R.id.produits_fragment);
    }



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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.FRANCE);
            pc.setDate(sdf.format(date));
            panierDao.saveMouvementPaiement(typePaiement, total);
            panierDao.saveDataPanier(pc);
            reset();
            Toast.makeText(this, "Panier validé !!!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Pas de produits dans le panier", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMapArticles(Produit p, Boolean produitAdded) {
        //vérifie que l'article n'est pas dé jà présent
        if (produitAdded) {
            if (articlesMap.get(p.getProductId()) != null) {
                articlesMap.put(p.getProductId(), articlesMap.get(p.getProductId()) + 1);
            } else {
                articlesMap.put(p.getProductId(), 1);
            }
        } else {
            if (articlesMap.get(p.getProductId()) != null && articlesMap.get(p.getProductId()) != 1) {
                articlesMap.put(p.getProductId(), articlesMap.get(p.getProductId()) - 1);
            } else {
                articlesMap.remove(p.getProductId());
            }
        }

    }

    private List<Article> buildArticles() {
        List<Article> articles = new ArrayList<Article>();
        for (Map.Entry<Integer, Integer> entry : articlesMap.entrySet()) {
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
        //AlertDialog.Builder builder = null;
        switch (item.getItemId()) {
            case R.id.action_a_propos:
                AlertDialog.Builder builder_apropos = new AlertDialog.Builder(CaisseActivity.this);
                builder_apropos.setTitle(R.string.string_a_propos);
                builder_apropos.setMessage(R.string.libelle_a_propos);
                builder_apropos.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //rien
                            }
                        });

                AlertDialog ad_apropos = builder_apropos.create();
                ad_apropos.show();
                break;
            case R.id.action_reinit:
                AlertDialog.Builder builder_reset = new AlertDialog.Builder(CaisseActivity.this);
                builder_reset.setTitle(R.string.action_reinit);
                builder_reset.setMessage(R.string.confirm_reinit);
                builder_reset.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //envoi de la transaction à la bdd
                                Article a = new Article();
                                a.setQuantite(1);
                                a.setIdProduit(19);
                                List<Article> ar = new ArrayList<Article>();
                                ar.add(a);
                                PanierCommande pcr = new PanierCommande();
                                pcr.setArticles(ar);
                                pcr.setMoyenPaiement(2);
                                pcr.setNbArticles(1);
                                Double montantTotal =-1*Double.parseDouble(panierDao.getData(String.valueOf(R.string.key_total)));
                                pcr.setPrixTotal(montantTotal);
                                Date date = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.FRANCE);
                                pcr.setDate(sdf.format(date));
                                panierDao.saveDataPanier(pcr);
                                panierDao.resetData(Double.toString(-1*montantTotal));
                            }
                        });
                builder_reset.setNegativeButton(R.string.annuler,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.dismiss();
                            }
                        });
                AlertDialog ad_reinit = builder_reset.create();
                ad_reinit.show();
                break;
            case R.id.action_boisson:
                produitsFragment.afficherProduitsBar();
                break;
            case R.id.action_snack:
                produitsFragment.afficherProduitsSnack();
                break;
            case R.id.action_caisse:
                AlertDialog.Builder builder_caisse = new AlertDialog.Builder(CaisseActivity.this);
                builder_caisse.setTitle(R.string.action_caisse)
                        .setItems(R.array.caisse_options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    case 0:
                                        // 1. Instantiate an AlertDialog.Builder with its constructor
                                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                                CaisseActivity.this);

                                        // 2. Chain together various setter methods to set the dialog
                                        // characteristics
                                        builder.setTitle("Ajout d'argent dans la caisse");
                                        builder.setView(getLayoutInflater().inflate(R.layout.popup,
                                                null));
                                        // Add action buttons
                                        // 3. Get the AlertDialog from create()
                                        builder.setPositiveButton(R.string.ok,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        handleMouvementRetrait(dialog, 18);
                                                    }
                                                });
                                        builder.setNegativeButton(R.string.annuler,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // User cancelled the dialog
                                                        dialog.dismiss();
                                                    }
                                                });
                                        AlertDialog ad = builder.create();
                                        ad.show();
                                        break;
                                    case 1:
                                        // 1. Instantiate an AlertDialog.Builder with its constructor
                                        AlertDialog.Builder builder_remove = new AlertDialog.Builder(
                                                CaisseActivity.this);

                                        // 2. Chain together various setter methods to set the dialog
                                        // characteristics
                                        builder_remove.setTitle("Retrait d'argent dans la caisse");
                                        builder_remove.setView(getLayoutInflater().inflate(R.layout.popup,
                                                null));
                                        // Add action buttons
                                        // 3. Get the AlertDialog from create()
                                        builder_remove.setPositiveButton(R.string.ok,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        handleMouvementRetrait(dialog, 19);
                                                    }
                                                });
                                        builder_remove.setNegativeButton(R.string.annuler,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // User cancelled the dialog
                                                        dialog.dismiss();
                                                    }
                                                });
                                        AlertDialog adremove = builder_remove.create();
                                        adremove.show();
                                        break;
                                    case 2:
                                        String totalCaisse = panierDao.getData(String.valueOf(R.string.key_total));
                                        if (totalCaisse == null) {
                                            totalCaisse = " Pas de valeurs ";
                                        }

                                        // 1. Instantiate an AlertDialog.Builder with its constructor
                                        AlertDialog.Builder builder_recap = new AlertDialog.Builder(
                                                CaisseActivity.this);

                                        // 2. Chain together various setter methods to set the dialog
                                        // characteristics
                                        builder_recap.setTitle("Récapitulatif mouvement de caisse");
                                        View v = getLayoutInflater().inflate(R.layout.popup_recap,
                                                null);
                                        builder_recap.setView(v);
                                        // Add action buttons
                                        // 3. Get the AlertDialog from create()
                                        builder_recap.setPositiveButton(R.string.ok,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // User clicked OK button
                                                        // get edit view data

                                                    }
                                                });
                                        AlertDialog adrecap = builder_recap.create();
                                        TextView vEspece = (TextView) v.findViewById(R.id.recap_total_espece);
                                        TextView vCaisse = (TextView) v.findViewById(R.id.recap_total_cb);
                                        TextView vCaisseTotalEspeceCB = (TextView) v.findViewById(R.id.recap_total_caisse_cb_espece);
                                        TextView vCaisseTotal = (TextView) v.findViewById(R.id.recap_total_caisse);

                                        vEspece.setText(panierDao.getData(String.valueOf(R.string.key_total_espece)));
                                        vCaisse.setText(panierDao.getData(String.valueOf(R.string.key_total_cb)));
                                        String totalCBEspece = Double.toString(Double.parseDouble(panierDao.getData(String.valueOf(R.string.key_total_cb)))+Double.parseDouble(panierDao.getData(String.valueOf(R.string.key_total_espece))));
                                        vCaisseTotalEspeceCB.setText(totalCBEspece);
                                        vCaisseTotal.setText(panierDao.getData(String.valueOf(R.string.key_total)));
                                        ListView listMouvements = (ListView) v.findViewById(R.id.recap_liste_mouvements);
                                        if(panierDao.getDataMouvement()!=null && panierDao.getDataMouvement().size()>0){
                                            ArrayAdapter<String> aa = new ArrayAdapter<String>(CaisseActivity.this,android.R.layout.simple_list_item_1,panierDao.getDataMouvement());
                                            listMouvements.setAdapter(aa);
                                            listMouvements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                public void onItemClick(AdapterView<?> parent, View v,
                                                                        int position, long id) {
                                                }
                                            });
                                        }
                                        adrecap.show();
                                        break;
                                }
                            }
                        });
                AlertDialog ad_caisse = builder_caisse.create();
                ad_caisse.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void handleMouvementRetrait(DialogInterface dialog, int type_mouvement){
        AlertDialog ad = (AlertDialog) dialog;
        EditText it = (EditText) ad.findViewById(R.id.money_value);
        EditText it_libelle = (EditText) ad.findViewById(R.id.libelle_mouvement);
        if (it.getText().toString().length() > 0) {
            Double montant = Double.parseDouble(it.getText()
                    .toString());
            if(type_mouvement ==19){
                montant = montant * -1;
            }
            //envoi de la transaction à la bdd
            Article a = new Article();
            a.setQuantite(1);
            a.setIdProduit(type_mouvement);
            List<Article> ar = new ArrayList<Article>();
            ar.add(a);
            PanierCommande pcr = new PanierCommande();
            pcr.setArticles(ar);
            pcr.setMoyenPaiement(2);
            pcr.setNbArticles(1);
            pcr.setPrixTotal(montant);
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.FRANCE);
            pcr.setDate(sdf.format(date));
            Retrait r = new Retrait();
            r.setMontant(montant);
            r.setLibelle(it_libelle.getText().toString());
            r.setDateMouvement(sdf.format(date));
            panierDao.saveDataMouvement(r);
            panierDao.saveDataPanier(pcr);
        }
    }

    @Override
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
