package fr.info.antillesinfov2.business.service.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.model.Article;
import fr.info.antillesinfov2.business.model.Produit;

public class ArticleAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;
	private List<Produit> articles;

	public ArticleAdapter(Context context, List<Produit> articles) {
		super();
		this.layoutInflater = LayoutInflater.from(context);
		this.articles = articles;
	}

	/**
	 * récupérer un item de la liste en fonction de sa position
	 * 
	 * @param position
	 *            - Position de l'item à récupérer
	 * @return l'item récupéré
	 */
	public Object getItem(int position) {
		return articles.get(position);
	}

	/**
	 * Récupérer l'identifiant d'un item de la liste en fonction de sa position
	 * (plutôt utilisé dans le cas d'une base de données, mais on va l'utiliser
	 * aussi)
	 * 
	 * @param position
	 *            - Position de l'item à récupérer
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
		if (convertView == null) {
			convertView = layoutInflater.inflate(
					R.layout.simple_list_articles_view, null);
			holder = new ViewHolder();
			holder.nom_article= (TextView) convertView.findViewById(R.id.nom_article);
			holder.prix_article = (TextView) convertView
					.findViewById(R.id.prix_article);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Produit p = (Produit) getItem(position);

		holder.nom_article.setText(p.getProductName());
		holder.prix_article.setText(Double.toString(p.getProductPrice()));

		return convertView;
	}

	@Override
	public int getCount() {
		return articles.size();
	}

	/**
	 * classe contenant les vues procédé moins couteux en matiere de generation
	 * des vues
	 * 
	 * @author NEBLAI
	 * 
	 */
	static class ViewHolder {
		public TextView nom_article;
		public TextView prix_article;
	}
}
