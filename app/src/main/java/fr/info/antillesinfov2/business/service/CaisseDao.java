package fr.info.antillesinfov2.business.service;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Response;


import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.Constant;
import fr.info.antillesinfov2.business.model.DetailPanierCommande;
import fr.info.antillesinfov2.business.model.PanierCommande;
import fr.info.antillesinfov2.business.model.Retrait;
import fr.info.antillesinfov2.library.HttpClient;

/**
 * Created by Frankito on 30/01/16.
 */
public class CaisseDao {

    private static final String URL_PANIER = Constant.URL_DNS + "/commandes";
    private HttpClient mHttpClient;
    private SharedPreferences sp;
    private FileWriter fileWriter;
    private static CaisseDao mInstance = null;
    private List<DetailPanierCommande> detailPanierCommandes;


    private Activity activity;

    public CaisseDao(Activity a) {
        activity = a;
        detailPanierCommandes = new ArrayList<DetailPanierCommande>();
    }

    public static CaisseDao getInstance(Activity a) {
        if (mInstance == null) {
            mInstance = new CaisseDao(a);
        }
        return mInstance;
    }


    public void initData() {
        if (!sp.contains(activity.getString(R.string.key_total_cb))) {
            saveData(activity.getString(R.string.key_total_cb), "0.0");
        }
        if (!sp.contains(activity.getString(R.string.key_total_espece))) {
            saveData(activity.getString(R.string.key_total_espece), "0.0");
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
        Double savedMontant = Double.parseDouble(sp.getString(activity.getString(R.string.key_total), "0"));
        if (isAdd) {
            savedMontant = savedMontant + montant;
        } else {
            savedMontant = savedMontant - montant;
        }
        saveData(activity.getString(R.string.key_total), savedMontant.toString());
    }

    public String getData(String key) {
        return sp.getString(key, null);
    }

    public void resetData(String montantTotal) {
        //create fileWriter
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
        fileWriter.writeCsvFile("/reinitCSV" + sdf.format(date) + Long.toString(date.getTime()) + ".csv", getRetraits(), getData(activity.getString(R.string.key_total_cb)), getData(activity.getString(R.string.key_total_espece)), montantTotal);
        writeData();
        MediaScannerConnection.scanFile(activity, new String[]{Environment.getExternalStoragePublicDirectory("csv").getAbsolutePath()}, null, null);
        //clean data
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(activity.getString(R.string.key_liste_mouvements));
        editor.remove(activity.getString(R.string.key_detail_panier));
        saveData(activity.getString(R.string.key_total), "0.0");
        saveData(activity.getString(R.string.key_total_cb), "0.0");
        saveData(activity.getString(R.string.key_total_espece), "0.0");
        editor.commit();
    }

    public void writeData() {
        //create fileWriter
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.FRANCE);
        fileWriter.writeJsonFile("/json" + sdf.format(date) + ".json", getData(activity.getString(R.string.key_panier)));
    }

    private List<Retrait> getRetraits() {
        String panier = getData(activity.getString(R.string.key_liste_mouvements));
        Type listType = new TypeToken<ArrayList<Retrait>>() {
        }.getType();
        return new Gson().fromJson(panier, listType);
    }

    public List<String> getDataMouvement() {
        if (sp.contains(activity.getString(R.string.key_liste_mouvements))) {
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
        if (sp.contains(activity.getString(R.string.key_liste_mouvements))) {
            String panier = getData(activity.getString(R.string.key_liste_mouvements));
            Type listType = new TypeToken<ArrayList<Retrait>>() {
            }.getType();
            ArrayList<Retrait> rs = new Gson().fromJson(panier, listType);
            rs.add(r);
            saveData(activity.getString(R.string.key_liste_mouvements), gson.toJson(rs));
        } else {
            ArrayList<Retrait> rs = new ArrayList<Retrait>();
            rs.add(r);
            saveData(activity.getString(R.string.key_liste_mouvements), gson.toJson(rs));
        }
    }

    public void saveDataPanier(PanierCommande pc) {
        //sauvegarde au niveau de la base sinon sauvegarde au niveau de l'application
        //update montant total caisse
        pc.setSessionId(getData(activity.getString(R.string.key_id_session)));
        pc.setCaisseId(getData(activity.getString(R.string.key_id_caisse)));
        updateTotalCaisse(pc.getPrixTotal(), true);
        new HttpRequestTask().execute(pc);
    }


    public void saveMouvementPaiement(String typePaiement, Double total) {
        if (typePaiement == "CB") {
            if (sp.contains(activity.getString(R.string.key_total_cb))) {
                Double total_cb = Double.parseDouble(getData(activity.getString(R.string.key_total_cb))) + total;
                saveData(activity.getString(R.string.key_total_cb), total_cb.toString());
            } else {
                saveData(activity.getString(R.string.key_total_cb), total.toString());
            }
        } else {
            if (sp.contains(activity.getString(R.string.key_total_espece))) {
                Double total_espece = Double.parseDouble(getData(activity.getString(R.string.key_total_espece))) + total;
                saveData(activity.getString(R.string.key_total_espece), (total_espece).toString());
            } else {
                saveData(activity.getString(R.string.key_total_espece), total.toString());
            }

        }
    }

    public SharedPreferences getSp() {
        return sp;
    }

    public void setSp(SharedPreferences sp) {
        this.sp = sp;
    }

    public FileWriter getFileWriter() {
        return fileWriter;
    }

    public void setFileWriter(FileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }

    public List<DetailPanierCommande> getDetailPanierCommandes() {
        return detailPanierCommandes;
    }

    public void setDetailPanierCommandes(List<DetailPanierCommande> detailPanierCommandes) {
        this.detailPanierCommandes = detailPanierCommandes;
    }

    public void saveDetailPanier(DetailPanierCommande dpc) {
        Gson gson = new Gson();
        getDetailPanierCommandes().add(dpc);
        saveData(activity.getString(R.string.key_detail_panier),gson.toJson(getDetailPanierCommandes()));
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
            mHttpClient = HttpClient.getInstance();
            Gson gson = new Gson();
            if (params.length != 0) {
                Log.i(this.getClass().getName(), "panier non vide");
                try {
                    //transformation panier de commande
                    List<PanierCommande> pcs = new ArrayList<PanierCommande>();
                    //recuperation du tableau
                    pcs.add(params[0]);
                    mHttpClient.post(URL_PANIER, gson.toJson(pcs));
                } catch (IOException e) {
                    e.printStackTrace();
                    //sauvegarde du panier
                    if (sp.contains(activity.getString(R.string.key_panier))) {
                        String panier = getData(activity.getString(R.string.key_panier));
                        Type listType = new TypeToken<ArrayList<PanierCommande>>() {
                        }.getType();
                        ArrayList<PanierCommande> pcs = new Gson().fromJson(panier, listType);
                        pcs.add(params[0]);
                        saveData(activity.getString(R.string.key_panier), gson.toJson(pcs));
                    } else {
                        ArrayList<PanierCommande> pcs = new ArrayList<PanierCommande>();
                        pcs.add(params[0]);
                        saveData(activity.getString(R.string.key_panier), gson.toJson(pcs));
                    }
                }
            } else if (sp.contains(activity.getString(R.string.key_panier))) {
                Log.i(this.getClass().getName(), "envoi du panier sauvegardé");
                try {
                    Response response = mHttpClient.post(URL_PANIER, getData(activity.getString(R.string.key_panier)));
                    if (response.isSuccessful()) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove(activity.getString(R.string.key_panier));
                        editor.commit();
                    }
                        Log.i(getClass().getName(), "test");
                } catch (Exception e) {
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


}
