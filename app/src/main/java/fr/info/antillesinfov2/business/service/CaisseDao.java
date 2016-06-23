package fr.info.antillesinfov2.business.service;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.Constant;
import fr.info.antillesinfov2.business.model.PanierCommande;
import fr.info.antillesinfov2.business.model.Produit;
import fr.info.antillesinfov2.business.model.Retrait;
import fr.info.antillesinfov2.library.HttpClientAntilles;

/**
 * Created by Frankito on 30/01/16.
 */
public class CaisseDao {

    private static final String URL_PANIER = Constant.URL_DNS + "/commandes";
    private HttpClientAntilles httpClient;
    private SharedPreferences sp;
    private CSVFileWriter csv;
    private static CaisseDao mInstance = null;
    private Activity activity;

    public CaisseDao(Activity a) {
        activity = a;
    }

    public static CaisseDao getInstance(Activity a) {
        if (mInstance == null) {
            mInstance = new CaisseDao(a);
        }
        return mInstance;
    }


    public void initData() {
        if (!sp.contains(String.valueOf(R.string.key_total_cb))) {
            saveData(String.valueOf(R.string.key_total_cb), "0.0");
        }
        if (!sp.contains(String.valueOf(R.string.key_total_espece))) {
            saveData(String.valueOf(R.string.key_total_espece), "0.0");
        }
    }

    public void sendOldData() {
        new HttpRequestTask().execute();
    }

