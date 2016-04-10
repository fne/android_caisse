package fr.info.antillesinfov2.business.service;

import java.util.List;

import fr.info.antillesinfov2.business.model.News;

public interface NewsManager {
	/**
	 * récupération des news en provenance du flux rss
	 * @param url
	 * @return
	 */
	public String getNewsChannel(String url);
	
	/**
	 * récupération des images
	 * @param url
	 * @return
	 */
	public String getImagesChannel(String url);
	
	/**
	 * Recuperation de l'ensemble des news a partir d un flux 
	 * @param rssSource
	 * @return
	 * @throws Exception
	 */
	public List<News> getListNews(String rssSource) throws Exception;
}
