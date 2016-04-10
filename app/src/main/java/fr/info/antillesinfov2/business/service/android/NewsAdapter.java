package fr.info.antillesinfov2.business.service.android;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import fr.info.antillesinfov2.R;
import fr.info.antillesinfov2.business.model.News;

public class NewsAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;
	private List<News> listNews;

	public NewsAdapter(Context context, List<News> listNews) {
		super();
		this.layoutInflater = LayoutInflater.from(context);
		this.listNews = listNews;
	}

	/**
	 * récupérer un item de la liste en fonction de sa position
	 * 
	 * @param position
	 *            - Position de l'item à récupérer
	 * @return l'item récupéré
	 */
	public Object getItem(int position) {
		return listNews.get(position);
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
					R.layout.simple_list_antille_info_view, null);
			holder = new ViewHolder();
			holder.titre = (TextView) convertView.findViewById(R.id.titre);
			holder.category = (TextView) convertView
					.findViewById(R.id.category);
			holder.image = (ImageView) convertView.findViewById(R.id.img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		News newsItem = (News) getItem(position);

		holder.titre.setText(newsItem.getTitle());
		holder.category.setText(newsItem.getCategory());

		if (holder.image != null && newsItem.getImageUrl()!= null) {			
			UrlImageViewHelper.setUrlDrawable(holder.image,
					newsItem.getImageUrl());
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return listNews.size();
	}

	/**
	 * classe contenant les vues procédé moins couteux en matiere de generation
	 * des vues
	 * 
	 * @author NEBLAI
	 * 
	 */
	static class ViewHolder {
		public TextView titre;
		public TextView category;
		public ImageView image;
	}
}
