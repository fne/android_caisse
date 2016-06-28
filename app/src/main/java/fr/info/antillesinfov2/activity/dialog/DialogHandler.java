package fr.info.antillesinfov2.activity.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.Constant;
import fr.info.antillesinfov2.business.model.Article;
import fr.info.antillesinfov2.business.model.PanierCommande;
import fr.info.antillesinfov2.business.model.Retrait;
import fr.info.antillesinfov2.business.service.CaisseDao;

/**
 * Created by QJRS8151 on 22/06/2016.
 */
public class DialogHandler {

    private Activity activity;
    private CaisseDao caisseDao;

    public DialogHandler(Activity a) {
        activity = a;
        caisseDao = CaisseDao.getInstance(a);
    }

    public void showDialogConfig() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(
                activity);

        // 2. Chain together various setter methods to set the dialog
        // characteristics
        builder.setTitle(activity.getString(R.string.string_config));
        final Boolean isSessionSet = caisseDao.getData(activity.getString(R.string.key_id_session)) != null;
        builder.setView(activity.getLayoutInflater().inflate(R.layout.popup_config_all,
                null));

        // Add action buttons
        // 3. Get the AlertDialog from create()
        builder.setPositiveButton(R.string.ok, null);
        final AlertDialog ad_config = builder.create();
        ad_config.setCancelable(false);
        ad_config.show();
        final EditText it_caisse = (EditText) ad_config.findViewById(R.id.id_caisse);
        final EditText it_session = (EditText) ad_config.findViewById(R.id.id_session);
        it_caisse.setText(caisseDao.getData(activity.getString(R.string.key_id_caisse)));
        it_session.setText(caisseDao.getData(activity.getString(R.string.key_id_session)));
        ad_config.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(it_caisse.getText().toString()) && !TextUtils.isEmpty(it_session.getText().toString())) {
                            handleConfig(it_caisse.getText().toString(), it_session.getText().toString());
                            activity.getActionBar().setTitle(it_caisse.getText().toString());
                            ad_config.dismiss();
                        } else {
                            Toast.makeText(activity, activity.getString(R.string.string_error_config), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void showDialogReinitCaisse() {
        AlertDialog.Builder builder_reset = new AlertDialog.Builder(activity);
        builder_reset.setTitle(R.string.action_reinit);
        builder_reset.setMessage(R.string.confirm_reinit);
        builder_reset.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //envoi de la transaction à la bdd
                        Article a = new Article();
                        a.setQuantite(1);
                        a.setIdProduit(Constant.ACTION_RETRAIT);
                        List<Article> ar = new ArrayList<Article>();
                        ar.add(a);
                        PanierCommande pcr = new PanierCommande();
                        pcr.setArticles(ar);
                        pcr.setMoyenPaiement("ESP");
                        pcr.setNbArticles(1);
                        Double montantTotal = -1 * Double.parseDouble(caisseDao.getData(activity.getString(R.string.key_total)));
                        a.setPrice(montantTotal);
                        pcr.setPrixTotal(montantTotal);
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.FRANCE);
                        pcr.setDate(sdf.format(date));
                        caisseDao.saveDataPanier(pcr);
                        caisseDao.resetData(Double.toString(-1 * montantTotal));
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
    }

    public void showDialogMenuCaisse() {
        AlertDialog.Builder builder_caisse = new AlertDialog.Builder(activity);
        builder_caisse.setTitle(R.string.action_caisse)
                .setItems(R.array.caisse_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                // 1. Instantiate an AlertDialog.Builder with its constructor
                                AlertDialog.Builder builder = new AlertDialog.Builder(
                                        activity);

                                // 2. Chain together various setter methods to set the dialog
                                // characteristics
                                builder.setTitle("Ajout d'argent dans la caisse");
                                builder.setView(activity.getLayoutInflater().inflate(R.layout.popup,
                                        null));
                                // Add action buttons
                                // 3. Get the AlertDialog from create()
                                builder.setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                handleMouvementRetrait(dialog, Constant.ACTION_DEPOT);
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
                                        activity);

                                // 2. Chain together various setter methods to set the dialog
                                // characteristics
                                builder_remove.setTitle("Retrait d'argent dans la caisse");
                                builder_remove.setView(activity.getLayoutInflater().inflate(R.layout.popup,
                                        null));
                                // Add action buttons
                                // 3. Get the AlertDialog from create()
                                builder_remove.setPositiveButton(R.string.ok,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                handleMouvementRetrait(dialog, Constant.ACTION_RETRAIT);
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
                                String totalCaisse = caisseDao.getData(activity.getString(R.string.key_total));
                                if (totalCaisse == null) {
                                    totalCaisse = " Pas de valeurs ";
                                }

                                // 1. Instantiate an AlertDialog.Builder with its constructor
                                AlertDialog.Builder builder_recap = new AlertDialog.Builder(
                                        activity);

                                // 2. Chain together various setter methods to set the dialog
                                // characteristics
                                builder_recap.setTitle("Récapitulatif mouvement de caisse");
                                View v = activity.getLayoutInflater().inflate(R.layout.popup_recap,
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

                                vEspece.setText(caisseDao.getData(activity.getString(R.string.key_total_espece)));
                                vCaisse.setText(caisseDao.getData(activity.getString(R.string.key_total_cb)));
                                String totalCBEspece = Double.toString(Double.parseDouble(caisseDao.getData(activity.getString(R.string.key_total_cb))) + Double.parseDouble(caisseDao.getData(activity.getString(R.string.key_total_espece))));
                                vCaisseTotalEspeceCB.setText(totalCBEspece);
                                vCaisseTotal.setText(caisseDao.getData(activity.getString(R.string.key_total)));
                                ListView listMouvements = (ListView) v.findViewById(R.id.recap_liste_mouvements);
                                if (caisseDao.getDataMouvement() != null && caisseDao.getDataMouvement().size() > 0) {
                                    ArrayAdapter<String> aa = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, caisseDao.getDataMouvement());
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
    }

    /**
     * affichage des informations sur l'application
     */
    public void showDialogAppInfo() {
        AlertDialog.Builder builder_apropos = new AlertDialog.Builder(activity);
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
    }

    /**
     * enregistrement des informations sessionId et caisseId de l'utilisateur
     */
    public void handleConfig(String idCaisse, String idSession) {
        caisseDao.saveData(activity.getString(R.string.key_id_session), idSession);
        caisseDao.saveData(activity.getString(R.string.key_id_caisse), idCaisse);
    }

    /**
     * gestion des mouvements de retrait ou d'ajout de liquide
     *
     * @param dialog
     * @param type_mouvement
     */
    public void handleMouvementRetrait(DialogInterface dialog, String type_mouvement) {
        AlertDialog ad = (AlertDialog) dialog;
        EditText it = (EditText) ad.findViewById(R.id.money_value);
        EditText it_libelle = (EditText) ad.findViewById(R.id.libelle_mouvement);
        if (it.getText().toString().length() > 0) {
            Double montant = Double.parseDouble(it.getText()
                    .toString());
            if (type_mouvement == Constant.ACTION_RETRAIT) {
                montant = montant * -1;
            }
            //envoi de la transaction à la bdd
            Article a = new Article();
            a.setQuantite(1);
            a.setIdProduit(type_mouvement);
            a.setPrice(montant);
            List<Article> ar = new ArrayList<Article>();
            ar.add(a);
            PanierCommande pcr = new PanierCommande();
            pcr.setArticles(ar);
            pcr.setMoyenPaiement("ESP");
            pcr.setNbArticles(1);
            pcr.setPrixTotal(montant);
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.FRANCE);
            pcr.setDate(sdf.format(date));
            Retrait r = new Retrait();
            r.setMontant(montant);
            r.setLibelle(it_libelle.getText().toString());
            SimpleDateFormat sdfr = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.FRANCE);
            r.setDateMouvement(sdfr.format(date));
            caisseDao.saveDataMouvement(r);
            caisseDao.saveDataPanier(pcr);
        }
    }
}
