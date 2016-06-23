package fr.info.antillesinfov2.business.service.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.model.Produit;

public class ProduitAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<Produit> listProduits;

    public ProduitAdapter(Context context, List<Produit> listProduits) {
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
        Produit p = (Produit) getItem(position);
        if (convertView == null) {
            if(p.getProductImage()!=null && p.getProductImage().length()>0){
                convertView = layoutInflater.inflate(
                        R.layout.simple_list_button_view, null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.img_produit);
                convertView.setTag(holder);
            }else{
                convertView = layoutInflater.inflate(
                        R.layout.simple_list_produits_no_image_view, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView
                        .findViewById(R.id.nom_produit);
                convertView.setTag(holder);
            }

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        //holder.image.setImageResource(Integer.decode(p.getProductImage()));
        //holder.image.setImage()
        /*InputStream is = ClassLoader
                .getSystemResourceAsStream("res/drawable/"+p.getProductImage());
        Bitmap bm = BitmapFactory.decodeStream(is);*/
        if(holder.image != null && p.getProductImage()!= null && p.getProductImage().length()>0 && Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/image_caisse/"+p.getProductImage())!= null){
            //holder.image.setImageResource(layoutInflater.getContext().getResources().getIdentifier("fr.info.antillesinfov2:drawable/"+p.getProductImage(), null, null));
            holder.image.setImageURI(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/image_caisse/"+p.getProductImage()));

        }else{
            holder.text.setText(p.getProductName());
        }
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
        public ImageView image;
        public TextView text;
    }
}