    public void saveData(String key, String data) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, data);
        editor.commit();
    }

    public void updateTotalCaisse(Double montant, Boolean isAdd) {
        Double savedMontant = Double.parseDouble(sp.getString(String.valueOf(R.string.key_total), "0"));
        if (isAdd) {
            savedMontant = savedMontant + montant;
        } else {
            savedMontant = savedMontant - montant;
        }
        saveData(String.valueOf(R.string.key_total), savedMontant.toString());
    }

    public String getData(String key) {
        return sp.getString(key, null);
    }

    public void resetData(String montantTotal) {
        //create csv
        Date date = new Date();
        csv.writeCsvFile("/reinitCSV" + Long.toString(date.getTime()) + ".csv", getRetraits(), getData(String.valueOf(R.string.key_total_cb)), getData(String.valueOf(R.string.key_total_espece)), montantTotal);
        //clean data
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(String.valueOf(R.string.key_liste_mouvements));
        saveData(String.valueOf(R.string.key_total), "0.0");
        saveData(String.valueOf(R.string.key_total_cb), "0.0");
        saveData(String.valueOf(R.string.key_total_espece), "0.0");
        editor.commit();

    }

    private List<Retrait> getRetraits() {
        String panier = getData(String.valueOf(R.string.key_liste_mouvements));
        Type listType = new TypeToken<ArrayList<Retrait>>() {
        }.getType();
        return new Gson().fromJson(panier, listType);
    }

    public List<String> getDataMouvement() {
        if (sp.contains(String.valueOf(R.string.key_liste_mouvements))) {
            List<String> rss = new ArrayList<String>();
            for (Retrait r : getRetraits()) {
                rss.add(r.getDateMouvement() + " | " + r.getLibelle() + " : " + r.getMontant());
            }
            return rss;
        }
        return null;
    }

    public void saveDataMouvement(Retrait r) {
        Gson gson = new Gson();
        if (sp.contains(String.valueOf(R.string.key_liste_mouvements))) {
            String panier = getData(String.valueOf(R.string.key_liste_mouvements));
            Type listType = new TypeToken<ArrayList<Retrait>>() {
            }.getType();
            ArrayList<Retrait> rs = new Gson().fromJson(panier, listType);
            rs.add(r);
            saveData(String.valueOf(R.string.key_liste_mouvements), gson.toJson(rs));
        } else {
            ArrayList<Retrait> rs = new ArrayList<Retrait>();
            rs.add(r);
            saveData(String.valueOf(R.string.key_liste_mouvements), gson.toJson(rs));
        }
    }

    public void saveDataPanier(PanierCommande pc) {
        //sauvegarde au niveau de la base sinon sauvegarde au niveau de l'application
        //update montant total caisse
        pc.setSessionId(getData(String.valueOf(R.string.key_id_session)));
        pc.setCaisseId(getData(String.valueOf(R.string.key_id_caisse)));
        updateTotalCaisse(pc.getPrixTotal(), true);
        new HttpRequestTask().execute(pc);
    }

    public void saveMouvement(Retrait r) {

    }

    public void saveMouvementPaiement(Integer typePaiement, Double total) {
        if (typePaiement == 1) {
            if (sp.contains(String.valueOf(R.string.key_total_cb))) {
                Double total_cb = Double.parseDouble(getData(String.valueOf(R.string.key_total_cb))) + total;
                saveData(String.valueOf(R.string.key_total_cb), total_cb.toString());
            } else {
                saveData(String.valueOf(R.string.key_total_cb), total.toString());
            }
        } else {
            if (sp.contains(String.valueOf(R.string.key_total_espece))) {
                Double total_espece = Double.parseDouble(getData(String.valueOf(R.string.key_total_espece))) + total;
                saveData(String.valueOf(R.string.key_total_espece), (total_espece).toString());
            } else {
                saveData(String.valueOf(R.string.key_total_espece), total.toString());
            }

        }
    }

    /**
     * Tache permettant de recuperer les infos via un flux et de completer le
     * layout
     *
     * @author NEBLAI
     */
    protected class HttpRequestTask extends AsyncTask<PanierCommande, Integer, String> {

        @Override
        protected String doInBackground(PanierCommande... params) {
            httpClient = new HttpClientAntilles();
            Gson gson = new Gson();
            if (params.length != 0) {
                Log.i(this.getClass().getName(), "panier non vide");
                try {
                    //transformation panier de commande
                    List<PanierCommande> pcs = new ArrayList<PanierCommande>();
                    //recuperation du tableau
                    pcs.add(params[0]);
                    httpClient.post(URL_PANIER, gson.toJson(pcs));
                } catch (IOException e) {
                    e.printStackTrace();
                    //sauvegarde du panier
                    if (sp.contains(String.valueOf(R.string.key_panier))) {
                        String panier = getData(String.valueOf(R.string.key_panier));
                        Type listType = new TypeToken<ArrayList<PanierCommande>>() {
                        }.getType();
                        ArrayList<PanierCommande> pcs = new Gson().fromJson(panier, listType);
                        pcs.add(params[0]);
                        saveData(String.valueOf(R.string.key_panier), gson.toJson(pcs));
                    } else {
                        ArrayList<PanierCommande> pcs = new ArrayList<PanierCommande>();
                        pcs.add(params[0]);
                        saveData(String.valueOf(R.string.key_panier), gson.toJson(pcs));
                    }
                }
            } else if (sp.contains(String.valueOf(R.string.key_panier))) {
                Log.i(this.getClass().getName(), "envoi du panier sauvegardé");
                try {

                    HttpResponse response = httpClient.post(URL_PANIER, getData(String.valueOf(R.string.key_panier)));
                    if (response.getStatusLine().getStatusCode() == 201) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove(String.valueOf(R.string.key_panier));
                        editor.commit();
                        Log.i(getClass().getName(), "test");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ;
            }
            return "";
        }

        @Override
        protected void onPostExecute(String produitsReponse) {
            Log.i(this.getClass().getName(), produitsReponse);
        }

    }

    public SharedPreferences getSp() {
        return sp;
    }

    public void setSp(SharedPreferences sp) {
        this.sp = sp;
    }

    public CSVFileWriter getCsv() {
        return csv;
    }

    public void setCsv(CSVFileWriter csv) {
        this.csv = csv;
    }
}
