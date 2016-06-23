package fr.info.antillesinfov2.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.Constant;
import fr.info.antillesinfov2.business.model.Produit;
import fr.info.antillesinfov2.business.service.android.ProduitAdapter;
import fr.info.antillesinfov2.library.HttpClientAntilles;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProduitsFragment.OnProduitsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProduitsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProduitsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String URL_PRODUITS = Constant.URL_DNS + "/produits";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    List<Produit> produits;
    List<Produit> produitsBar = new ArrayList<Produit>();
    List<Produit> produitsConsigne = new ArrayList<Produit>();
    List<Produit> produitsSnack = new ArrayList<Produit>();
    private GridView gridView;

    private OnProduitsFragmentInteractionListener mListener;

    public ProduitsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProduitsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProduitsFragment newInstance(String param1, String param2) {
        ProduitsFragment fragment = new ProduitsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_produits, container, false);
        gridView = (GridView) v.findViewById(R.id.gridView1);
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if ((networkInfo != null) && networkInfo.isConnected()) {
            new HttpRequestTask().execute(URL_PRODUITS);
        } else {
            Log.i(getTag(), "wifi déconnecté");
            setProduitsFromCSV();
            afficherProduitsBar();
        }
        Button addConsigneButton = (Button) v.findViewById(R.id.addConsigne);
        addConsigneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("CaisseActivity", "add consigne");
                mListener.onProduitsFragmentInteraction(produitsConsigne.get(0));
            }
        });
        Button removeConsigneButton = (Button) v.findViewById(R.id.removeConsigne);
        removeConsigneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("CaisseActivity", "remove consigne");
                mListener.onProduitsFragmentInteraction(produitsConsigne.get(1));
            }
        });
        return v;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnProduitsFragmentInteractionListener {
        void onProduitsFragmentInteraction(Produit p);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (OnProduitsFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProduitsFragmentInteractionListener");
        }
    }

    public void setProduitsCategorie() {
        for (Produit p : produits) {
            if (p.getCategory() == 1) {
                produitsBar.add(p);
            }
            if (p.getCategory() == 2) {
                produitsSnack.add(p);
            }
            if (p.getCategory() == 3) {
                produitsConsigne.add(p);
            }
        }
    }

    public void setProduitsFromCSV() {
        String csvFileToRead = "csv/product.csv";
        BufferedReader br = null;
        String line = "";
        String splitBy = ";";
        try {
            produits = new ArrayList<Produit>();
            String baseDir = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
            br = new BufferedReader(new FileReader(baseDir + "/"
                    + csvFileToRead));
            while ((line = br.readLine()) != null) {
                String[] p_a = line.split(splitBy);
                Produit p = new Produit();
                p.setProductCode(p_a[0]);
                p.setProductImage(p_a[1]);
                p.setProductName(p_a[2]);
                p.setProductPrice(Double.parseDouble(p_a[3]));
                p.setCategory(Integer.parseInt(p_a[4]));
                produits.add(p);
            }
            setProduitsCategorie();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * build du fragment
     *
     * @param produitsReponse
     */
    public void buildProduitsFragment(String produitsReponse) {
        try {
            Type listType = new TypeToken<ArrayList<Produit>>() {
            }.getType();
            produits = new Gson().fromJson(produitsReponse, listType);
            setProduitsCategorie();
            ProduitAdapter pa = new ProduitAdapter(getActivity(), produitsBar);
            gridView.setAdapter(pa);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    mListener.onProduitsFragmentInteraction(produitsBar.get(position));
                }
            });
        } catch (Exception e) {
            Log.i("mainActivity", "no news");
            e.printStackTrace();
        } finally {

        }
    }

    public void afficherProduitsBar() {
        ProduitAdapter pa = new ProduitAdapter(getActivity(), produitsBar);
        gridView.setAdapter(pa);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                mListener.onProduitsFragmentInteraction(produitsBar.get(position));
            }
        });
    }

    public void afficherProduitsSnack() {
        ProduitAdapter pa = new ProduitAdapter(getActivity(), produitsSnack);
        gridView.setAdapter(pa);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                mListener.onProduitsFragmentInteraction(produitsSnack.get(position));
            }
        });
    }

    /**
     * Tache permettant de recuperer les infos via un flux et de completer le
     * layout
     *
     * @author NEBLAI
     */
    protected class HttpRequestTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String reponse = null;
            try {
                reponse = new HttpClientAntilles().readContentsOfUrl(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialog.show(getActivity(), null, getString(R.string.loading));
        }

        @Override
        protected void onPostExecute(String produitsReponse) {
            if (produitsReponse != null) {
                buildProduitsFragment(produitsReponse);
            } else {
                Log.i(getTag(), "Erreur lors de la récupération des datas");
                setProduitsFromCSV();
                afficherProduitsBar();
            }
        }
    }
}
