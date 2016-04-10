package fr.info.antillesinfov2.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.model.Article;
import fr.info.antillesinfov2.business.model.Produit;
import fr.info.antillesinfov2.business.service.android.ArticleAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PanierFragment.OnFragmentPanierInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PanierFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PanierFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView listView;
    private List<Produit> produits = new ArrayList<Produit>();
    private ArticleAdapter aa;
    private OnFragmentPanierInteractionListener mListener;

    public PanierFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResumeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PanierFragment newInstance(String param1, String param2) {
        PanierFragment fragment = new PanierFragment();
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
        View v = inflater.inflate(R.layout.fragment_panier, container, false);
        listView = (ListView) v.findViewById(R.id.produitsRecapitulatifs);
        aa = new ArticleAdapter(getActivity(),produits);
        listView.setAdapter(aa);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                produits.remove(position);
                aa.notifyDataSetChanged();
                mListener.onFragmentPanierInteraction(position);
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (OnFragmentPanierInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnProduitsFragmentInteractionListener");
        }
    }

    /**
     * suppression de tous les éléments du panier
     */
    public void viderPanier(){
        produits.clear();
        aa.notifyDataSetChanged();
    }

    /**
     * rajout d'un élément au panier
     * @param p
     */
    public void updatePanierView(Produit p){
        produits.add(p);
        aa.notifyDataSetChanged();
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
    public interface OnFragmentPanierInteractionListener {
        void onFragmentPanierInteraction(Integer indexProduit);
    }
}
