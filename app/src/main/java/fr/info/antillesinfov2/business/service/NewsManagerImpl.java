package fr.info.antillesinfov2.business.service;

import java.io.IOException;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import fr.info.antillesinfov2.business.model.News;
import fr.info.antillesinfov2.dao.DaoNews;
import fr.info.antillesinfov2.dao.model.RSS;

public class NewsManagerImpl implements NewsManager {
	
	private DaoNews daoNews;
	
	@Override
	public String getNewsChannel(String url) {
		daoNews = new DaoNews();
		String rssSource = null;
		try {
			rssSource = daoNews.readContentsOfUrl(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rssSource;
	}
	
	public List<News> getListNews(String rssSource) throws Exception{
		//serialisation de l objet
		Serializer serializer = new Persister();
		RSS rssObject = serializer.read(RSS.class, rssSource);
		return TransformBeanUtilisateur.listNews(rssObject.getChannel().getItem()); 
	}

	@Override
	public String getImagesChannel(String url) {
		daoNews = new DaoNews();
		String imageSource = null;
		try {
			imageSource = daoNews.readContentsOfUrl(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageSource;
	}

}
