package fr.info.antillesinfov2.business.service;

import java.util.ArrayList;
import java.util.List;

import fr.info.antillesinfov2.business.model.News;
import fr.info.antillesinfov2.dao.model.Item;

public class TransformBeanUtilisateur {
	/**
	 * transformation d'un objet item en un objet news (permet de decorele les
	 * datas du back office par rapport au front en cas de changement de flux)
	 * 
	 * @param item
	 * @return
	 */
	private static News transformItemToNews(Item item) {
		News news = new News();
		if (item != null) {
			news.setCategory(item.getCategory());
			news.setDescription(item.getDescription());
			if (item.getEnclosure() != null) {
				news.setImageUrl(item.getEnclosure().getUrl());
			}
			news.setTitle(item.getTitle());
			// recuperation de la vue mobile
			String mobileLink = item.getLink();
			// utilisation de string utils plus tard
			if (mobileLink != null && mobileLink != "") {
				mobileLink = mobileLink.replace("www", "m");
			}
			news.setLink(mobileLink);
			return news;
		}
		return null;
	}

	/**
	 * renvoie les data du front
	 * 
	 * @param listItem
	 * @return
	 */
	public static List<News> listNews(List<Item> listItem) {
		List<News> listNews = new ArrayList<News>();
		for (Item item : listItem) {
			News news = transformItemToNews(item);
			if (news != null) {
				listNews.add(news);
			}
		}
		return listNews;
	}
}
