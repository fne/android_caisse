package fr.info.antillesinfov2.business.service.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.model.DetailPanierCommande;

public class PanierCommandeAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<DetailPanierCommande> listProduits;

    public PanierCommandeAdapter(Context context, List<DetailPanierCommande> listProduits) {
        super();
        this.layoutInflater = LayoutInflater.from(context);
        this.listProduits = listProduits;
    }

    /**
     * récupérer un item de la liste en fonction de sa position
     *
     * @param position - Position de l'item à récupérer
     * @return l'item récupéré
     */
    public Object getItem(int position) {
        return listProduits.get(position);
    }

    /**
     * Récupérer l'identifiant d'un item de la liste en fonction de sa position
     * (plutôt utilisé dans le cas d'une base de données, mais on va l'utiliser
     * aussi)
     *
     * @param position - Position de l'item à récupérer
     * @return l'identifiant de l'item
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * Explication juste en dessous.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        DetailPanierCommande dpc = (DetailPanierCommande) getItem(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(
                    R.layout.simple_list_commande_view, null);
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.commande_date);
            holder.nbArticles = (TextView) convertView
                    .findViewById(R.id.commande_nb_article);
            holder.total = (TextView) convertView
                    .findViewById(R.id.commande_total);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.date.setText(dpc.getDate());
        holder.nbArticles.setText(Integer.toString(dpc.getNbArticles()));
        holder.total.setText(Double.toString(dpc.getPrixTotal()));
        return convertView;
    }

    @Override
    public int getCount() {
        return listProduits.size();
    }

    /**
     * classe contenant les vues procédé moins couteux en matiere de generation
     * des vues
     *
     * @author NEBLAI
     */
    static class ViewHolder {
        public TextView date;
        public TextView total;
        public TextView nbArticles;
    }
}
